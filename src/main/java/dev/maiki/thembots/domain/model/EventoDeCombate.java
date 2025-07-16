package dev.maiki.thembots.domain.model;

import dev.maiki.thembots.domain.model.enums.TipoEvento;

/**
 * Interfaz base para todos los eventos que ocurren durante la simulación.
 * Cada implementación representa un tipo concreto de evento (movimiento, disparo, impacto, etc.).
 */
public interface EventoDeCombate {

    /**
     * Tick en el que ocurre este evento.
     */
    int tick();

    /**
     * Tipo general del evento.
     */
    TipoEvento tipo();
}
