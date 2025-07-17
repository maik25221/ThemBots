package dev.maiki.thembots.domain.model;


import dev.maiki.thembots.domain.value.Posicion;
import dev.maiki.thembots.domain.value.Radio;
import dev.maiki.thembots.domain.value.Vector;

import java.util.UUID;

/**
 * Entidad que representa un proyectil en movimiento lanzado por un robot.
 * Se desplaza en línea recta hasta impactar o salir de la arena.
 */
public class Proyectil {

    private final UUID id;
    private final Vector vector;
    private final double dmg;
    private final UUID origen;
    private final Radio radio;
    private Posicion posicion;

    public Proyectil(UUID id, Posicion posicion, Vector vector, double dmg, UUID origen, Radio radio) {
        this.id = id;
        this.posicion = posicion;
        this.vector = vector;
        this.dmg = dmg;
        this.origen = origen;
        this.radio = radio;
    }

    /**
     * Avanza el proyectil según su vector de movimiento.
     */
    public void avanzar() {
        this.posicion = this.posicion.avanzar(vector);
    }

    public UUID getId() {
        return id;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public Vector getVector() {
        return vector;
    }

    public double getDmg() {
        return dmg;
    }

    public UUID getOrigen() {
        return origen;
    }

    public Radio getRadio() {
        return radio;
    }
}
