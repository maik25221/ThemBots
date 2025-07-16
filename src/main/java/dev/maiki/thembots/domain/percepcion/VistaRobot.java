package dev.maiki.thembots.domain.percepcion;


import dev.maiki.thembots.domain.value.Posicion;

import java.util.UUID;

/**
 * Vista de solo lectura del estado de un robot desde el punto de vista de otro.
 * Se utiliza dentro del ContextoPercepcion.
 */
public class VistaRobot {

    private final UUID id;
    private final String nombre;
    private final Posicion posicion;
    private final double direccion;
    private final double vida;
    private final boolean destruido;
    private final double cooldown;
    private final double radio;

    public VistaRobot(UUID id, String nombre, Posicion posicion, double direccion,
                      double vida, boolean destruido, double cooldown, double radio) {
        this.id = id;
        this.nombre = nombre;
        this.posicion = posicion;
        this.direccion = direccion;
        this.vida = vida;
        this.destruido = destruido;
        this.cooldown = cooldown;
        this.radio = radio;
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public double getDireccion() {
        return direccion;
    }

    public double getVida() {
        return vida;
    }

    public boolean isDestruido() {
        return destruido;
    }

    public double getCooldown() {
        return cooldown;
    }

    public double getRadio() {
        return radio;
    }
}
