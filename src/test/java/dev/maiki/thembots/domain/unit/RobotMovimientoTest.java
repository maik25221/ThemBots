package dev.maiki.thembots.domain.unit;

import dev.maiki.thembots.domain.model.Arena;
import dev.maiki.thembots.domain.model.Obstaculo;
import dev.maiki.thembots.domain.model.Robot;
import dev.maiki.thembots.domain.model.accion.AccionMover;
import dev.maiki.thembots.domain.model.accion.AccionNada;
import dev.maiki.thembots.domain.model.enums.EstadoRobot;
import dev.maiki.thembots.domain.service.SimuladorCombate;
import dev.maiki.thembots.domain.value.Posicion;
import dev.maiki.thembots.domain.value.Radio;
import dev.maiki.thembots.domain.value.Vida;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RobotMovimientoTest {

    @Test
    public void testRobotAvanzaEnDireccionCorrecta() {
        // Robot que siempre avanza 5 unidades hacia la derecha (0 radianes)
        Robot robot = new Robot(UUID.randomUUID(), "Mover",
                                new Posicion(10, 10), 0,
                                new Vida(100), 0, new Radio(1),
                                contexto -> new AccionMover(5));

        Arena arena = new Arena(100, 100, List.of(robot), List.of());
        SimuladorCombate simulador = new SimuladorCombate(arena, 1);

        simulador.simularTick();

        Posicion posFinal = robot.getPosicion();
        assertEquals(15, posFinal.getX(), 0.0001);
        assertEquals(10, posFinal.getY(), 0.0001);
    }

    @Test
    public void testRobotNoSaleDeLimites() {
        // Robot en el borde, intenta salir de la arena
        Robot robot = new Robot(UUID.randomUUID(), "Borde",
                                new Posicion(99, 50), 0,
                                new Vida(100), 0, new Radio(1),
                                contexto -> new AccionMover(5));

        Arena arena = new Arena(100, 100, List.of(robot), List.of());
        SimuladorCombate simulador = new SimuladorCombate(arena, 1);

        simulador.simularTick();

        // No debe haber salido de la arena
        Posicion posFinal = robot.getPosicion();
        assertTrue(posFinal.getX() <= 100);
    }

    @Test
    public void testRobotNoColisionaConObstaculo() {
        // Robot intenta avanzar hacia un obstáculo
        Robot robot = new Robot(UUID.randomUUID(), "Movil",
                                new Posicion(10, 10), 0,
                                new Vida(100), 0, new Radio(1),
                                contexto -> new AccionMover(5));

        Obstaculo obstaculo = new Obstaculo(new Posicion(15, 10), new Radio(1.5));
        Arena arena = new Arena(100, 100, List.of(robot), List.of(obstaculo));
        SimuladorCombate simulador = new SimuladorCombate(arena, 1);

        simulador.simularTick();

        // El robot debe haberse detenido antes del obstáculo
        Posicion posFinal = robot.getPosicion();
        assertTrue(posFinal.getX() < 15);
    }

    @Test
    public void testRobotNoColisionaConOtroRobot() {
        // Dos robots, uno intenta avanzar hacia el otro
        Robot robot1 = new Robot(UUID.randomUUID(), "A",
                                 new Posicion(10, 10), 0,
                                 new Vida(100), 0, new Radio(1),
                                 contexto -> new AccionMover(5));
        Robot robot2 = new Robot(UUID.randomUUID(), "B",
                                 new Posicion(14, 10), 0,
                                 new Vida(100), 0, new Radio(1),
                                 contexto -> new AccionNada());

        Arena arena = new Arena(100, 100, List.of(robot1, robot2), List.of());
        SimuladorCombate simulador = new SimuladorCombate(arena, 1);

        simulador.simularTick();

        // robot1 no debe haber colisionado con robot2
        assertTrue(robot1.getPosicion().getX() < 14);
    }

    @Test
    public void testRobotPuedeRetroceder() {
        // Robot que retrocede (distancia negativa)
        Robot robot = new Robot(UUID.randomUUID(), "Retroceso",
                                new Posicion(20, 20), 0,
                                new Vida(100), 0, new Radio(1),
                                contexto -> new AccionMover(-3));

        Arena arena = new Arena(100, 100, List.of(robot), List.of());
        SimuladorCombate simulador = new SimuladorCombate(arena, 1);

        simulador.simularTick();

        // Debe haber retrocedido en X
        assertEquals(17, robot.getPosicion().getX(), 0.0001);
        assertEquals(20, robot.getPosicion().getY(), 0.0001);
    }

    @Test
    public void testRobotNoSeMueveSiAccionNada() {
        // Robot que no se mueve
        Posicion inicial = new Posicion(30, 30);
        Robot robot = new Robot(UUID.randomUUID(), "Quieto",
                                inicial, 0,
                                new Vida(100), 0, new Radio(1),
                                contexto -> new AccionNada());

        Arena arena = new Arena(100, 100, List.of(robot), List.of());
        SimuladorCombate simulador = new SimuladorCombate(arena, 1);

        simulador.simularTick();

        assertEquals(inicial, robot.getPosicion());
    }

    @Test
    public void testRobotNoSeMueveSiEstaDestruido() {
        // Robot destruido no debe moverse aunque su comportamiento lo indique
        Robot robot = new Robot(UUID.randomUUID(), "Destruido",
                                new Posicion(40, 40), 0,
                                new Vida(0), 0, new Radio(1),
                                contexto -> new AccionMover(5));
        // Forzar estado destruido
        robot.recibirDanio(100);

        Arena arena = new Arena(100, 100, List.of(robot), List.of());
        SimuladorCombate simulador = new SimuladorCombate(arena, 1);

        simulador.simularTick();

        // No debe haberse movido
        assertEquals(40, robot.getPosicion().getX(), 0.0001);
        assertEquals(40, robot.getPosicion().getY(), 0.0001);
        assertEquals(EstadoRobot.DESTRUIDO, robot.getEstado());
    }

    @Test
    public void testRobotMovimientoDiagonal() {
        // Robot con dirección diagonal (PI/4 radianes)
        double angulo = Math.PI / 4;
        Robot robot = new Robot(UUID.randomUUID(), "Diagonal",
                                new Posicion(0, 0), angulo,
                                new Vida(100), 0, new Radio(1),
                                contexto -> new AccionMover(Math.sqrt(2)));

        Arena arena = new Arena(100, 100, List.of(robot), List.of());
        SimuladorCombate simulador = new SimuladorCombate(arena, 1);

        simulador.simularTick();

        // Debe haber avanzado 1 en X y 1 en Y
        assertEquals(1, robot.getPosicion().getX(), 0.0001);
        assertEquals(1, robot.getPosicion().getY(), 0.0001);
    }
}
