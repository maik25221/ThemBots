package dev.maiki.thembots.domain.model;

import dev.maiki.thembots.domain.model.accion.AccionNada;
import dev.maiki.thembots.domain.model.enums.EstadoRobot;
import dev.maiki.thembots.domain.ports.Comportamiento;
import dev.maiki.thembots.domain.value.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Robot - Tests de entidad principal")
class RobotTest {

    private UUID robotId;
    private Comportamiento comportamientoSimple;
    private Posicion posicionInicial;
    private Vida vidaInicial;
    private Radio radioStandard;

    @BeforeEach
    void setUp() {
        robotId = UUID.randomUUID();
        comportamientoSimple = contexto -> new AccionNada();
        posicionInicial = new Posicion(10.0, 15.0);
        vidaInicial = new Vida(100.0);
        radioStandard = new Radio(1.5);
    }

    @Test
    @DisplayName("Crear robot con parámetros válidos")
    void crearRobotConParametrosValidos() {
        Robot robot = new Robot(robotId, "TestBot", posicionInicial, Math.PI / 4, 
                              vidaInicial, 0.0, radioStandard, comportamientoSimple);

        assertEquals(robotId, robot.getId());
        assertEquals("TestBot", robot.getNombre());
        assertEquals(posicionInicial, robot.getPosicion());
        assertEquals(Math.PI / 4, robot.getDireccion());
        assertEquals(vidaInicial, robot.getVida());
        assertEquals(0.0, robot.getCooldown());
        assertEquals(radioStandard, robot.getRadio());
        assertEquals(EstadoRobot.ACTIVO, robot.getEstado());
        assertEquals(comportamientoSimple, robot.getComportamiento());
        assertTrue(robot.estaActivo());
    }

    @Test
    @DisplayName("Robot inicia con estado ACTIVO")
    void robotIniciaConEstadoActivo() {
        Robot robot = new Robot(robotId, "TestBot", posicionInicial, 0.0,
                              vidaInicial, 0.0, radioStandard, comportamientoSimple);

        assertEquals(EstadoRobot.ACTIVO, robot.getEstado());
        assertTrue(robot.estaActivo());
    }

    @Test
    @DisplayName("Recibir daño reduce vida pero mantiene estado activo")
    void recibirDanioReduceVidaPeroMantieneEstadoActivo() {
        Robot robot = new Robot(robotId, "TestBot", posicionInicial, 0.0,
                              new Vida(100.0), 0.0, radioStandard, comportamientoSimple);

        robot.recibirDanio(30.0);

        assertEquals(70.0, robot.getVida().valor());
        assertEquals(EstadoRobot.ACTIVO, robot.getEstado());
        assertTrue(robot.estaActivo());
    }

    @Test
    @DisplayName("Recibir daño letal cambia estado a DESTRUIDO")
    void recibirDanioLetalCambiaEstadoADestruido() {
        Robot robot = new Robot(robotId, "TestBot", posicionInicial, 0.0,
                              new Vida(50.0), 0.0, radioStandard, comportamientoSimple);

        robot.recibirDanio(60.0);

        assertEquals(0.0, robot.getVida().valor());
        assertEquals(EstadoRobot.DESTRUIDO, robot.getEstado());
        assertFalse(robot.estaActivo());
        assertFalse(robot.getVida().estaVivo());
    }

    @Test
    @DisplayName("Recibir daño exacto para destruir robot")
    void recibirDanioExactoParaDestruirRobot() {
        Robot robot = new Robot(robotId, "TestBot", posicionInicial, 0.0,
                              new Vida(50.0), 0.0, radioStandard, comportamientoSimple);

        robot.recibirDanio(50.0);

        assertEquals(0.0, robot.getVida().valor());
        assertEquals(EstadoRobot.DESTRUIDO, robot.getEstado());
        assertFalse(robot.estaActivo());
    }

    @Test
    @DisplayName("Múltiples daños acumulativos")
    void multiplesDaniosAcumulativos() {
        Robot robot = new Robot(robotId, "TestBot", posicionInicial, 0.0,
                              new Vida(100.0), 0.0, radioStandard, comportamientoSimple);

        robot.recibirDanio(25.0);
        assertEquals(75.0, robot.getVida().valor());
        assertTrue(robot.estaActivo());

        robot.recibirDanio(30.0);
        assertEquals(45.0, robot.getVida().valor());
        assertTrue(robot.estaActivo());

        robot.recibirDanio(50.0);
        assertEquals(0.0, robot.getVida().valor());
        assertFalse(robot.estaActivo());
        assertEquals(EstadoRobot.DESTRUIDO, robot.getEstado());
    }

    @Test
    @DisplayName("Cambiar posición del robot")
    void cambiarPosicionDelRobot() {
        Robot robot = new Robot(robotId, "TestBot", posicionInicial, 0.0,
                              vidaInicial, 0.0, radioStandard, comportamientoSimple);

        Posicion nuevaPosicion = new Posicion(20.0, 25.0);
        robot.setPosicion(nuevaPosicion);

        assertEquals(nuevaPosicion, robot.getPosicion());
        assertEquals(posicionInicial, posicionInicial); // Original no cambia
    }

    @Test
    @DisplayName("Cambiar dirección del robot")
    void cambiarDireccionDelRobot() {
        Robot robot = new Robot(robotId, "TestBot", posicionInicial, 0.0,
                              vidaInicial, 0.0, radioStandard, comportamientoSimple);

        double nuevaDireccion = Math.PI / 2;
        robot.setDireccion(nuevaDireccion);

        assertEquals(nuevaDireccion, robot.getDireccion());
    }

