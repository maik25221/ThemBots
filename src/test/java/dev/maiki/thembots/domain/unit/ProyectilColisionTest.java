package dev.maiki.thembots.domain.unit;

import dev.maiki.thembots.domain.model.Arena;
import dev.maiki.thembots.domain.model.Obstaculo;
import dev.maiki.thembots.domain.model.Robot;
import dev.maiki.thembots.domain.model.accion.AccionDisparar;
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

public class ProyectilColisionTest {

    @Test
    public void proyectilSeMueveEnLineaRecta() {
        // Robot dispara, no hay obstáculos ni otros robots
        Robot robot = new Robot(UUID.randomUUID(), "Shooter", new Posicion(5, 5), 0,
                                new Vida(100), 0, new Radio(1), contexto -> new AccionDisparar());
        Arena arena = new Arena(20, 20, List.of(robot), List.of());
        SimuladorCombate simulador = new SimuladorCombate(arena, 3);

        simulador.simularTick(); // Dispara
        assertEquals(1, arena.getProyectiles().size());
        Posicion posInicial = arena.getProyectiles().get(0).getPosicion();

        simulador.simularTick(); // Proyectil avanza
        assertEquals(1, arena.getProyectiles().size());
        Posicion posFinal = arena.getProyectiles().get(0).getPosicion();

        assertNotEquals(posInicial, posFinal, "El proyectil debe haberse movido");
        assertTrue(posFinal.getX() > posInicial.getX(), "El proyectil debe avanzar en X positiva");
    }

    @Test
    public void proyectilDesapareceAlSalirDeArena() {
        // Dispara hacia fuera de la arena
        Robot robot = new Robot(UUID.randomUUID(), "Shooter", new Posicion(19, 10), 0,
                                new Vida(100), 0, new Radio(1), contexto -> new AccionDisparar());
        Arena arena = new Arena(20, 20, List.of(robot), List.of());
        SimuladorCombate simulador = new SimuladorCombate(arena, 5);

        simulador.simularTick(); // Dispara
        assertEquals(1, arena.getProyectiles().size());

        // El proyectil debería salir de la arena en el siguiente tick
        simulador.simularTick();
        assertEquals(0, arena.getProyectiles().size(), "El proyectil debe eliminarse al salir de la arena");
    }

    @Test
    public void proyectilImpactaYDestruyeRobotVivo() {
        UUID idShooter = UUID.randomUUID();
        UUID idVictima = UUID.randomUUID();

        Comportamiento dispara = contexto -> new AccionDisparar();
        Comportamiento quieto = contexto -> new AccionNada();

        Robot shooter = new Robot(idShooter, "Shooter", new Posicion(10, 10), 0,
                                  new Vida(100), 0, new Radio(1), dispara);
        Robot victima = new Robot(idVictima, "Victima", new Posicion(12, 10), 0,
                                  new Vida(10), 0, new Radio(1), quieto);

        Arena arena = new Arena(20, 20, List.of(shooter, victima), List.of());
        SimuladorCombate simulador = new SimuladorCombate(arena, 5);

        simulador.simularTick(); // Dispara
        simulador.simularTick(); // Proyectil impacta

        assertEquals(EstadoRobot.DESTRUIDO, victima.getEstado(), "El robot debe estar destruido tras el impacto");
        assertEquals(0, arena.getProyectiles().size(), "El proyectil debe eliminarse tras el impacto");
    }

    @Test
    public void proyectilNoAfectaRobotDestruido() {
        UUID idShooter = UUID.randomUUID();
        UUID idVictima = UUID.randomUUID();

        Comportamiento dispara = contexto -> new AccionDisparar();
        Comportamiento quieto = contexto -> new AccionNada();

        Robot shooter = new Robot(idShooter, "Shooter", new Posicion(10, 10), 0,
                                  new Vida(100), 0, new Radio(1), dispara);
        Robot victima = new Robot(idVictima, "Victima", new Posicion(12, 10), 0,
                                  new Vida(0), 0, new Radio(1), quieto); // Ya destruido

        // Forzar estado destruido
        victima.recibirDanio(100);

        Arena arena = new Arena(20, 20, List.of(shooter, victima), List.of());
        SimuladorCombate simulador = new SimuladorCombate(arena, 5);

        simulador.simularTick(); // Dispara
        simulador.simularTick(); // Proyectil debería pasar de largo

        assertEquals(EstadoRobot.DESTRUIDO, victima.getEstado());
        assertTrue(arena.getProyectiles().isEmpty(), "El proyectil debe eliminarse aunque no haga daño");
    }

    @Test
    public void proyectilSeDetieneAnteObstaculo() {
        // Dispara hacia un obstáculo
        Robot robot = new Robot(UUID.randomUUID(), "Shooter", new Posicion(10, 10), 0,
                                new Vida(100), 0, new Radio(1), contexto -> new AccionDisparar());
        Obstaculo obstaculo = new Obstaculo(new Posicion(12, 10), new Radio(1.5));
        Arena arena = new Arena(20, 20, List.of(robot), List.of(obstaculo));
        SimuladorCombate simulador = new SimuladorCombate(arena, 5);

        simulador.simularTick(); // Dispara
        simulador.simularTick(); // Proyectil debería colisionar con obstáculo

        assertEquals(0, arena.getProyectiles().size(), "El proyectil debe eliminarse al chocar con obstáculo");
    }
}
