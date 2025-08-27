package dev.maiki.thembots.integration;

import dev.maiki.thembots.application.model.ConfiguracionCombate;
import dev.maiki.thembots.application.model.InstanciaRobot;
import dev.maiki.thembots.application.service.GestorDeSimulaciones;
import dev.maiki.thembots.application.usecase.SimularCombateCompleto;
import dev.maiki.thembots.application.usecase.SimularUnTick;
import dev.maiki.thembots.domain.ia.ComportamientoCazador;
import dev.maiki.thembots.domain.ia.ComportamientoCobarde;
import dev.maiki.thembots.domain.model.EventoDeCombate;
import dev.maiki.thembots.domain.model.Obstaculo;
import dev.maiki.thembots.domain.model.ResultadoCombate;
import dev.maiki.thembots.domain.model.Robot;
import dev.maiki.thembots.domain.ports.Comportamiento;
import dev.maiki.thembots.domain.value.Posicion;
import dev.maiki.thembots.domain.value.Radio;
import dev.maiki.thembots.domain.value.Vida;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests básicos de integración para simulaciones simples.
 */
@DisplayName("Integración - Simulación Básica")
class SimulacionBasicaIntegrationTest {

    private SimularCombateCompleto simularCombateCompleto;
    private SimularUnTick simularUnTick;
    private GestorDeSimulaciones gestorSimulaciones;

    @BeforeEach
    void setUp() {
        simularCombateCompleto = new SimularCombateCompleto();
        gestorSimulaciones = new GestorDeSimulaciones();
        simularUnTick = new SimularUnTick(gestorSimulaciones);
    }

    @Test
    @DisplayName("Simulación completa con todos los componentes")
    void simulacionCompletaConTodosLosComponentes() {
        // Crear configuración de combate
        InstanciaRobot atacante = crearRobot("Atacante", new Posicion(10.0, 10.0), 
                                           new Vida(50.0), new ComportamientoCazador());
        InstanciaRobot defensor = crearRobot("Defensor", new Posicion(90.0, 90.0), 
                                           new Vida(100.0), new ComportamientoCobarde());

        List<Obstaculo> obstaculos = List.of(
                new Obstaculo(new Posicion(50.0, 50.0), new Radio(5.0)),
                new Obstaculo(new Posicion(25.0, 75.0), new Radio(3.0))
        );

        ConfiguracionCombate config = crearConfiguracion(100.0, 100.0, 50, 
                                                        List.of(atacante, defensor), 
                                                        obstaculos);

        // Ejecutar simulación
        ResultadoCombate resultado = simularCombateCompleto.ejecutar(config);

        // Verificaciones
        assertNotNull(resultado);
        assertTrue(resultado.getTickFinal() > 0);
        assertTrue(resultado.getTickFinal() <= 50);
        assertNotNull(resultado.getRanking());
        assertEquals(2, resultado.getRanking().size());
        
        // Al menos uno debe haber sobrevivido o ambos destruidos
        boolean haySupervivientes = resultado.getRanking().stream()
                .anyMatch(stats -> !stats.isDestruido());
        boolean todosMuertos = resultado.getRanking().stream()
                .allMatch(stats -> stats.isDestruido());
        
        assertTrue(haySupervivientes || todosMuertos);
    }

    @Test
    @DisplayName("Simulación paso a paso con eventos")
    void simulacionPasoAPasoConEventos() {
        // Configuración simple
        InstanciaRobot robot1 = crearRobot("Robot1", new Posicion(20.0, 20.0), 
                                         new Vida(100.0), new ComportamientoCazador());
        InstanciaRobot robot2 = crearRobot("Robot2", new Posicion(80.0, 80.0), 
                                         new Vida(100.0), new ComportamientoCobarde());

        ConfiguracionCombate config = crearConfiguracion(100.0, 100.0, 10, 
                                                        List.of(robot1, robot2), 
                                                        List.of());

        // Crear simulador directamente
        dev.maiki.thembots.domain.model.Arena arena = new dev.maiki.thembots.domain.model.Arena(
                config.anchoArena(), config.altoArena(), config.crearRobots(), config.obstaculos());
        dev.maiki.thembots.domain.service.SimuladorCombate simulador = 
                new dev.maiki.thembots.domain.service.SimuladorCombate(arena, config.tickMaximo());

        // Registrar en el gestor
        String simulacionId = "test-simulation";
        gestorSimulaciones.registrarSimulador(simulacionId, simulador);

        // Ejecutar algunos ticks manualmente
        int ticksEjecutados = 0;
        while (!simulador.estaTerminado() && ticksEjecutados < 5) {
            List<EventoDeCombate> eventos = simularUnTick.ejecutar(simulacionId);
            assertNotNull(eventos);
            assertTrue(eventos.size() >= 2); // Al menos TICK_INICIADO y TICK_FINALIZADO
            ticksEjecutados++;
        }

        assertTrue(ticksEjecutados > 0);
        assertEquals(ticksEjecutados, simulador.getTickActual());

        // Limpiar
        gestorSimulaciones.eliminarSimulador(simulacionId);
    }

    private InstanciaRobot crearRobot(String nombre, Posicion posicion, 
                                    Vida vida, Comportamiento comportamiento) {
        return crearRobotConId(UUID.randomUUID(), nombre, posicion, vida, comportamiento);
    }
    
    private InstanciaRobot crearRobotConId(UUID id, String nombre, Posicion posicion, 
                                          Vida vida, Comportamiento comportamiento) {
        return new InstanciaRobot() {
            public UUID getId() { return id; }
            
            @Override
            public Robot aEntidad() {
                return new Robot(id, nombre, posicion, 0.0, vida, 0.0, 
                               new Radio(1.5), comportamiento);
            }
        };
    }

    private ConfiguracionCombate crearConfiguracion(double ancho, double alto, int tickMaximo,
                                                   List<InstanciaRobot> robots, 
                                                   List<Obstaculo> obstaculos) {
        ConfiguracionCombate config = new ConfiguracionCombate();
        try {
            var anchoField = ConfiguracionCombate.class.getDeclaredField("anchoArena");
            anchoField.setAccessible(true);
            anchoField.set(config, ancho);
            
            var altoField = ConfiguracionCombate.class.getDeclaredField("altoArena");
            altoField.setAccessible(true);
            altoField.set(config, alto);
            
            var tickField = ConfiguracionCombate.class.getDeclaredField("tickMaximo");
            tickField.setAccessible(true);
            tickField.set(config, tickMaximo);
            
            var robotsField = ConfiguracionCombate.class.getDeclaredField("robots");
            robotsField.setAccessible(true);
            robotsField.set(config, robots);
            
            var obstaculosField = ConfiguracionCombate.class.getDeclaredField("obstaculos");
            obstaculosField.setAccessible(true);
            obstaculosField.set(config, obstaculos);
            
        } catch (Exception e) {
            throw new RuntimeException("Error creando configuración", e);
        }
        return config;
    }
}