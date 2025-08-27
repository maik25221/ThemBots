package dev.maiki.thembots.domain.ia;

import dev.maiki.thembots.domain.model.accion.*;
import dev.maiki.thembots.domain.percepcion.ContextoPercepcion;
import dev.maiki.thembots.domain.percepcion.VistaRobot;
import dev.maiki.thembots.domain.value.Posicion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ComportamientoCazador - Tests simplificados de IA agresiva")
class ComportamientoCazadorTestSimple {

    private ComportamientoCazador cazador;
    private UUID robotPropioId;

    @BeforeEach
    void setUp() {
        cazador = new ComportamientoCazador();
        robotPropioId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Sin enemigos disponibles - no hace nada")
    void sinEnemigosDisponiblesNoHaceNada() {
        VistaRobot robotPropio = crearVistaRobot(robotPropioId, "Cazador",
                new Posicion(10.0, 10.0), 0.0, 0.0, 100.0, false, 1.5);
        
        ContextoPercepcion contexto = crearContexto(robotPropio, List.of(robotPropio));
        
        Accion accion = cazador.decidir(contexto);
        
        assertInstanceOf(AccionNada.class, accion);
    }

    @Test
    @DisplayName("Todos los enemigos están destruidos - no hace nada")
    void todosLosEnemigosEstanDestruidosNoHaceNada() {
        VistaRobot robotPropio = crearVistaRobot(robotPropioId, "Cazador",
                new Posicion(10.0, 10.0), 0.0, 0.0, 100.0, false, 1.5);
        VistaRobot enemigoDestruido = crearVistaRobot(UUID.randomUUID(), "Destruido",
                new Posicion(20.0, 10.0), 0.0, 0.0, 0.0, true, 1.5);
        
        ContextoPercepcion contexto = crearContexto(robotPropio, 
                List.of(robotPropio, enemigoDestruido));
        
        Accion accion = cazador.decidir(contexto);
        
        assertInstanceOf(AccionNada.class, accion);
    }

    @Test
    @DisplayName("Enemigo cerca y apuntando correctamente - dispara")
    void enemigoCercaYApuntandoCorrectamenteDispara() {
        VistaRobot robotPropio = crearVistaRobot(robotPropioId, "Cazador",
                new Posicion(10.0, 10.0), 0.0, 0.0, 100.0, false, 1.5);
        // Enemigo a distancia 4, en línea recta hacia la derecha
        VistaRobot enemigoCercano = crearVistaRobot(UUID.randomUUID(), "Cercano",
                new Posicion(14.0, 10.0), 0.0, 0.0, 100.0, false, 1.5);
        
        ContextoPercepcion contexto = crearContexto(robotPropio,
                List.of(robotPropio, enemigoCercano));
        
        Accion accion = cazador.decidir(contexto);
        
        assertInstanceOf(AccionDisparar.class, accion);
    }

    @Test
    @DisplayName("Enemigo cerca pero no apuntando - gira hacia él")
    void enemigoCercaPeroNoApuntandoGiraHaciaEl() {
        VistaRobot robotPropio = crearVistaRobot(robotPropioId, "Cazador",
                new Posicion(10.0, 10.0), 0.0, 0.0, 100.0, false, 1.5);
        // Enemigo arriba - requiere girar π/2 radianes
        VistaRobot enemigoArriba = crearVistaRobot(UUID.randomUUID(), "Arriba",
                new Posicion(10.0, 15.0), 0.0, 0.0, 100.0, false, 1.5);
        
        ContextoPercepcion contexto = crearContexto(robotPropio,
                List.of(robotPropio, enemigoArriba));
        
        Accion accion = cazador.decidir(contexto);
        
        assertInstanceOf(AccionGirar.class, accion);
        AccionGirar giro = (AccionGirar) accion;
        assertTrue(giro.getAngulo() > 0); // Debería girar en sentido positivo
    }

    @Test
    @DisplayName("Enemigo lejos y apuntando correctamente - avanza")
    void enemigoLejosYApuntandoCorrectamenteAvanza() {
        VistaRobot robotPropio = crearVistaRobot(robotPropioId, "Cazador",
                new Posicion(10.0, 10.0), 0.0, 0.0, 100.0, false, 1.5);
        // Enemigo a distancia 10, lejos
        VistaRobot enemigoLejano = crearVistaRobot(UUID.randomUUID(), "Lejano",
                new Posicion(20.0, 10.0), 0.0, 0.0, 100.0, false, 1.5);
        
        ContextoPercepcion contexto = crearContexto(robotPropio,
                List.of(robotPropio, enemigoLejano));
        
        Accion accion = cazador.decidir(contexto);
        
        assertInstanceOf(AccionMover.class, accion);
        AccionMover movimiento = (AccionMover) accion;
        assertTrue(movimiento.getDistancia() > 0); // Debería avanzar
    }

    @Test
    @DisplayName("Enemigo muy cerca - retrocede")
    void enemigoMuyCercaRetrocede() {
        VistaRobot robotPropio = crearVistaRobot(robotPropioId, "Cazador",
                new Posicion(10.0, 10.0), 0.0, 0.0, 100.0, false, 1.5);
        // Enemigo a distancia 1.5, muy cerca
        VistaRobot enemigoMuyCercano = crearVistaRobot(UUID.randomUUID(), "MuyCercano",
                new Posicion(11.5, 10.0), 0.0, 0.0, 100.0, false, 1.5);
        
        ContextoPercepcion contexto = crearContexto(robotPropio,
                List.of(robotPropio, enemigoMuyCercano));
        
        Accion accion = cazador.decidir(contexto);
        
        assertInstanceOf(AccionMover.class, accion);
        AccionMover movimiento = (AccionMover) accion;
        assertEquals(-1.0, movimiento.getDistancia()); // Debería retroceder
    }

    @Test
    @DisplayName("Robot con cooldown no puede disparar")
    void robotConCooldownNoPuedeDisparar() {
        VistaRobot robotConCooldown = crearVistaRobot(robotPropioId, "Cazador",
                new Posicion(10.0, 10.0), 0.0, 3.0, 100.0, false, 1.5);
        // Enemigo en posición ideal para disparar
        VistaRobot enemigo = crearVistaRobot(UUID.randomUUID(), "Objetivo",
                new Posicion(15.0, 10.0), 0.0, 0.0, 100.0, false, 1.5);
        
        ContextoPercepcion contexto = crearContexto(robotConCooldown,
                List.of(robotConCooldown, enemigo));
        
        Accion accion = cazador.decidir(contexto);
        
        // No debe disparar debido al cooldown
        assertInstanceOf(AccionMover.class, accion);
    }

    @Test
    @DisplayName("Selecciona enemigo más cercano entre múltiples")
    void seleccionaEnemigoMasCercanoEntreMultiples() {
        VistaRobot robotPropio = crearVistaRobot(robotPropioId, "Cazador",
                new Posicion(10.0, 10.0), 0.0, 0.0, 100.0, false, 1.5);
        
        VistaRobot enemigoLejano = crearVistaRobot(UUID.randomUUID(), "Lejano",
                new Posicion(30.0, 10.0), 0.0, 0.0, 100.0, false, 1.5);
        VistaRobot enemigoCercano = crearVistaRobot(UUID.randomUUID(), "Cercano",
                new Posicion(15.0, 10.0), 0.0, 0.0, 100.0, false, 1.5);
        VistaRobot enemigoMedio = crearVistaRobot(UUID.randomUUID(), "Medio",
                new Posicion(20.0, 10.0), 0.0, 0.0, 100.0, false, 1.5);
        
        ContextoPercepcion contexto = crearContexto(robotPropio,
                List.of(robotPropio, enemigoLejano, enemigoCercano, enemigoMedio));
        
        Accion accion = cazador.decidir(contexto);
        
        // Debería disparar al más cercano (distancia 5)
        assertInstanceOf(AccionDisparar.class, accion);
    }

    @Test
    @DisplayName("Comportamiento determinista con misma entrada")
    void comportamientoDeterministaConMismaEntrada() {
        VistaRobot robotPropio = crearVistaRobot(robotPropioId, "Cazador",
                new Posicion(10.0, 10.0), 0.0, 0.0, 100.0, false, 1.5);
        VistaRobot enemigo = crearVistaRobot(UUID.randomUUID(), "Enemigo",
                new Posicion(15.0, 10.0), 0.0, 0.0, 100.0, false, 1.5);
        
        ContextoPercepcion contexto = crearContexto(robotPropio,
                List.of(robotPropio, enemigo));
        
        Accion accion1 = cazador.decidir(contexto);
        Accion accion2 = cazador.decidir(contexto);
        
        assertEquals(accion1.getClass(), accion2.getClass());
        
        if (accion1 instanceof AccionGirar giro1 && accion2 instanceof AccionGirar giro2) {
            assertEquals(giro1.getAngulo(), giro2.getAngulo(), 0.000001);
        } else if (accion1 instanceof AccionMover mov1 && accion2 instanceof AccionMover mov2) {
            assertEquals(mov1.getDistancia(), mov2.getDistancia(), 0.000001);
        }
    }

    // Métodos auxiliares
    private VistaRobot crearVistaRobot(UUID id, String nombre, Posicion posicion, 
                                     double direccion, double cooldown, double vida, 
                                     boolean destruido, double radio) {
        return new VistaRobot(id, nombre, posicion, direccion, vida, destruido, cooldown, radio);
    }
    
    private ContextoPercepcion crearContexto(VistaRobot robotPropio, List<VistaRobot> todosRobots) {
        return new ContextoPercepcion() {
            @Override
            public UUID idPropio() { return robotPropio.getId(); }
            
            @Override
            public VistaRobot miEstado() { return robotPropio; }
            
            @Override
            public List<VistaRobot> todosLosRobots() { return todosRobots; }
            
            @Override
            public List<dev.maiki.thembots.domain.model.Proyectil> proyectiles() { return List.of(); }
            
            @Override
            public List<dev.maiki.thembots.domain.model.Obstaculo> obstaculos() { return List.of(); }
            
            @Override
            public double anchoArena() { return 100.0; }
            
            @Override
            public double altoArena() { return 100.0; }
            
            @Override
            public int tickActual() { return 0; }
        };
    }
}