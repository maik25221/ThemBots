package dev.maiki.thembots.domain.service;

import dev.maiki.thembots.domain.model.*;
import dev.maiki.thembots.domain.model.accion.Accion;
import dev.maiki.thembots.domain.model.accion.AccionDisparar;
import dev.maiki.thembots.domain.model.accion.AccionGirar;
import dev.maiki.thembots.domain.model.accion.AccionMover;
import dev.maiki.thembots.domain.model.enums.TipoEvento;
import dev.maiki.thembots.domain.percepcion.ContextoPercepcion;
import dev.maiki.thembots.domain.value.Posicion;

import java.util.*;

/**
 * Servicio de dominio que gestiona el ciclo completo de una simulación de combate.
 * Ejecuta cada tick: toma decisiones, aplica acciones, registra eventos y evalúa condiciones de finalización.
 */
public class SimuladorCombate {

    private final Arena arena;
    private final int tickMaximo;
    private final Map<Integer, List<EventoDeCombate>> eventosPorTick = new LinkedHashMap<>();
    private final FabricaContexto fabricaContexto = new FabricaContexto();
    private final MotorDeColisiones motorColisiones = new MotorDeColisiones();
    private final MotorDeProyectiles motorProyectiles = new MotorDeProyectiles();
    private final AcumuladorEstadisticas acumulador = new AcumuladorEstadisticas();
    private int tickActual = 0;
    private boolean terminado = false;
    private ResultadoCombate resultado;

    public SimuladorCombate(Arena arena, int tickMaximo) {
        this.arena = arena;
        this.tickMaximo = tickMaximo;
        this.acumulador.inicializar(arena.getRobots());
    }

    /**
     * Ejecuta la simulación completa hasta que finaliza por condición natural o límite de ticks.
     */
    public void simularHastaElFinal() {
        while (!estaTerminado()) {
            simularTick();
        }
        this.resultado = calcularResultadoFinal();
    }

    /**
     * Ejecuta un solo tick de la simulación.
     */
    public void simularTick() {
        if (estaTerminado()) return;

        List<EventoDeCombate> eventos = new ArrayList<>();
        eventos.add(new EventoSimple(tickActual, TipoEvento.TICK_INICIADO));

        acumulador.registrarTick(arena.getRobots());

        // 1. Decidir acciones
        Map<UUID, Accion> decisiones = new HashMap<>();
        for (Robot robot : arena.getRobots()) {
            if (!robot.estaActivo()) continue;
            ContextoPercepcion contexto = fabricaContexto.generar(robot.getId(), tickActual, arena);
            Accion accion = robot.getComportamiento().decidir(contexto);
            decisiones.put(robot.getId(), accion);
        }

        // 2. Aplicar acciones
        for (Robot robot : arena.getRobots()) {
            if (!robot.estaActivo()) continue;
            Accion accion = decisiones.get(robot.getId());
            switch (accion.tipo()) {
                case MOVER -> {
                    AccionMover a = (AccionMover) accion;
                    Posicion destino = robot.getPosicion().avanzar(robot.getDireccionVector().escalar(a.getDistancia()));
                    if (!arena.dentroDeLimites(destino)) continue;
                    var otrosRobots = arena.getRobots().stream()
                        .filter(r -> !r.getId().equals(robot.getId()))
                        .toList();
                    if (motorColisiones.hayColision(destino, robot.getRadio(), otrosRobots, arena.getObstaculos()))
                        continue;
                    robot.setPosicion(destino);
                    eventos.add(new EventoSimple(tickActual, TipoEvento.MOVIMIENTO_REALIZADO));
                }
                case GIRAR -> {
                    AccionGirar a = (AccionGirar) accion;
                    robot.setDireccion(robot.getDireccion() + a.getAngulo());
                    eventos.add(new EventoSimple(tickActual, TipoEvento.GIRO_REALIZADO));
                }
                case DISPARAR -> {
                    if (robot.getCooldown() > 0) continue;
                    AccionDisparar a = (AccionDisparar) accion;
                    Proyectil p = new Proyectil(UUID.randomUUID(), robot.getPosicion(), robot.getDireccionVector(), 10, robot.getId(), robot.getRadio());
                    arena.agregarProyectil(p);
                    robot.setCooldown(5); // Ejemplo de cooldown fijo
                    acumulador.registrarDisparo(robot);
                    eventos.add(new EventoSimple(tickActual, TipoEvento.DISPARO_EJECUTADO));
                }
                case NADA -> {
                    // No hacer nada
                }
            }
            // Reducir cooldown en todos los casos
            robot.setCooldown(Math.max(0, robot.getCooldown() - 1));
        }

        // 3. Procesar proyectiles
        motorProyectiles.procesarProyectiles(
            arena.getProyectiles(),
            arena.getRobots(),
            arena.getAncho(),
            arena.getAlto(),
            p -> arena.eliminarProyectil(p),
            impacto -> {
                impacto.objetivo().recibirDanio(impacto.proyectil().getDano());
                acumulador.registrarImpacto(impacto.proyectil(), impacto.objetivo());
                eventos.add(new EventoSimple(tickActual, TipoEvento.IMPACTO_DETECTADO));
                if (!impacto.objetivo().estaActivo()) {
                    acumulador.registrarDestruccion(impacto.objetivo());
                    eventos.add(new EventoSimple(tickActual, TipoEvento.ROBOT_DESTRUIDO));
                }
                arena.eliminarProyectil(impacto.proyectil());
            }
        );

        eventos.add(new EventoSimple(tickActual, TipoEvento.TICK_FINALIZADO));
        eventosPorTick.put(tickActual, eventos);

        tickActual++;

        if (tickActual >= tickMaximo || botsActivos() <= 1) {
            terminado = true;
            this.resultado = calcularResultadoFinal();
        }
    }

    public boolean estaTerminado() {
        return terminado;
    }

    public int getTickActual() {
        return tickActual;
    }

    public List<EventoDeCombate> eventosDelTick(int tick) {
        return eventosPorTick.getOrDefault(tick, List.of());
    }

    public Map<Integer, List<EventoDeCombate>> historialCompleto() {
        return eventosPorTick;
    }

    public ResultadoCombate resultadoFinal() {
        return resultado;
    }

    private int botsActivos() {
        return (int) arena.getRobots().stream().filter(Robot::estaActivo).count();
    }

    private ResultadoCombate calcularResultadoFinal() {
        acumulador.finalizar(arena.getRobots());
        List<EstadisticasRobot> ranking = acumulador.obtenerRanking();
        boolean empate = ranking.size() > 1 && ranking.get(0).getVidaFinal() == ranking.get(1).getVidaFinal();
        UUID ganador = empate ? null : ranking.get(0).getRobotId();
        return new ResultadoCombate(tickActual, ganador, empate, ranking);
    }
}
