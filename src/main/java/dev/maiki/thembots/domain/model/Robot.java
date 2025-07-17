package dev.maiki.thembots.domain.model;

import dev.maiki.thembots.domain.model.enums.EstadoRobot;
import dev.maiki.thembots.domain.ports.Comportamiento;
import dev.maiki.thembots.domain.value.Posicion;
import dev.maiki.thembots.domain.value.Radio;
import dev.maiki.thembots.domain.value.Vector;
import dev.maiki.thembots.domain.value.Vida;

import java.util.UUID;

/**
 * Entidad principal que representa un robot dentro de la arena.
 * Tiene identidad propia, estado mutable y un comportamiento programático
 * que se ejecuta en cada tick de la simulación.
 */
public class Robot {

    private final UUID id;
    private final String nombre;
    private final Radio radio;
    private final Comportamiento comportamiento;
    private Posicion posicion;
    private double direccion; // Ángulo en radianes
    private Vida vida;
    private double cooldown; // Ticks restantes para poder disparar
    private EstadoRobot estado;

    public Robot(UUID id, String nombre, Posicion posicion, double direccion,
                 Vida vida, double cooldown, Radio radio, Comportamiento comportamiento) {
        this.id = id;
        this.nombre = nombre;
        this.posicion = posicion;
        this.direccion = direccion;
        this.vida = vida;
        this.cooldown = cooldown;
        this.radio = radio;
        this.estado = EstadoRobot.ACTIVO;
        this.comportamiento = comportamiento;
    }

    /**
     * Aplica daño al robot. Si la vida llega a 0 o menos, cambia a estado DESTRUIDO.
     */
    public void recibirDanio(double cantidad) {
        this.vida = this.vida.restar(cantidad);
        if (!vida.estaVivo()) {
            this.estado = EstadoRobot.DESTRUIDO;
        }
    }

    /**
     * Devuelve un vector unitario en la dirección actual del robot.
     */
    public Vector getDireccionVector() {
        return new Vector(Math.cos(direccion), Math.sin(direccion));
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

    public void setPosicion(Posicion posicion) {
        this.posicion = posicion;
    }

    public double getDireccion() {
        return direccion;
    }

    public void setDireccion(double direccion) {
        this.direccion = ((direccion % (2 * Math.PI)) + (2 * Math.PI)) % (2 * Math.PI);
    }

    public Vida getVida() {
        return vida;
    }

    public double getCooldown() {
        return cooldown;
    }

    public void setCooldown(double cooldown) {
        this.cooldown = cooldown;
    }

    public Radio getRadio() {
        return radio;
    }

    public EstadoRobot getEstado() {
        return estado;
    }

    public Comportamiento getComportamiento() {
        return comportamiento;
    }

    public boolean estaActivo() {
        return estado == EstadoRobot.ACTIVO;
    }
}
