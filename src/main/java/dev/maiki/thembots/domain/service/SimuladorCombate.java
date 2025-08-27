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
    private long contadorProyectiles = 0;

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

        // 1. Decidir acciones (orden determinista) con registro detallado
        Map<UUID, Accion> decisiones = new LinkedHashMap<>();
        List<Robot> robotsOrdenados = arena.getRobots().stream()
                .sorted(Comparator.comparing(r -> r.getId().toString()))
                .toList();
        
        for (Robot robot : robotsOrdenados) {
            if (!robot.estaActivo()) continue;
            ContextoPercepcion contexto = fabricaContexto.generar(robot.getId(), tickActual, arena);
            
            // Obtener decisión detallada
            DecisionIA decision = robot.getComportamiento().decidirConDetalle(contexto);
            decisiones.put(robot.getId(), decision.getAccion());
            
            // Registrar evento de decisión
            eventos.add(new EventoDecision(
                tickActual,
                robot.getId(),
                robot.getNombre(),
                robot.getComportamiento().getClass().getSimpleName(),
                robot.getPosicion(),
                robot.getDireccion(),
                decision.getAccion(),
                decision.getRazonamiento()
            ));
        }

        // 2. Reducir cooldown primero
        for (Robot robot : robotsOrdenados) {
            if (!robot.estaActivo()) continue;
            robot.setCooldown(Math.max(0, robot.getCooldown() - 1));
        }
        
        // 3. Resolver conflictos y aplicar acciones
        resolverConflictosYAplicarAcciones(robotsOrdenados, decisiones, eventos);
        
        // 4. Procesar proyectiles
        motorProyectiles.procesarProyectiles(
            arena.getProyectiles(),
            arena.getRobots(),
            arena.getAncho(),
            arena.getAlto(),
            p -> arena.eliminarProyectil(p),
            impacto -> {
                // Capturar estado antes del impacto
                double vidaAnterior = impacto.objetivo().getVida().valor();
                
                // Buscar quién disparó el proyectil
                Robot disparador = arena.getRobots().stream()
                    .filter(r -> r.getId().equals(impacto.proyectil().getOrigen()))
                    .findFirst()
                    .orElse(null);
                String nombreDisparador = disparador != null ? disparador.getNombre() : "Desconocido";
                
                // Aplicar daño
                impacto.objetivo().recibirDanio(impacto.proyectil().getDmg());
                double vidaPosterior = impacto.objetivo().getVida().valor();
                boolean objetivoDestruido = !impacto.objetivo().estaActivo();
                
                // Registrar eventos
                acumulador.registrarImpacto(impacto.proyectil(), impacto.objetivo());
                
                eventos.add(new EventoImpacto(
                    tickActual,
                    impacto.proyectil().getId(),
                    impacto.proyectil().getOrigen(),
                    nombreDisparador,
                    impacto.objetivo().getId(),
                    impacto.objetivo().getNombre(),
                    impacto.proyectil().getPosicion(),
                    impacto.proyectil().getDmg(),
                    vidaAnterior,
                    vidaPosterior,
                    objetivoDestruido
                ));
                
                if (objetivoDestruido) {
                    acumulador.registrarDestruccion(impacto.objetivo());
                    eventos.add(new EventoSimple(tickActual, TipoEvento.ROBOT_DESTRUIDO));
                }
                
                arena.eliminarProyectil(impacto.proyectil());
            },
            arena.getObstaculos()
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

    /**
     * Resuelve conflictos entre acciones de robots y las aplica de forma determinista.
     * Detecta colisiones y aplica daño cuando robots intentan ocupar el mismo espacio.
     */
    private void resolverConflictosYAplicarAcciones(List<Robot> robotsOrdenados, 
                                                   Map<UUID, Accion> decisiones, 
                                                   List<EventoDeCombate> eventos) {
        
        // Separar acciones por tipo para procesamiento específico
        List<MovimientoPlaneado> movimientos = new ArrayList<>();
        
        // 1. Procesar acciones no conflictivas primero (giros y disparos)
        for (Robot robot : robotsOrdenados) {
            if (!robot.estaActivo()) continue;
            Accion accion = decisiones.get(robot.getId());
            
            Posicion posicionAntes = robot.getPosicion();
            double direccionAntes = robot.getDireccion();
            
            switch (accion.tipo()) {
                case GIRAR -> {
                    AccionGirar a = (AccionGirar) accion;
                    robot.setDireccion(robot.getDireccion() + a.getAngulo());
                    eventos.add(new EventoSimple(tickActual, TipoEvento.GIRO_REALIZADO));
                    eventos.add(new EventoAccion(tickActual, robot.getId(), robot.getNombre(), accion,
                        posicionAntes, robot.getPosicion(), direccionAntes, robot.getDireccion(),
                        true, String.format("Giró %.1f° (%.1f° → %.1f°)", 
                            Math.toDegrees(a.getAngulo()), Math.toDegrees(direccionAntes), Math.toDegrees(robot.getDireccion()))));
                }
                case DISPARAR -> {
                    boolean exitoso = robot.getCooldown() == 0;
                    String detalle = exitoso ? "Proyectil creado exitosamente" : String.format("Falló: cooldown restante %.0f", robot.getCooldown());
                    
                    if (exitoso) {
                        AccionDisparar a = (AccionDisparar) accion;
                        // Usar contador determinista para IDs de proyectiles
                        UUID proyectilId = UUID.fromString(String.format("10000000-0000-0000-0000-%012d", ++contadorProyectiles));
                        Proyectil p = new Proyectil(proyectilId, robot.getPosicion(), 
                                                   robot.getDireccionVector(), 10, robot.getId(), robot.getRadio());
                        arena.agregarProyectil(p);
                        robot.setCooldown(5);
                        acumulador.registrarDisparo(robot);
                        eventos.add(new EventoSimple(tickActual, TipoEvento.DISPARO_EJECUTADO));
                    }
                    
                    eventos.add(new EventoAccion(tickActual, robot.getId(), robot.getNombre(), accion,
                        posicionAntes, robot.getPosicion(), direccionAntes, robot.getDireccion(),
                        exitoso, detalle));
                }
                case MOVER -> {
                    AccionMover a = (AccionMover) accion;
                    Posicion destino = robot.getPosicion().avanzar(robot.getDireccionVector().escalar(a.getDistancia()));
                    movimientos.add(new MovimientoPlaneado(robot, robot.getPosicion(), destino, a.getDistancia()));
                }
                case NADA -> {
                    eventos.add(new EventoAccion(tickActual, robot.getId(), robot.getNombre(), accion,
                        posicionAntes, robot.getPosicion(), direccionAntes, robot.getDireccion(),
                        true, "Sin acción requerida"));
                }
            }
        }
        
        // 2. Resolver conflictos de movimiento
        resolverConflictosMovimiento(movimientos, eventos);
    }

    /**
     * Resuelve conflictos entre movimientos de robots.
     */
    private void resolverConflictosMovimiento(List<MovimientoPlaneado> movimientos, 
                                            List<EventoDeCombate> eventos) {
        
        for (MovimientoPlaneado mov : movimientos) {
            if (mov.procesado) continue;
            
            // Verificar límites de arena
            if (!arena.dentroDeLimites(mov.destino)) {
                eventos.add(new EventoAccion(tickActual, mov.robot.getId(), mov.robot.getNombre(), 
                    new AccionMover(mov.distancia), mov.origen, mov.origen, mov.robot.getDireccion(), mov.robot.getDireccion(),
                    false, String.format("Movimiento bloqueado: fuera de límites (%.1f,%.1f)", mov.destino.getX(), mov.destino.getY())));
                continue; // Robot se queda en su posición
            }
            
            // Verificar colisión con obstáculos
            List<Robot> otrosRobots = arena.getRobots().stream()
                    .filter(r -> !r.getId().equals(mov.robot.getId()))
                    .toList();
            
            if (motorColisiones.hayColisionEnCamino(mov.origen, mov.destino, 
                                                   mov.robot.getRadio(), otrosRobots, arena.getObstaculos())) {
                eventos.add(new EventoAccion(tickActual, mov.robot.getId(), mov.robot.getNombre(), 
                    new AccionMover(mov.distancia), mov.origen, mov.origen, mov.robot.getDireccion(), mov.robot.getDireccion(),
                    false, "Movimiento bloqueado: obstáculo en el camino"));
                continue; // Robot se queda en su posición
            }
            
            // Buscar conflictos con otros robots que también se mueven
            MovimientoPlaneado conflicto = encontrarConflictoRobot(mov, movimientos);
            
            if (conflicto != null) {
                // Resolver colisión entre robots
                resolverColisionRobots(mov, conflicto, eventos);
            } else {
                // Movimiento libre
                mov.robot.setPosicion(mov.destino);
                eventos.add(new EventoSimple(tickActual, TipoEvento.MOVIMIENTO_REALIZADO));
                eventos.add(new EventoAccion(tickActual, mov.robot.getId(), mov.robot.getNombre(), 
                    new AccionMover(mov.distancia), mov.origen, mov.destino, mov.robot.getDireccion(), mov.robot.getDireccion(),
                    true, String.format("Movimiento exitoso: %.1f unidades", mov.distancia)));
                mov.procesado = true;
            }
        }
    }

    /**
     * Encuentra si hay conflicto entre un movimiento y otros movimientos planeados.
     */
    private MovimientoPlaneado encontrarConflictoRobot(MovimientoPlaneado mov, List<MovimientoPlaneado> todosMovimientos) {
        for (MovimientoPlaneado otro : todosMovimientos) {
            if (otro == mov || otro.procesado) continue;
            
            // Verificar si las rutas se cruzan o los destinos están muy cerca
            double distanciaDestinos = mov.destino.distanciaA(otro.destino);
            double radiosCombinados = mov.robot.getRadio().valor() + otro.robot.getRadio().valor();
            
            if (distanciaDestinos <= radiosCombinados) {
                return otro;
            }
        }
        return null;
    }

    /**
     * Resuelve colisión entre dos robots, colocándolos antes del punto de colisión y aplicando daño.
     */
    private void resolverColisionRobots(MovimientoPlaneado mov1, MovimientoPlaneado mov2, List<EventoDeCombate> eventos) {
        // Calcular punto de colisión (punto medio entre destinos)
        double puntoColisionX = (mov1.destino.getX() + mov2.destino.getX()) / 2.0;
        double puntoColisionY = (mov1.destino.getY() + mov2.destino.getY()) / 2.0;
        
        // Calcular distancias de seguridad basadas en radios
        double radio1 = mov1.robot.getRadio().valor();
        double radio2 = mov2.robot.getRadio().valor();
        
        // Calcular direcciones desde el punto de colisión hacia cada robot original
        double dx1 = mov1.origen.getX() - puntoColisionX;
        double dy1 = mov1.origen.getY() - puntoColisionY;
        double distancia1 = Math.sqrt(dx1*dx1 + dy1*dy1);
        
        double dx2 = mov2.origen.getX() - puntoColisionX;
        double dy2 = mov2.origen.getY() - puntoColisionY;
        double distancia2 = Math.sqrt(dx2*dx2 + dy2*dy2);
        
        // Normalizar direcciones (si robots están en el mismo punto, usar direcciones por defecto)
        if (distancia1 > 0.1) {
            dx1 /= distancia1;
            dy1 /= distancia1;
        } else {
            dx1 = -1.0; dy1 = 0.0;
        }
        if (distancia2 > 0.1) {
            dx2 /= distancia2;
            dy2 /= distancia2;
        } else {
            dx2 = 1.0; dy2 = 0.0;
        }
        
        // Posicionar robots en lados opuestos del punto de colisión
        Posicion nuevaPos1 = new Posicion(
            puntoColisionX + dx1 * (radio1 + 0.1),
            puntoColisionY + dy1 * (radio1 + 0.1)
        );
        Posicion nuevaPos2 = new Posicion(
            puntoColisionX + dx2 * (radio2 + 0.1),
            puntoColisionY + dy2 * (radio2 + 0.1)
        );
        
        // Aplicar posiciones (verificando límites de arena)
        if (arena.dentroDeLimites(nuevaPos1)) {
            mov1.robot.setPosicion(nuevaPos1);
        }
        if (arena.dentroDeLimites(nuevaPos2)) {
            mov2.robot.setPosicion(nuevaPos2);
        }
        
        // Aplicar daño por colisión
        double dañoColision = 10.0; // Daño fijo por colisión
        mov1.robot.recibirDanio(dañoColision);
        mov2.robot.recibirDanio(dañoColision);
        
        // Registrar eventos
        eventos.add(new EventoSimple(tickActual, TipoEvento.IMPACTO_DETECTADO));
        
        // Marcar como procesados
        mov1.procesado = true;
        mov2.procesado = true;
    }

    /**
     * Clase auxiliar para representar un movimiento planeado.
     */
    private static class MovimientoPlaneado {
        final Robot robot;
        final Posicion origen;
        final Posicion destino;
        final double distancia;
        boolean procesado = false;
        
        MovimientoPlaneado(Robot robot, Posicion origen, Posicion destino, double distancia) {
            this.robot = robot;
            this.origen = origen;
            this.destino = destino;
            this.distancia = distancia;
        }
    }
}
