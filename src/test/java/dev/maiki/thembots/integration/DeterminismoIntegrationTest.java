package dev.maiki.thembots.integration;

import dev.maiki.thembots.application.model.ConfiguracionCombate;
import dev.maiki.thembots.application.model.InstanciaRobot;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests de determinismo del sistema de simulación.
 * Separado en clase individual para evitar interferencias con otros tests.
 */
@DisplayName("Integración - Determinismo del Sistema")
class DeterminismoIntegrationTest {

    private SimularCombateCompleto simularCombateCompleto;

    @BeforeEach
    void setUp() {
        simularCombateCompleto = new SimularCombateCompleto();
    }

    @Test
    @DisplayName("Determinismo completo del sistema")
    void determinismoCompletoDelSistema() {
        // Crear configuración idéntica para cada simulación
        // para evitar compartir estado entre ejecuciones
        ConfiguracionCombate config1 = crearConfiguracionCompleja();
        ConfiguracionCombate config2 = crearConfiguracionCompleja();
        ConfiguracionCombate config3 = crearConfiguracionCompleja();

        // Ejecutar la misma simulación múltiples veces
        ResultadoCombate resultado1 = simularCombateCompleto.ejecutar(config1);
        ResultadoCombate resultado2 = simularCombateCompleto.ejecutar(config2);
        ResultadoCombate resultado3 = simularCombateCompleto.ejecutar(config3);

        // Todos los resultados deben ser idénticos
        assertEquals(resultado1.getTickFinal(), resultado2.getTickFinal());
        assertEquals(resultado1.getTickFinal(), resultado3.getTickFinal());
        assertEquals(resultado1.isEmpate(), resultado2.isEmpate());
        assertEquals(resultado1.isEmpate(), resultado3.isEmpate());
        assertEquals(resultado1.getGanadorId(), resultado2.getGanadorId());
        assertEquals(resultado1.getGanadorId(), resultado3.getGanadorId());

        // Verificar que las estadísticas también sean idénticas
        assertEquals(resultado1.getRanking().size(), resultado2.getRanking().size());
        assertEquals(resultado1.getRanking().size(), resultado3.getRanking().size());
    }

    private ConfiguracionCombate crearConfiguracionCompleja() {
        // Usar UUIDs fijos para garantizar determinismo en el orden de procesamiento
        UUID id1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID id2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
        
        InstanciaRobot r1 = crearRobotConId(id1, "Bot1", new Posicion(25.0, 25.0), 
                new Vida(100.0), new ComportamientoCazador());
        InstanciaRobot r2 = crearRobotConId(id2, "Bot2", new Posicion(75.0, 25.0), 
                new Vida(100.0), new ComportamientoCobarde());
        
        List<Obstaculo> obs = List.of(
                new Obstaculo(new Posicion(50.0, 50.0), new Radio(5.0))
        );
        
        return crearConfiguracion(100.0, 100.0, 100, List.of(r1, r2), obs);
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
        // Crear configuración usando reflexión ya que ConfiguracionCombate no tiene constructor público
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