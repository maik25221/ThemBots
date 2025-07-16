package dev.maiki.thembots.domain.model;

import dev.maiki.thembots.domain.value.Posicion;
import dev.maiki.thembots.domain.value.Radio;

/**
 * Entidad simple e inmutable que representa un obstáculo fijo en la arena.
 * Se usa para colisiones y bloqueo de proyectiles o robots.
 */
public class Obstaculo {

    private final Posicion posicion;
    private final Radio radio;

    public Obstaculo(Posicion posicion, Radio radio) {
        this.posicion = posicion;
        this.radio = radio;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public Radio getRadio() {
        return radio;
    }
}
