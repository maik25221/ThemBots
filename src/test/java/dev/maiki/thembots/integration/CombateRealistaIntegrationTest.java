package dev.maiki.thembots.integration;

import dev.maiki.thembots.application.model.ConfiguracionCombate;
import dev.maiki.thembots.application.model.InstanciaRobot;
import dev.maiki.thembots.application.usecase.SimularCombateCompleto;
import dev.maiki.thembots.domain.ia.ComportamientoCazador;
import dev.maiki.thembots.domain.ia.ComportamientoCobarde;
import dev.maiki.thembots.domain.ia.ComportamientoPatrulla;
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
 * Tests de integración para combates realistas con múltiples tipos de IA.
 */
@DisplayName("Integración - Combates Realistas")
class CombateRealistaIntegrationTest {

    private SimularCombateCompleto simularCombateCompleto;

    @BeforeEach
    void setUp() {
        simularCombateCompleto = new SimularCombateCompleto();
    }

    @Test
    @DisplayName("Combate realista con múltiples tipos de IA")
    void combateRealistaConMultiplesTiposDeIA() {
        // Robots con diferentes comportamientos y configuraciones balanceadas
        InstanciaRobot cazador = crearRobot("CazadorAgresivo", 
                                          new Posicion(25.0, 25.0), 
                                          new Vida(120.0), 
                                          new ComportamientoCazador());

        InstanciaRobot cobarde = crearRobot("CobardeEvasivo", 
                                          new Posicion(75.0, 25.0), 
                                          new Vida(150.0), 
                                          new ComportamientoCobarde());

        InstanciaRobot patrulla = crearRobot("PatrullaDefensor", 
                                           new Posicion(50.0, 75.0), 
                                           new Vida(100.0), 
                                           new ComportamientoPatrulla(new Posicion(40.0, 75.0), new Posicion(60.0, 75.0)));

        // Arena con obstáculos estratégicos
        List<Obstaculo> obstaculos = List.of(
                new Obstaculo(new Posicion(50.0, 50.0), new Radio(8.0)), // Centro
                new Obstaculo(new Posicion(20.0, 80.0), new Radio(4.0)), // Esquina
                new Obstaculo(new Posicion(80.0, 20.0), new Radio(4.0)), // Otra esquina
                new Obstaculo(new Posicion(30.0, 60.0), new Radio(3.0))  // Intermedio
        );

        ConfiguracionCombate config = crearConfiguracion(100.0, 100.0, 300, 
                                                        List.of(cazador, cobarde, patrulla), 
                                                        obstaculos);

        // Ejecutar combate completo
        ResultadoCombate resultado = simularCombateCompleto.ejecutar(config);

        // Verificaciones del resultado
        assertNotNull(resultado);
        assertTrue(resultado.getTickFinal() > 10, "El combate debería durar más de 10 ticks");
        assertTrue(resultado.getTickFinal() <= 300, "El combate no debería exceder el máximo");

        // Verificar ranking completo
        assertNotNull(resultado.getRanking());
        assertEquals(3, resultado.getRanking().size(), "Debe haber estadísticas para 3 robots");

        // Verificar que hay diferencias de rendimiento
        var ranking = resultado.getRanking();
        assertTrue(ranking.get(0).getVidaFinal() >= ranking.get(1).getVidaFinal(), 
                  "El ranking debe estar ordenado por vida final");
        assertTrue(ranking.get(1).getVidaFinal() >= ranking.get(2).getVidaFinal(), 
                  "El ranking debe estar ordenado por vida final");

        // Debe haber al menos un superviviente (o empate total)
        boolean haySupervivientes = resultado.getRanking().stream()
                .anyMatch(stats -> !stats.isDestruido());
        
        assertTrue(haySupervivientes, "Debe haber al menos un superviviente");
        
        // El ranking debe mostrar diferencias de rendimiento
        double mejorVida = resultado.getRanking().get(0).getVidaFinal();
        double peorVida = resultado.getRanking().get(2).getVidaFinal();
        assertTrue(mejorVida >= peorVida, "El ranking debe estar ordenado correctamente");

        System.out.println("=== Resultado del combate realista ===");
        System.out.println("Duración: " + resultado.getTickFinal() + " ticks");
        System.out.println("Ganador: " + (resultado.isEmpate() ? "EMPATE" : "ID " + resultado.getGanadorId()));
        System.out.println("Ranking:");
        resultado.getRanking().forEach(stats -> 
            System.out.printf("  %s: %.1f vida final (%s)%n", 
                stats.getNombre(), 
                stats.getVidaFinal(), 
                stats.isDestruido() ? "DESTRUIDO" : "VIVO"));
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