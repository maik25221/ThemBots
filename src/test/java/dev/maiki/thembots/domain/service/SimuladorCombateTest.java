package dev.maiki.thembots.domain.service;

import dev.maiki.thembots.domain.model.*;
import dev.maiki.thembots.domain.model.accion.*;
import dev.maiki.thembots.domain.model.enums.EstadoRobot;
import dev.maiki.thembots.domain.model.enums.TipoEvento;
import dev.maiki.thembots.domain.ports.Comportamiento;
import dev.maiki.thembots.domain.value.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SimuladorCombate - Tests de servicio principal de dominio")
class SimuladorCombateTest {

    private Arena arena;
    private Comportamiento comportamientoQuieto;
    private Comportamiento comportamientoAtacante;
    private Comportamiento comportamientoMovil;

    @BeforeEach
    void setUp() {
        comportamientoQuieto = contexto -> new AccionNada();
        comportamientoAtacante = contexto -> new AccionDisparar();
        comportamientoMovil = contexto -> new AccionMover(2.0);
    }

    @Test
    @DisplayName("Simulador inicia correctamente")
    void simuladorIniciaCorrectamente() {
        Robot robot = crearRobot("TestBot", new Posicion(10.0, 10.0), comportamientoQuieto);
        arena = new Arena(100.0, 100.0, List.of(robot), List.of());
        
        SimuladorCombate simulador = new SimuladorCombate(arena, 10);

        assertEquals(0, simulador.getTickActual());
        assertFalse(simulador.estaTerminado());
        assertNull(simulador.resultadoFinal());
    }

    @Test
    @DisplayName("Combate con un solo robot termina inmediatamente")
    void combateConUnSoloRobotTerminaInmediatamente() {
        Robot robot = crearRobot("SoloBot", new Posicion(50.0, 50.0), comportamientoQuieto);
        arena = new Arena(100.0, 100.0, List.of(robot), List.of());
        
        SimuladorCombate simulador = new SimuladorCombate(arena, 10);
        simulador.simularHastaElFinal();

        assertTrue(simulador.estaTerminado());
        assertEquals(1, simulador.getTickActual()); // Termina después del primer tick
        assertNotNull(simulador.resultadoFinal());
        assertEquals(robot.getId(), simulador.resultadoFinal().getGanadorId());
    }

    @Test
    @DisplayName("Combate termina cuando solo queda un robot activo")
    void combateTerminaCuandoSoloQuedaUnRobotActivo() {
        Robot atacante = crearRobot("Atacante", new Posicion(10.0, 10.0), comportamientoAtacante);
        Robot victima = crearRobotConVida("Victima", new Posicion(12.0, 10.0), 
                                        comportamientoQuieto, new Vida(10.0));
        
        arena = new Arena(100.0, 100.0, List.of(atacante, victima), List.of());
        SimuladorCombate simulador = new SimuladorCombate(arena, 20);
        
        simulador.simularHastaElFinal();

        assertTrue(simulador.estaTerminado());
        assertEquals(EstadoRobot.DESTRUIDO, victima.getEstado());
        assertEquals(EstadoRobot.ACTIVO, atacante.getEstado());
        assertEquals(atacante.getId(), simulador.resultadoFinal().getGanadorId());
    }

    @Test
    @DisplayName("Combate termina por límite de ticks")
    void combateTerminaPorLimiteDeTicks() {
        Robot robot1 = crearRobot("Robot1", new Posicion(10.0, 10.0), comportamientoQuieto);
        Robot robot2 = crearRobot("Robot2", new Posicion(90.0, 90.0), comportamientoQuieto);
        
        arena = new Arena(100.0, 100.0, List.of(robot1, robot2), List.of());
        SimuladorCombate simulador = new SimuladorCombate(arena, 5); // Límite bajo
        
        simulador.simularHastaElFinal();

        assertTrue(simulador.estaTerminado());
        assertEquals(5, simulador.getTickActual());
        
        // Ambos robots deberían seguir vivos
        assertTrue(robot1.estaActivo());
        assertTrue(robot2.estaActivo());
    }

