package dev.maiki.thembots.integration;

import dev.maiki.thembots.application.model.ConfiguracionCombate;
import dev.maiki.thembots.application.model.InstanciaRobot;
import dev.maiki.thembots.application.service.GestorDeSimulaciones;
import dev.maiki.thembots.application.usecase.SimularCombateCompleto;
import dev.maiki.thembots.domain.ia.ComportamientoCazador;
import dev.maiki.thembots.domain.ia.ComportamientoCobarde;
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
 * Tests de integración para el gestor de múltiples simulaciones.
 */
@DisplayName("Integración - Gestor de Simulaciones")
class GestorSimulacionesIntegrationTest {

    private GestorDeSimulaciones gestor;

    @BeforeEach
    void setUp() {
        gestor = new GestorDeSimulaciones();
    }

    @Test
    @DisplayName("Gestión de múltiples simulaciones concurrentes")
    void gestionDeMultiplesSimulacionesConcurrentes() {
        // Crear varias simulaciones manualmente
        ConfiguracionCombate config1 = crearConfiguracion("Sim1");
        ConfiguracionCombate config2 = crearConfiguracion("Sim2"); 
        ConfiguracionCombate config3 = crearConfiguracion("Sim3");

        // Crear simuladores usando SimularCombateCompleto
        SimularCombateCompleto simulador = new SimularCombateCompleto();
        
        // IDs únicos para cada simulación
        String sim1Id = "sim1";
        String sim2Id = "sim2";
        String sim3Id = "sim3";

        // Registrar simuladores en el gestor
        gestor.registrarSimulador(sim1Id, new dev.maiki.thembots.domain.service.SimuladorCombate(
                new dev.maiki.thembots.domain.model.Arena(config1.anchoArena(), config1.altoArena(), 
                        config1.crearRobots(), config1.obstaculos()), 
                config1.tickMaximo()));
        
        gestor.registrarSimulador(sim2Id, new dev.maiki.thembots.domain.service.SimuladorCombate(
                new dev.maiki.thembots.domain.model.Arena(config2.anchoArena(), config2.altoArena(), 
                        config2.crearRobots(), config2.obstaculos()), 
                config2.tickMaximo()));
                
        gestor.registrarSimulador(sim3Id, new dev.maiki.thembots.domain.service.SimuladorCombate(
                new dev.maiki.thembots.domain.model.Arena(config3.anchoArena(), config3.altoArena(), 
                        config3.crearRobots(), config3.obstaculos()), 
                config3.tickMaximo()));

        // Verificar que los simuladores fueron registrados correctamente
        assertNotNull(gestor.obtenerSimulador(sim1Id));
        assertNotNull(gestor.obtenerSimulador(sim2Id));
        assertNotNull(gestor.obtenerSimulador(sim3Id));

        // Ejecutar un tick en cada una
        gestor.obtenerSimulador(sim1Id).simularTick();
        gestor.obtenerSimulador(sim2Id).simularTick();
        gestor.obtenerSimulador(sim3Id).simularTick();

        // Verificar que cada una progresó independientemente
        assertEquals(1, gestor.obtenerSimulador(sim1Id).getTickActual());
        assertEquals(1, gestor.obtenerSimulador(sim2Id).getTickActual());
        assertEquals(1, gestor.obtenerSimulador(sim3Id).getTickActual());

        // Ejecutar más ticks en una sola simulación
        while (!gestor.obtenerSimulador(sim1Id).estaTerminado() && gestor.obtenerSimulador(sim1Id).getTickActual() < 10) {
            gestor.obtenerSimulador(sim1Id).simularTick();
        }

        // Verificar que solo sim1 progresó más
        assertTrue(gestor.obtenerSimulador(sim1Id).getTickActual() > 1);
        assertEquals(1, gestor.obtenerSimulador(sim2Id).getTickActual());
        assertEquals(1, gestor.obtenerSimulador(sim3Id).getTickActual());
        
        // Limpiar simuladores
        gestor.eliminarSimulador(sim1Id);
        gestor.eliminarSimulador(sim2Id);
        gestor.eliminarSimulador(sim3Id);
    }

    private ConfiguracionCombate crearConfiguracion(String sufijo) {
        InstanciaRobot robot1 = crearRobot("Cazador" + sufijo, new Posicion(30.0, 30.0), 
                                          new Vida(80.0), new ComportamientoCazador());
        InstanciaRobot robot2 = crearRobot("Cobarde" + sufijo, new Posicion(70.0, 70.0), 
                                          new Vida(80.0), new ComportamientoCobarde());

        return crearConfiguracion(100.0, 100.0, 30, List.of(robot1, robot2), List.of());
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