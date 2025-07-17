package dev.maiki.thembots;

import dev.maiki.thembots.domain.model.Arena;
import dev.maiki.thembots.domain.model.Obstaculo;
import dev.maiki.thembots.domain.model.ResultadoCombate;
import dev.maiki.thembots.domain.model.Robot;
import dev.maiki.thembots.domain.model.accion.AccionNada;
import dev.maiki.thembots.domain.model.enums.EstadoRobot;
import dev.maiki.thembots.domain.ports.Comportamiento;
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


    @Test
    public void testSimulacionTresComportamientos() {
        // Comportamientos
        Comportamiento cazador = new dev.maiki.thembots.domain.ia.ComportamientoCazador();
        Comportamiento cobarde = new dev.maiki.thembots.domain.ia.ComportamientoCobarde();
        Comportamiento patrulla = new dev.maiki.thembots.domain.ia.ComportamientoPatrulla(
            new Posicion(5, 5), new Posicion(35, 15)
        );

        // Robots con posiciones separadas
        Robot botCazador = new Robot(UUID.randomUUID(), "Cazador",
                                     new Posicion(5, 5), 0,
                                     new Vida(100), 0, new Radio(1), cazador);

        Robot botCobarde = new Robot(UUID.randomUUID(), "Cobarde",
                                     new Posicion(35, 5), Math.PI,
                                     new Vida(100), 0, new Radio(1), cobarde);

        Robot botPatrulla = new Robot(UUID.randomUUID(), "Patrulla",
                                      new Posicion(20, 15), Math.PI / 2,
                                      new Vida(100), 0, new Radio(1), patrulla);

        Arena arena = new Arena(40, 20, List.of(botCazador, botCobarde, botPatrulla), List.of());
        SimuladorCombate simulador = new SimuladorCombate(arena, 100);

        simulador.simularHastaElFinal();
        ResultadoCombate resultado = simulador.resultadoFinal();

        // Verifica que todos los bots están en el ranking
        assertEquals(3, resultado.getRanking().size());

        // Imprime el ranking para observación manual
        System.out.println("Ranking final:");
        resultado.getRanking().forEach(r -> {
            System.out.printf("  - %s (Vida: %.1f, Destruido: %s)\n", r.getNombre(), r.getVidaFinal(), r.isDestruido());
        });

        // Debe haber un ganador o empate
        assertTrue(resultado.getGanadorId() != null || resultado.isEmpate());
    }
}
