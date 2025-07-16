package dev.maiki.thembots.domain.service;

import dev.maiki.thembots.domain.model.Arena;
import dev.maiki.thembots.domain.model.Obstaculo;
import dev.maiki.thembots.domain.model.Proyectil;
import dev.maiki.thembots.domain.model.Robot;
import dev.maiki.thembots.domain.percepcion.ContextoPercepcion;
import dev.maiki.thembots.domain.percepcion.VistaRobot;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio que genera el contexto de percepción completo para un robot específico.
 */
public class FabricaContexto {

    public ContextoPercepcion generar(UUID idRobot, int tick, Arena arena) {
        Robot robot = arena.getRobots().stream()
            .filter(r -> r.getId().equals(idRobot))
            .findFirst()
            .orElseThrow();

        VistaRobot miEstado = new VistaRobot(
            robot.getId(),
            robot.getNombre(),
            robot.getPosicion(),
            robot.getDireccion(),
            robot.getVida().valor(),
            robot.getEstado().name().equals("DESTRUIDO"),
            robot.getCooldown(),
            robot.getRadio().valor()
        );

        List<VistaRobot> todos = arena.getRobots().stream()
            .map(r -> new VistaRobot(
                r.getId(),
                r.getNombre(),
                r.getPosicion(),
                r.getDireccion(),
                r.getVida().valor(),
                r.getEstado().name().equals("DESTRUIDO"),
                r.getCooldown(),
                r.getRadio().valor()))
            .collect(Collectors.toList());

        return new ContextoPercepcion() {
            @Override
            public UUID idPropio() {
                return idRobot;
            }

            @Override
            public VistaRobot miEstado() {
                return miEstado;
            }

            @Override
            public List<VistaRobot> todosLosRobots() {
                return todos;
            }

            @Override
            public List<Proyectil> proyectiles() {
                return arena.getProyectiles();
            }

            @Override
            public List<Obstaculo> obstaculos() {
                return arena.getObstaculos();
            }

            @Override
            public double anchoArena() {
                return arena.getAncho();
            }

            @Override
            public double altoArena() {
                return arena.getAlto();
            }

            @Override
            public int tickActual() {
                return tick;
            }
        };
    }
}
