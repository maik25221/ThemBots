package dev.maiki.thembots.domain.model;

import dev.maiki.thembots.domain.model.enums.TipoEvento;
import dev.maiki.thembots.domain.value.Posicion;

import java.util.UUID;

/**
 * Evento detallado que registra impactos entre proyectiles y robots
 */
public class EventoImpacto implements EventoDeCombate {
    
    private final int tick;
    private final UUID proyectilId;
    private final UUID disparadorId;
    private final String nombreDisparador;
    private final UUID objetivoId;
    private final String nombreObjetivo;
    private final Posicion posicionImpacto;
    private final double danoInfligido;
    private final double vidaAnterior;
    private final double vidaPosterior;
    private final boolean objetivoDestruido;
    
    public EventoImpacto(int tick, UUID proyectilId, UUID disparadorId, String nombreDisparador,
                        UUID objetivoId, String nombreObjetivo, Posicion posicionImpacto,
                        double danoInfligido, double vidaAnterior, double vidaPosterior, boolean objetivoDestruido) {
        this.tick = tick;
        this.proyectilId = proyectilId;
        this.disparadorId = disparadorId;
        this.nombreDisparador = nombreDisparador;
        this.objetivoId = objetivoId;
        this.nombreObjetivo = nombreObjetivo;
        this.posicionImpacto = posicionImpacto;
        this.danoInfligido = danoInfligido;
        this.vidaAnterior = vidaAnterior;
        this.vidaPosterior = vidaPosterior;
        this.objetivoDestruido = objetivoDestruido;
    }

    @Override
    public int tick() {
        return tick;
    }

    @Override
    public TipoEvento tipo() {
        return TipoEvento.IMPACTO_DETECTADO;
    }
    
    public UUID getProyectilId() { return proyectilId; }
    public UUID getDisparadorId() { return disparadorId; }
    public String getNombreDisparador() { return nombreDisparador; }
    public UUID getObjetivoId() { return objetivoId; }
    public String getNombreObjetivo() { return nombreObjetivo; }
    public Posicion getPosicionImpacto() { return posicionImpacto; }
    public double getDanoInfligido() { return danoInfligido; }
    public double getVidaAnterior() { return vidaAnterior; }
    public double getVidaPosterior() { return vidaPosterior; }
    public boolean isObjetivoDestruido() { return objetivoDestruido; }
    
    public TipoEvento getTipo() { return tipo(); }
    
    @Override
    public String toString() {
        String destruccion = objetivoDestruido ? " 💀 DESTRUIDO!" : "";
        return String.format("[Tick %d] 🎯 %s impactó a %s en (%.1f,%.1f): %.0f daño (%.0f→%.0f vida)%s",
            tick, nombreDisparador, nombreObjetivo, posicionImpacto.getX(), posicionImpacto.getY(),
            danoInfligido, vidaAnterior, vidaPosterior, destruccion);
    }
}