    @Test
    @DisplayName("Ejecutar un solo tick")
    void ejecutarUnSoloTick() {
        Robot robot = crearRobot("TestBot", new Posicion(50.0, 50.0), comportamientoMovil);
        arena = new Arena(100.0, 100.0, List.of(robot), List.of());
        
        SimuladorCombate simulador = new SimuladorCombate(arena, 10);
        Posicion posicionInicial = robot.getPosicion();
        
        simulador.simularTick();

        assertEquals(1, simulador.getTickActual());
        assertNotEquals(posicionInicial, robot.getPosicion()); // Debería haberse movido
    }

    @Test
    @DisplayName("Simulador no ejecuta ticks adicionales cuando está terminado")
    void simuladorNoEjecutaTicksAdicionalesCuandoEstaTerminado() {
        Robot robot = crearRobot("TestBot", new Posicion(50.0, 50.0), comportamientoQuieto);
        arena = new Arena(100.0, 100.0, List.of(robot), List.of());
        
        SimuladorCombate simulador = new SimuladorCombate(arena, 1);
        simulador.simularHastaElFinal();
        
        assertTrue(simulador.estaTerminado());
        int ticksAlFinal = simulador.getTickActual();
        
        // Intentar ejecutar más ticks
        simulador.simularTick();
        simulador.simularTick();

        assertEquals(ticksAlFinal, simulador.getTickActual()); // No debe cambiar
    }

    @Test
    @DisplayName("Robot con cooldown no puede disparar")
    void robotConCooldownNoPuedeDisparar() {
        Robot robot = crearRobotConCooldown("Atacante", new Posicion(50.0, 50.0), 
                                          comportamientoAtacante, 3.0);
        arena = new Arena(100.0, 100.0, List.of(robot), List.of());
        
        SimuladorCombate simulador = new SimuladorCombate(arena, 1);
        simulador.simularTick();

        // No debería haber proyectiles porque el robot tenía cooldown
        assertEquals(0, arena.getProyectiles().size());
        // El cooldown debería haber disminuido
        assertEquals(2.0, robot.getCooldown());
    }

    @Test
    @DisplayName("Robot puede disparar cuando no tiene cooldown")
    void robotPuedeDispararCuandoNoTieneCooldown() {
        Robot robot = crearRobot("Atacante", new Posicion(50.0, 50.0), comportamientoAtacante);
        arena = new Arena(100.0, 100.0, List.of(robot), List.of());
        
        SimuladorCombate simulador = new SimuladorCombate(arena, 1);
        simulador.simularTick();

        // Debería haber un proyectil
        assertEquals(1, arena.getProyectiles().size());
        // El robot debería tener cooldown después de disparar
        assertEquals(5.0, robot.getCooldown()); // Cooldown fijo en la implementación
    }

    @Test
    @DisplayName("Robot evita colisión con obstáculo")
    void robotEvitaColisionConObstaculo() {
        Comportamiento moverHaciaObstaculo = contexto -> new AccionMover(10.0);
        Robot robot = crearRobot("MovilBot", new Posicion(10.0, 10.0), moverHaciaObstaculo);
        robot.setDireccion(0.0); // Hacia la derecha
        
        Obstaculo obstaculo = new Obstaculo(new Posicion(15.0, 10.0), new Radio(2.0));
        arena = new Arena(100.0, 100.0, List.of(robot), List.of(obstaculo));
        
        SimuladorCombate simulador = new SimuladorCombate(arena, 1);
        simulador.simularTick();

        // El robot no debería haber pasado del obstáculo
        assertTrue(robot.getPosicion().getX() < 13.0); // Antes del obstáculo
    }

    @Test
    @DisplayName("Robot evita salir de los límites de la arena")
    void robotEvitaSalirDeLosLimitesDeLaArena() {
        Comportamiento moverFuera = contexto -> new AccionMover(20.0);
        Robot robot = crearRobot("BordeBot", new Posicion(95.0, 50.0), moverFuera);
        robot.setDireccion(0.0); // Hacia la derecha
        
        arena = new Arena(100.0, 100.0, List.of(robot), List.of());
        SimuladorCombate simulador = new SimuladorCombate(arena, 1);
        
        simulador.simularTick();

        // El robot no debería haber salido de la arena
        assertTrue(arena.dentroDeLimites(robot.getPosicion()));
        assertTrue(robot.getPosicion().getX() <= 100.0);
    }

