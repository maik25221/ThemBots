package dev.maiki.thembots;

import dev.maiki.thembots.domain.model.Arena;
import dev.maiki.thembots.domain.model.Obstaculo;
import dev.maiki.thembots.domain.model.ResultadoCombate;
import dev.maiki.thembots.domain.model.Robot;
import dev.maiki.thembots.domain.model.accion.AccionNada;
import dev.maiki.thembots.domain.model.enums.EstadoRobot;
import dev.maiki.thembots.domain.port.Comportamiento;
import dev.maiki.thembots.domain.service.SimuladorCombate;
import dev.maiki.thembots.domain.value.Posicion;
import dev.maiki.thembots.domain.value.Radio;
import dev.maiki.thembots.domain.value.Vida;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class SimuladorCombateTest {

    @Test
    public void testCombateConUnSoloRobotTerminaInmediatamente() {
        Robot robot = new Robot(UUID.randomUUID(), "Bot1",
                                new Posicion(10, 10), 0,
                                new Vida(100), 0, new Radio(1), contexto -> new AccionNada());

        Arena arena = new Arena(100, 100, List.of(robot), List.of());
        SimuladorCombate simulador = new SimuladorCombate(arena, 10);

        simulador.simularHastaElFinal();

        assertTrue(simulador.estaTerminado());
        //Tiene que utilizar un tick para calcular que termino
        assertEquals(1, simulador.getTickActual());
        assertNotNull(simulador.resultadoFinal());
    }

    @Test
    public void testRobotDisparandoDestruyeAlOtro() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Comportamiento atacante = contexto -> new dev.maiki.thembots.domain.model.accion.AccionDisparar();
        Comportamiento victima = contexto -> new dev.maiki.thembots.domain.model.accion.AccionNada();

        Robot robot1 = new Robot(id1, "Atacante", new Posicion(10, 10), 0,
                                 new Vida(100), 0, new Radio(1), atacante);
        Robot robot2 = new Robot(id2, "Defensor", new Posicion(12, 10), Math.PI,
                                 new Vida(10), 0, new Radio(1), victima);

        Arena arena = new Arena(100, 100, List.of(robot1, robot2), List.of());
        SimuladorCombate simulador = new SimuladorCombate(arena, 5);
        simulador.simularHastaElFinal();

        ResultadoCombate resultado = simulador.resultadoFinal();

        assertTrue(resultado.getRanking().stream().anyMatch(r -> r.getNombre().equals("Defensor") && r.isDestruido()));
        assertFalse(resultado.isEmpate());
        assertEquals("Atacante", resultado.getRanking().get(0).getNombre());
        assertEquals(EstadoRobot.DESTRUIDO, robot2.getEstado());
    }

    @Test
    public void testMovimientoEvitaObstaculo() {
        Robot robot = new Robot(UUID.randomUUID(), "BotMovil",
                                new Posicion(10, 10), 0,
                                new Vida(100), 0, new Radio(1),
                                contexto -> new dev.maiki.thembots.domain.model.accion.AccionMover(5));

        Obstaculo obstaculo = new Obstaculo(new Posicion(15, 10), new Radio(1.5));
        Arena arena = new Arena(100, 100, List.of(robot), List.of(obstaculo));

        SimuladorCombate simulador = new SimuladorCombate(arena, 3);
        simulador.simularHastaElFinal();

        Posicion finalPos = robot.getPosicion();
        assertTrue(finalPos.getX() < 15); // Debería haberse detenido antes del obstáculo
    }
}
