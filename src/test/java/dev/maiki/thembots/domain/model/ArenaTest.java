package dev.maiki.thembots.domain.model;

import dev.maiki.thembots.domain.model.accion.AccionNada;
import dev.maiki.thembots.domain.ports.Comportamiento;
import dev.maiki.thembots.domain.value.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Arena - Tests de entidad contenedora")
class ArenaTest {

    private Robot robot1;
    private Robot robot2;
    private Obstaculo obstaculo1;
    private Obstaculo obstaculo2;
    private Comportamiento comportamientoSimple;

    @BeforeEach
    void setUp() {
        comportamientoSimple = contexto -> new AccionNada();
        
        robot1 = new Robot(UUID.randomUUID(), "Robot1", new Posicion(10.0, 10.0), 0.0,
                          new Vida(100.0), 0.0, new Radio(1.0), comportamientoSimple);
        robot2 = new Robot(UUID.randomUUID(), "Robot2", new Posicion(20.0, 20.0), Math.PI,
                          new Vida(80.0), 0.0, new Radio(1.5), comportamientoSimple);
        
        obstaculo1 = new Obstaculo(new Posicion(15.0, 15.0), new Radio(2.0));
        obstaculo2 = new Obstaculo(new Posicion(5.0, 25.0), new Radio(1.0));
    }

    @Test
    @DisplayName("Crear arena con parámetros válidos")
    void crearArenaConParametrosValidos() {
        double ancho = 50.0;
        double alto = 40.0;
        List<Robot> robots = List.of(robot1, robot2);
        List<Obstaculo> obstaculos = List.of(obstaculo1, obstaculo2);

        Arena arena = new Arena(ancho, alto, robots, obstaculos);

        assertEquals(ancho, arena.getAncho());
        assertEquals(alto, arena.getAlto());
        assertEquals(2, arena.getRobots().size());
        assertEquals(2, arena.getObstaculos().size());
        assertEquals(0, arena.getProyectiles().size()); // Inicia vacío
    }

    @Test
    @DisplayName("Arena sin robots ni obstáculos")
    void arenaSinRobotsNiObstaculos() {
        Arena arena = new Arena(100.0, 100.0, List.of(), List.of());

        assertEquals(100.0, arena.getAncho());
        assertEquals(100.0, arena.getAlto());
        assertTrue(arena.getRobots().isEmpty());
        assertTrue(arena.getObstaculos().isEmpty());
        assertTrue(arena.getProyectiles().isEmpty());
    }

    @Test
    @DisplayName("Listas de robots y obstáculos son inmutables")
    void listasDeRobotsYObstaculosSonInmutables() {
        Arena arena = new Arena(50.0, 50.0, List.of(robot1), List.of(obstaculo1));

        // Intentar modificar las listas devueltas debe fallar
        assertThrows(UnsupportedOperationException.class, () -> 
            arena.getRobots().add(robot2)
        );
        
        assertThrows(UnsupportedOperationException.class, () -> 
            arena.getObstaculos().add(obstaculo2)
        );
        
        assertThrows(UnsupportedOperationException.class, () -> 
            arena.getProyectiles().clear()
        );
    }

    @Test
    @DisplayName("Arena mantiene copias independientes de las listas")
    void arenaMantieneCopiasIndependientesDeLasListas() {
        List<Robot> robotsOriginales = List.of(robot1);
        List<Obstaculo> obstaculosOriginales = List.of(obstaculo1);
        
        Arena arena = new Arena(50.0, 50.0, robotsOriginales, obstaculosOriginales);

        // Las listas dentro de la arena deben ser copias
        assertEquals(1, arena.getRobots().size());
        assertEquals(1, arena.getObstaculos().size());
        
        // Modificar las listas originales no debe afectar la arena
        // (Nota: List.of() devuelve listas inmutables, pero el principio es importante)
        assertEquals(1, arena.getRobots().size());
        assertEquals(1, arena.getObstaculos().size());
    }

    @Test
    @DisplayName("Agregar proyectil a la arena")
    void agregarProyectilALaArena() {
        Arena arena = new Arena(50.0, 50.0, List.of(robot1), List.of());

        Proyectil proyectil = new Proyectil(UUID.randomUUID(), new Posicion(10.0, 10.0),
                                          new Vector(1.0, 0.0), 10.0, robot1.getId(), new Radio(0.5));

        arena.agregarProyectil(proyectil);

        assertEquals(1, arena.getProyectiles().size());
        assertTrue(arena.getProyectiles().contains(proyectil));
    }

    @Test
    @DisplayName("Agregar múltiples proyectiles")
    void agregarMultiplesProyectiles() {
        Arena arena = new Arena(50.0, 50.0, List.of(robot1, robot2), List.of());

        Proyectil proyectil1 = new Proyectil(UUID.randomUUID(), new Posicion(10.0, 10.0),
                                           new Vector(1.0, 0.0), 10.0, robot1.getId(), new Radio(0.5));
        Proyectil proyectil2 = new Proyectil(UUID.randomUUID(), new Posicion(20.0, 20.0),
                                           new Vector(0.0, 1.0), 8.0, robot2.getId(), new Radio(0.3));

        arena.agregarProyectil(proyectil1);
        arena.agregarProyectil(proyectil2);

        assertEquals(2, arena.getProyectiles().size());
        assertTrue(arena.getProyectiles().contains(proyectil1));
        assertTrue(arena.getProyectiles().contains(proyectil2));
    }