    @Test
    @DisplayName("Historial de eventos se registra correctamente")
    void historialDeEventosSeRegistraCorrectamente() {
        Robot robot = crearRobot("EventBot", new Posicion(50.0, 50.0), comportamientoAtacante);
        arena = new Arena(100.0, 100.0, List.of(robot), List.of());
        
        SimuladorCombate simulador = new SimuladorCombate(arena, 2);
        simulador.simularTick();

        List<EventoDeCombate> eventosTick0 = simulador.eventosDelTick(0);
        assertFalse(eventosTick0.isEmpty());
        
        // Debe incluir eventos básicos del tick
        assertTrue(eventosTick0.stream()
                  .anyMatch(e -> e instanceof EventoSimple es && 
                           es.getTipo() == TipoEvento.TICK_INICIADO));
        assertTrue(eventosTick0.stream()
                  .anyMatch(e -> e instanceof EventoSimple es && 
                           es.getTipo() == TipoEvento.TICK_FINALIZADO));
    }

    @Test
    @DisplayName("Resultado final incluye ranking correcto")
    void resultadoFinalIncluyeRankingCorrecto() {
        Robot fuerte = crearRobotConVida("Fuerte", new Posicion(10.0, 10.0), 
                                       comportamientoQuieto, new Vida(100.0));
        Robot debil = crearRobotConVida("Debil", new Posicion(90.0, 90.0), 
                                      comportamientoQuieto, new Vida(50.0));
        
        arena = new Arena(100.0, 100.0, List.of(fuerte, debil), List.of());
        SimuladorCombate simulador = new SimuladorCombate(arena, 1);
        
        simulador.simularHastaElFinal();
        ResultadoCombate resultado = simulador.resultadoFinal();

        assertNotNull(resultado);
        assertEquals(2, resultado.getRanking().size());
        
        // El ranking debería estar ordenado por vida final (descendente)
        EstadisticasRobot primero = resultado.getRanking().get(0);
        EstadisticasRobot segundo = resultado.getRanking().get(1);
        
        assertTrue(primero.getVidaFinal() >= segundo.getVidaFinal());
    }

    @Test
    @DisplayName("Detección de empate cuando robots tienen la misma vida final")
    void deteccionDeEmpateCuandoRobotsTienenLaMismaVidaFinal() {
        Robot robot1 = crearRobotConVida("Bot1", new Posicion(10.0, 10.0), 
                                       comportamientoQuieto, new Vida(75.0));
        Robot robot2 = crearRobotConVida("Bot2", new Posicion(90.0, 90.0), 
                                       comportamientoQuieto, new Vida(75.0));
        
        arena = new Arena(100.0, 100.0, List.of(robot1, robot2), List.of());
        SimuladorCombate simulador = new SimuladorCombate(arena, 1);
        
        simulador.simularHastaElFinal();
        ResultadoCombate resultado = simulador.resultadoFinal();

        assertTrue(resultado.isEmpate());
        assertNull(resultado.getGanadorId());
    }

    // Métodos auxiliares
    private Robot crearRobot(String nombre, Posicion posicion, Comportamiento comportamiento) {
        return new Robot(UUID.randomUUID(), nombre, posicion, 0.0,
                        new Vida(100.0), 0.0, new Radio(1.5), comportamiento);
    }

    private Robot crearRobotConVida(String nombre, Posicion posicion, 
                                  Comportamiento comportamiento, Vida vida) {
        return new Robot(UUID.randomUUID(), nombre, posicion, 0.0,
                        vida, 0.0, new Radio(1.5), comportamiento);
    }

    private Robot crearRobotConCooldown(String nombre, Posicion posicion, 
                                      Comportamiento comportamiento, double cooldown) {
        return new Robot(UUID.randomUUID(), nombre, posicion, 0.0,
                        new Vida(100.0), cooldown, new Radio(1.5), comportamiento);
    }
}