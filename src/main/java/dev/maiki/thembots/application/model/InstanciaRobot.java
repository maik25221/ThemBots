package dev.maiki.thembots.application.model;


import dev.maiki.thembots.domain.model.Robot;
import dev.maiki.thembots.domain.ports.Comportamiento;
import dev.maiki.thembots.domain.value.Posicion;
import dev.maiki.thembots.domain.value.Radio;
import dev.maiki.thembots.domain.value.Vida;

import java.util.UUID;

/**
 * Representa una definición programática de robot antes de ser convertido en entidad de dominio.
 */
public class InstanciaRobot {

    private UUID id;
    private String nombre;
    private Posicion posicionInicial;
    private double direccionInicial;
    private double vidaInicial;
    private double radio;
    private double cooldown;
    private Comportamiento comportamiento;

    public Robot aEntidad() {
        return new Robot(
            id,
            nombre,
            posicionInicial,
            direccionInicial,
            new Vida(vidaInicial),
            cooldown,
            new Radio(radio),
            comportamiento
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Posicion getPosicionInicial() {
        return posicionInicial;
    }

    public void setPosicionInicial(Posicion posicionInicial) {
        this.posicionInicial = posicionInicial;
    }

    public double getDireccionInicial() {
        return direccionInicial;
    }

    public void setDireccionInicial(double direccionInicial) {
        this.direccionInicial = direccionInicial;
    }

    public double getVidaInicial() {
        return vidaInicial;
    }

    public void setVidaInicial(double vidaInicial) {
        this.vidaInicial = vidaInicial;
    }

    public double getRadio() {
        return radio;
    }

    public void setRadio(double radio) {
        this.radio = radio;
    }

    public double getCooldown() {
        return cooldown;
    }

    public void setCooldown(double cooldown) {
        this.cooldown = cooldown;
    }

    public Comportamiento getComportamiento() {
        return comportamiento;
    }

    public void setComportamiento(Comportamiento comportamiento) {
        this.comportamiento = comportamiento;
    }
}
