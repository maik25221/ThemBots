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

    // Getters y setters necesarios para deserialización si procede
}
