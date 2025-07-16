package dev.maiki.thembots.domain.model;

import dev.maiki.thembots.domain.value.Posicion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Entidad que representa la arena donde se desarrolla el combate.
 * Contiene a los robots, proyectiles y obstáculos.
 */
public class Arena {

    private final double ancho;
    private final double alto;

    private final List<Robot> robots;
    private final List<Proyectil> proyectiles;
    private final List<Obstaculo> obstaculos;

    public Arena(double ancho, double alto, List<Robot> robots, List<Obstaculo> obstaculos) {
        this.ancho = ancho;
        this.alto = alto;
        this.robots = new ArrayList<>(robots);
        this.proyectiles = new ArrayList<>();
        this.obstaculos = new ArrayList<>(obstaculos);
    }

    public double getAncho() {
        return ancho;
    }

    public double getAlto() {
        return alto;
    }

    public List<Robot> getRobots() {
        return Collections.unmodifiableList(robots);
    }

    public List<Proyectil> getProyectiles() {
        return Collections.unmodifiableList(proyectiles);
    }

    public List<Obstaculo> getObstaculos() {
        return Collections.unmodifiableList(obstaculos);
    }

    /**
     * Añade un nuevo proyectil a la arena.
     */
    public void agregarProyectil(Proyectil proyectil) {
        proyectiles.add(proyectil);
    }

    /**
     * Elimina un proyectil de la arena (por impacto o salir de límites).
     */
    public void eliminarProyectil(Proyectil proyectil) {
        proyectiles.remove(proyectil);
    }

    /**
     * Devuelve true si una posición está dentro de los límites de la arena.
     */
    public boolean dentroDeLimites(Posicion posicion) {
        return posicion.dentroDeLimites(ancho, alto);
    }
}