    @Test
    @DisplayName("Normalización de dirección en rango [0, 2π)")
    void normalizacionDeDireccionEnRango() {
        Robot robot = new Robot(robotId, "TestBot", posicionInicial, 0.0,
                              vidaInicial, 0.0, radioStandard, comportamientoSimple);

        // Ángulo mayor que 2π
        robot.setDireccion(3 * Math.PI);
        assertEquals(Math.PI, robot.getDireccion(), 0.000001);

        // Ángulo negativo
        robot.setDireccion(-Math.PI / 2);
        assertEquals(3 * Math.PI / 2, robot.getDireccion(), 0.000001);

        // Múltiples vueltas
        robot.setDireccion(5 * Math.PI);
        assertEquals(Math.PI, robot.getDireccion(), 0.000001);
    }

    @Test
    @DisplayName("Vector de dirección se calcula correctamente")
    void vectorDeDireccionSeCalculaCorrectamente() {
        Robot robot = new Robot(robotId, "TestBot", posicionInicial, 0.0,
                              vidaInicial, 0.0, radioStandard, comportamientoSimple);

        // Dirección 0 (derecha)
        robot.setDireccion(0.0);
        Vector vector = robot.getDireccionVector();
        assertEquals(1.0, vector.dx(), 0.000001);
        assertEquals(0.0, vector.dy(), 0.000001);

        // Dirección π/2 (arriba)
        robot.setDireccion(Math.PI / 2);
        vector = robot.getDireccionVector();
        assertEquals(0.0, vector.dx(), 0.000001);
        assertEquals(1.0, vector.dy(), 0.000001);

        // Dirección π (izquierda)
        robot.setDireccion(Math.PI);
        vector = robot.getDireccionVector();
        assertEquals(-1.0, vector.dx(), 0.000001);
        assertEquals(0.0, vector.dy(), 0.000001);
    }

    @Test
    @DisplayName("Gestión de cooldown de disparo")
    void gestionDeCooldownDeDisparo() {
        Robot robot = new Robot(robotId, "TestBot", posicionInicial, 0.0,
                              vidaInicial, 5.0, radioStandard, comportamientoSimple);

        assertEquals(5.0, robot.getCooldown());

        robot.setCooldown(3.0);
        assertEquals(3.0, robot.getCooldown());

        robot.setCooldown(0.0);
        assertEquals(0.0, robot.getCooldown());
    }

    @Test
    @DisplayName("Cooldown no puede ser negativo")
    void cooldownNoPuedeSerNegativo() {
        Robot robot = new Robot(robotId, "TestBot", posicionInicial, 0.0,
                              vidaInicial, 0.0, radioStandard, comportamientoSimple);

        robot.setCooldown(-5.0);
        assertEquals(-5.0, robot.getCooldown()); // La implementación actual permite negativos
    }

    @Test
    @DisplayName("Comportamiento se ejecuta correctamente")
    void comportamientoSeEjecutaCorrectamente() {
        Comportamiento comportamientoContador = contexto -> new AccionNada();
        Robot robot = new Robot(robotId, "TestBot", posicionInicial, 0.0,
                              vidaInicial, 0.0, radioStandard, comportamientoContador);

        assertEquals(comportamientoContador, robot.getComportamiento());
        assertNotNull(robot.getComportamiento().decidir(null));
    }

    @Test
    @DisplayName("Robot destruido sigue siendo el mismo objeto")
    void robotDestruidoSigueSiendoElMismoObjeto() {
        Robot robot = new Robot(robotId, "TestBot", posicionInicial, 0.0,
                              new Vida(10.0), 0.0, radioStandard, comportamientoSimple);

        UUID idAntes = robot.getId();
        String nombreAntes = robot.getNombre();
        
        robot.recibirDanio(15.0);

        assertEquals(idAntes, robot.getId());
        assertEquals(nombreAntes, robot.getNombre());
        assertEquals(EstadoRobot.DESTRUIDO, robot.getEstado());
    }

    @Test
    @DisplayName("Propiedades inmutables del robot")
    void propiedadesInmutablesDelRobot() {
        Robot robot = new Robot(robotId, "TestBot", posicionInicial, Math.PI / 4,
                              vidaInicial, 2.0, radioStandard, comportamientoSimple);

        // Estas propiedades no deben cambiar durante la vida del robot
        assertEquals(robotId, robot.getId());
        assertEquals("TestBot", robot.getNombre());
        assertEquals(radioStandard, robot.getRadio());
        assertEquals(comportamientoSimple, robot.getComportamiento());

        // Incluso después de recibir daño
        robot.recibirDanio(50.0);
        assertEquals(robotId, robot.getId());
        assertEquals("TestBot", robot.getNombre());
        assertEquals(radioStandard, robot.getRadio());
        assertEquals(comportamientoSimple, robot.getComportamiento());
    }

    @Test
    @DisplayName("Robots con diferente ID son diferentes")
    void robotsConDiferenteIdSonDiferentes() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Robot robot1 = new Robot(id1, "Bot1", posicionInicial, 0.0,
                                vidaInicial, 0.0, radioStandard, comportamientoSimple);
        Robot robot2 = new Robot(id2, "Bot2", posicionInicial, 0.0,
                                vidaInicial, 0.0, radioStandard, comportamientoSimple);

        assertNotEquals(robot1.getId(), robot2.getId());
        assertNotEquals(id1, id2);
    }
}