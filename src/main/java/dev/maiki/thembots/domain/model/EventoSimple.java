package dev.maiki.thembots.domain.model;

import dev.maiki.thembots.domain.model.enums.TipoEvento;

/**
 * Evento genérico sin información adicional, como TICK_INICIADO o TICK_FINALIZADO.
 */
public class EventoSimple implements EventoDeCombate {

    private final int tick;
    private final TipoEvento tipo;

    public EventoSimple(int tick, TipoEvento tipo) {
        this.tick = tick;
        this.tipo = tipo;
    }

    @Override
    public int tick() {
        return tick;
    }

    @Override
    public TipoEvento tipo() {
        return tipo;
    }

    public TipoEvento getTipo() {
        return tipo;
    }
}
