package dev.maiki.thembots.domain.service;

import dev.maiki.thembots.domain.model.Obstaculo;
import dev.maiki.thembots.domain.model.Robot;
import dev.maiki.thembots.domain.model.accion.AccionNada;
import dev.maiki.thembots.domain.ports.Comportamiento;
import dev.maiki.thembots.domain.value.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MotorDeColisiones - Tests de servicio de dominio")
class MotorDeColisionesTest {

    private MotorDeColisiones motor;
    private Comportamiento comportamientoSimple;
    private Robot robot1;
    private Robot robot2;
    private Robot robot3;
    private Obstaculo obstaculo1;
    private Obstaculo obstaculo2;

    @BeforeEach
    void setUp() {
        motor = new MotorDeColisiones();
        comportamientoSimple = contexto -> new AccionNada();

        robot1 = new Robot(UUID.randomUUID(), "Robot1", new Posicion(10.0, 10.0), 0.0,
                          new Vida(100.0), 0.0, new Radio(2.0), comportamientoSimple);
        robot2 = new Robot(UUID.randomUUID(), "Robot2", new Posicion(20.0, 10.0), 0.0,
                          new Vida(100.0), 0.0, new Radio(1.5), comportamientoSimple);
        robot3 = new Robot(UUID.randomUUID(), "Robot3", new Posicion(30.0, 30.0), 0.0,
                          new Vida(100.0), 0.0, new Radio(1.0), comportamientoSimple);

        obstaculo1 = new Obstaculo(new Posicion(15.0, 15.0), new Radio(2.5));
        obstaculo2 = new Obstaculo(new Posicion(25.0, 5.0), new Radio(1.0));
    }

    @Test
    @DisplayName("Sin colisión cuando no hay robots ni obstáculos")
    void sinColisionCuandoNoHayRobotsNiObstaculos() {
        Posicion posicion = new Posicion(15.0, 15.0);
        Radio radio = new Radio(1.0);

        boolean hayColision = motor.hayColision(posicion, radio, List.of(), List.of());

        assertFalse(hayColision);
    }

    @Test
    @DisplayName("Sin colisión cuando robots están separados")
    void sinColisionCuandoRobotsEstanSeparados() {
        Posicion nuevaPosicion = new Posicion(50.0, 50.0); // Muy lejos
        Radio radio = new Radio(1.0);
        List<Robot> otrosRobots = List.of(robot1, robot2);

        boolean hayColision = motor.hayColision(nuevaPosicion, radio, otrosRobots, List.of());

        assertFalse(hayColision);
    }

    @Test
    @DisplayName("Colisión detectada con robot cercano")
    void colisionDetectadaConRobotCercano() {
        // Robot1 está en (10, 10) con radio 2.0
        // Intentamos mover a (11, 10) con radio 1.5
        // Distancia = 1.0, suma radios = 3.5 -> colisión
        Posicion nuevaPosicion = new Posicion(11.0, 10.0);
        Radio radio = new Radio(1.5);
        List<Robot> otrosRobots = List.of(robot1);

        boolean hayColision = motor.hayColision(nuevaPosicion, radio, otrosRobots, List.of());

        assertTrue(hayColision);
    }

    @Test
    @DisplayName("Colisión exacta en el límite")
    void colisionExactaEnElLimite() {
        // Robot1 en (10, 10) con radio 2.0
        // Mover a (13.5, 10) con radio 1.5
        // Distancia = 3.5, suma radios = 3.5 -> colisión exacta
        Posicion nuevaPosicion = new Posicion(13.5, 10.0);
        Radio radio = new Radio(1.5);
        List<Robot> otrosRobots = List.of(robot1);

        boolean hayColision = motor.hayColision(nuevaPosicion, radio, otrosRobots, List.of());

        assertTrue(hayColision);
    }

    @Test
    @DisplayName("Sin colisión justo fuera del límite")
    void sinColisionJustoFueraDelLimite() {
        // Robot1 en (10, 10) con radio 2.0
        // Mover a (13.6, 10) con radio 1.5
        // Distancia = 3.6, suma radios = 3.5 -> sin colisión
        Posicion nuevaPosicion = new Posicion(13.6, 10.0);
        Radio radio = new Radio(1.5);
        List<Robot> otrosRobots = List.of(robot1);

        boolean hayColision = motor.hayColision(nuevaPosicion, radio, otrosRobots, List.of());

        assertFalse(hayColision);
    }

    @Test
    @DisplayName("Colisión con múltiples robots")
    void colisionConMultiplesRobots() {
        // Posición que no colisiona con robot1 pero sí con robot2
        Posicion nuevaPosicion = new Posicion(18.0, 10.0);
        Radio radio = new Radio(1.0);
        List<Robot> otrosRobots = List.of(robot1, robot2); // robot2 en (20, 10) radio 1.5

        boolean hayColision = motor.hayColision(nuevaPosicion, radio, otrosRobots, List.of());

        assertTrue(hayColision); // Colisiona con robot2
    }

    @Test
    @DisplayName("Sin colisión con robot destruido")
    void sinColisionConRobotDestruido() {
        // Destruir robot1
        robot1.recibirDanio(200.0);
        assertFalse(robot1.estaActivo());

        Posicion nuevaPosicion = new Posicion(11.0, 10.0);
        Radio radio = new Radio(1.5);
        List<Robot> otrosRobots = List.of(robot1);

        boolean hayColision = motor.hayColision(nuevaPosicion, radio, otrosRobots, List.of());

        assertFalse(hayColision); // Robot destruido no causa colisión
    }