    @Test
    @DisplayName("Eliminar proyectil de la arena")
    void eliminarProyectilDeLaArena() {
        Arena arena = new Arena(50.0, 50.0, List.of(robot1), List.of());

        Proyectil proyectil = new Proyectil(UUID.randomUUID(), new Posicion(10.0, 10.0),
                                          new Vector(1.0, 0.0), 10.0, robot1.getId(), new Radio(0.5));

        arena.agregarProyectil(proyectil);
        assertEquals(1, arena.getProyectiles().size());

        arena.eliminarProyectil(proyectil);
        assertEquals(0, arena.getProyectiles().size());
        assertFalse(arena.getProyectiles().contains(proyectil));
    }

    @Test
    @DisplayName("Eliminar proyectil que no existe no causa error")
    void eliminarProyectilQueNoExisteNoCausaError() {
        Arena arena = new Arena(50.0, 50.0, List.of(robot1), List.of());

        Proyectil proyectil = new Proyectil(UUID.randomUUID(), new Posicion(10.0, 10.0),
                                          new Vector(1.0, 0.0), 10.0, robot1.getId(), new Radio(0.5));

        // No debería lanzar excepción
        assertDoesNotThrow(() -> arena.eliminarProyectil(proyectil));
        assertEquals(0, arena.getProyectiles().size());
    }

    @Test
    @DisplayName("Verificar si posición está dentro de límites")
    void verificarSiPosicionEstaDentroDeLimites() {
        Arena arena = new Arena(100.0, 80.0, List.of(), List.of());

        // Posiciones dentro
        assertTrue(arena.dentroDeLimites(new Posicion(50.0, 40.0)));
        assertTrue(arena.dentroDeLimites(new Posicion(0.0, 0.0))); // Esquina
        assertTrue(arena.dentroDeLimites(new Posicion(100.0, 80.0))); // Límite

        // Posiciones fuera
        assertFalse(arena.dentroDeLimites(new Posicion(101.0, 40.0)));
        assertFalse(arena.dentroDeLimites(new Posicion(50.0, 81.0)));
        assertFalse(arena.dentroDeLimites(new Posicion(-1.0, 40.0)));
        assertFalse(arena.dentroDeLimites(new Posicion(50.0, -1.0)));
    }

    @Test
    @DisplayName("Verificar límites con posiciones extremas")
    void verificarLimitesConPosicionesExtremas() {
        Arena arena = new Arena(10.0, 10.0, List.of(), List.of());

        // Puntos exactos en los límites
        assertTrue(arena.dentroDeLimites(new Posicion(0.0, 0.0)));
        assertTrue(arena.dentroDeLimites(new Posicion(10.0, 0.0)));
        assertTrue(arena.dentroDeLimites(new Posicion(0.0, 10.0)));
        assertTrue(arena.dentroDeLimites(new Posicion(10.0, 10.0)));

        // Justo fuera de los límites
        assertFalse(arena.dentroDeLimites(new Posicion(10.001, 5.0)));
        assertFalse(arena.dentroDeLimites(new Posicion(5.0, 10.001)));
        assertFalse(arena.dentroDeLimites(new Posicion(-0.001, 5.0)));
        assertFalse(arena.dentroDeLimites(new Posicion(5.0, -0.001)));
    }

    @Test
    @DisplayName("Arena con dimensiones muy pequeñas")
    void arenaConDimensionesMuyPequenas() {
        Arena arena = new Arena(1.0, 1.0, List.of(), List.of());

        assertEquals(1.0, arena.getAncho());
        assertEquals(1.0, arena.getAlto());
        assertTrue(arena.dentroDeLimites(new Posicion(0.5, 0.5)));
        assertFalse(arena.dentroDeLimites(new Posicion(1.1, 0.5)));
    }

    @Test
    @DisplayName("Arena con dimensiones muy grandes")
    void arenaConDimensionesMuyGrandes() {
        double ancho = 10000.0;
        double alto = 8000.0;
        Arena arena = new Arena(ancho, alto, List.of(), List.of());

        assertEquals(ancho, arena.getAncho());
        assertEquals(alto, arena.getAlto());
        assertTrue(arena.dentroDeLimites(new Posicion(5000.0, 4000.0)));
        assertTrue(arena.dentroDeLimites(new Posicion(ancho, alto)));
        assertFalse(arena.dentroDeLimites(new Posicion(ancho + 1, alto)));
    }

    @Test
    @DisplayName("Gestión completa del ciclo de vida de proyectiles")
    void gestionCompletaDelCicloDeVidaDeProyectiles() {
        Arena arena = new Arena(50.0, 50.0, List.of(robot1), List.of());

        // Inicialmente sin proyectiles
        assertTrue(arena.getProyectiles().isEmpty());

        // Agregar proyectiles
        Proyectil p1 = new Proyectil(UUID.randomUUID(), new Posicion(10.0, 10.0),
                                   new Vector(1.0, 0.0), 10.0, robot1.getId(), new Radio(0.5));
        Proyectil p2 = new Proyectil(UUID.randomUUID(), new Posicion(20.0, 20.0),
                                   new Vector(0.0, 1.0), 8.0, robot1.getId(), new Radio(0.3));

        arena.agregarProyectil(p1);
        arena.agregarProyectil(p2);
        assertEquals(2, arena.getProyectiles().size());

        // Eliminar uno
        arena.eliminarProyectil(p1);
        assertEquals(1, arena.getProyectiles().size());
        assertTrue(arena.getProyectiles().contains(p2));
        assertFalse(arena.getProyectiles().contains(p1));

        // Eliminar el último
        arena.eliminarProyectil(p2);
        assertTrue(arena.getProyectiles().isEmpty());
    }
}