package dev.maiki.thembots.application.model;


import dev.maiki.thembots.domain.model.Obstaculo;
import dev.maiki.thembots.domain.model.Robot;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Representa la configuración externa para crear una simulación de combate.
 */
public class ConfiguracionCombate {

    private double anchoArena;
    private double altoArena;
    private int tickMaximo;
    private List<InstanciaRobot> robots;
    private List<Obstaculo> obstaculos;

    public double anchoArena() {
        return anchoArena;
    }

    public double altoArena() {
        return altoArena;
    }

    public int tickMaximo() {
        return tickMaximo;
    }

    public List<Obstaculo> obstaculos() {
        return obstaculos;
    }

    public List<Robot> crearRobots() {
        return robots.stream()
            .map(InstanciaRobot::aEntidad)
            .collect(Collectors.toList());
    }

    // Setters si se necesita deserializar desde JSON, etc.
}