    @Test
    @DisplayName("Colisión con obstáculo")
    void colisionConObstaculo() {
        // Obstaculo1 en (15, 15) con radio 2.5
        // Intentar mover a (16, 15) con radio 1.0
        // Distancia = 1.0, suma radios = 3.5 -> colisión
        Posicion nuevaPosicion = new Posicion(16.0, 15.0);
        Radio radio = new Radio(1.0);
        List<Obstaculo> obstaculos = List.of(obstaculo1);

        boolean hayColision = motor.hayColision(nuevaPosicion, radio, List.of(), obstaculos);

        assertTrue(hayColision);
    }

    @Test
    @DisplayName("Sin colisión con obstáculo distante")
    void sinColisionConObstaculoDistante() {
        // Obstaculo1 en (15, 15) con radio 2.5
        // Mover a (5, 5) con radio 1.0
        // Distancia > suma de radios -> sin colisión
        Posicion nuevaPosicion = new Posicion(5.0, 5.0);
        Radio radio = new Radio(1.0);
        List<Obstaculo> obstaculos = List.of(obstaculo1);

        boolean hayColision = motor.hayColision(nuevaPosicion, radio, List.of(), obstaculos);

        assertFalse(hayColision);
    }

    @Test
    @DisplayName("Colisión con múltiples obstáculos")
    void colisionConMultiplesObstaculos() {
        Posicion nuevaPosicion = new Posicion(24.0, 5.0);
        Radio radio = new Radio(1.5);
        List<Obstaculo> obstaculos = List.of(obstaculo1, obstaculo2);

        boolean hayColision = motor.hayColision(nuevaPosicion, radio, List.of(), obstaculos);

        assertTrue(hayColision); // Colisiona con obstaculo2
    }

    @Test
    @DisplayName("Colisión compleja con robots y obstáculos")
    void colisionComplejaConRobotsYObstaculos() {
        // Posición que no colisiona con robots pero sí con obstáculo
        Posicion nuevaPosicion = new Posicion(13.0, 13.0);
        Radio radio = new Radio(2.0);
        List<Robot> robots = List.of(robot1, robot2);
        List<Obstaculo> obstaculos = List.of(obstaculo1);

        boolean hayColision = motor.hayColision(nuevaPosicion, radio, robots, obstaculos);

        assertTrue(hayColision); // Colisiona con obstaculo1
    }

    @Test
    @DisplayName("Sin colisión en posición libre")
    void sinColisionEnPosicionLibre() {
        Posicion nuevaPosicion = new Posicion(0.0, 0.0);
        Radio radio = new Radio(1.0);
        List<Robot> robots = List.of(robot1, robot2, robot3);
        List<Obstaculo> obstaculos = List.of(obstaculo1, obstaculo2);

        boolean hayColision = motor.hayColision(nuevaPosicion, radio, robots, obstaculos);

        assertFalse(hayColision);
    }

    @Test
    @DisplayName("Colisión con radio muy grande")
    void colisionConRadioMuyGrande() {
        Posicion nuevaPosicion = new Posicion(0.0, 0.0);
        Radio radioEnorme = new Radio(50.0); // Radio que abarca todo
        List<Robot> robots = List.of(robot1);

        boolean hayColision = motor.hayColision(nuevaPosicion, radioEnorme, robots, List.of());

        assertTrue(hayColision);
    }

    @Test
    @DisplayName("Cálculo preciso de distancias")
    void calculoPrecisoDeDistancias() {
        // Caso específico con números exactos
        Robot robotEspecifico = new Robot(UUID.randomUUID(), "Especifico", 
                                        new Posicion(0.0, 0.0), 0.0,
                                        new Vida(100.0), 0.0, new Radio(1.0), 
                                        comportamientoSimple);

        // Triángulo 3-4-5: distancia será exactamente 5.0
        Posicion nuevaPosicion = new Posicion(3.0, 4.0);
        Radio radio = new Radio(4.0); // suma total = 5.0
        
        // Distancia = 5.0, suma radios = 5.0 -> colisión exacta
        boolean hayColision = motor.hayColision(nuevaPosicion, radio, 
                                              List.of(robotEspecifico), List.of());

        assertTrue(hayColision);

        // Con radio ligeramente menor
        radio = new Radio(3.99);
        hayColision = motor.hayColision(nuevaPosicion, radio, 
                                      List.of(robotEspecifico), List.of());

        assertFalse(hayColision);
    }

    @Test
    @DisplayName("Robot con radio cero")
    void robotConRadioCero() {
        // Crear robot con radio mínimo (el constructor de Radio requiere > 0)
        Robot robotPequeno = new Robot(UUID.randomUUID(), "Pequeno",
                                     new Posicion(10.0, 10.0), 0.0,
                                     new Vida(100.0), 0.0, new Radio(0.1),
                                     comportamientoSimple);

        Posicion nuevaPosicion = new Posicion(10.0, 10.0); // Misma posición
        Radio radio = new Radio(0.1);

        boolean hayColision = motor.hayColision(nuevaPosicion, radio, 
                                              List.of(robotPequeno), List.of());

        assertTrue(hayColision); // Distancia 0, suma radios 0.2 -> colisión
    }
}