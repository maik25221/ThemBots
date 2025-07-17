package dev.maiki.thembots;

import dev.maiki.thembots.application.service.ConsolaSerializadorSimulacion;
import dev.maiki.thembots.domain.model.Arena;
import dev.maiki.thembots.domain.model.Obstaculo;
import dev.maiki.thembots.domain.model.Robot;
import dev.maiki.thembots.domain.model.accion.AccionDisparar;
import dev.maiki.thembots.domain.model.accion.AccionNada;
import dev.maiki.thembots.domain.ports.Comportamiento;
import dev.maiki.thembots.domain.service.SimuladorCombate;
import dev.maiki.thembots.domain.value.Posicion;
import dev.maiki.thembots.domain.value.Radio;
import dev.maiki.thembots.domain.value.Vida;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConsolaSerializadorSimulacionTest {

    @Test
    public void testVisualizacionSimulacion() {
        // Robot 1: Siempre dispara
        Comportamiento atacante = contexto -> new AccionDisparar();
        // Robot 2: No hace nada
        Comportamiento victima = contexto -> new AccionNada();

        Robot robot1 = new Robot(UUID.randomUUID(), "Atacante", new Posicion(10, 10), 0,
                                 new Vida(100), 0, new Radio(1), atacante);
        Robot robot2 = new Robot(UUID.randomUUID(), "Defensor", new Posicion(12, 10), Math.PI,
                                 new Vida(20), 0, new Radio(1), victima);

        Arena arena = new Arena(20, 20, List.of(robot1, robot2), List.of(new Obstaculo(new Posicion(15, 10), new Radio(1.5))));
        SimuladorCombate simulador = new SimuladorCombate(arena, 10);
        simulador.simularHastaElFinal();

        ConsolaSerializadorSimulacion serializador = new ConsolaSerializadorSimulacion();
        serializador.imprimirSimulacion(arena, simulador.historialCompleto(), simulador.resultadoFinal());
    }

    @Test
    public void testVisualizacionSimulacion100Ticks() {
        // Comportamientos
        Comportamiento cazador = new dev.maiki.thembots.domain.ia.ComportamientoCazador();
        Comportamiento cobarde = new dev.maiki.thembots.domain.ia.ComportamientoCobarde();
        Comportamiento patrulla = new dev.maiki.thembots.domain.ia.ComportamientoPatrulla(
            new Posicion(5, 5), new Posicion(35, 15)
        );

        Robot botCazador = new Robot(UUID.randomUUID(), "Asesino",
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

        // Guardar posiciones iniciales
        Posicion posCazadorInicial = botCazador.getPosicion();
        Posicion posCobardeInicial = botCobarde.getPosicion();
        Posicion posPatrullaInicial = botPatrulla.getPosicion();

        simulador.simularHastaElFinal();

        ConsolaSerializadorSimulacion serializador = new ConsolaSerializadorSimulacion();
        serializador.imprimirSimulacion(arena, simulador.historialCompleto(), simulador.resultadoFinal());

        // Verifica que al menos uno se haya movido
        boolean algunMovimiento = !botCazador.getPosicion().equals(posCazadorInicial)
            || !botCobarde.getPosicion().equals(posCobardeInicial)
            || !botPatrulla.getPosicion().equals(posPatrullaInicial);

        assertTrue(algunMovimiento, "Al menos un robot debe haberse movido en 100 ticks");
    }
}
