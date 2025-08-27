package dev.maiki.thembots.domain.model;

import dev.maiki.thembots.domain.model.accion.Accion;
import dev.maiki.thembots.domain.model.enums.TipoEvento;
import dev.maiki.thembots.domain.value.Posicion;

import java.util.UUID;

/**
 * Evento que registra las decisiones específicas tomadas por cada robot
 */
public class EventoDecision implements EventoDeCombate {
    
    private final int tick;
    private final UUID robotId;
    private final String nombreRobot;
    private final String tipoComportamiento;
    private final Posicion posicionAntes;
    private final double direccionAntes;
    private final Accion accionDecidida;
    private final String razonamiento;
    
    public EventoDecision(int tick, UUID robotId, String nombreRobot, String tipoComportamiento,
                         Posicion posicionAntes, double direccionAntes, Accion accionDecidida, String razonamiento) {
        this.tick = tick;
        this.robotId = robotId;
        this.nombreRobot = nombreRobot;
        this.tipoComportamiento = tipoComportamiento;
        this.posicionAntes = posicionAntes;
        this.direccionAntes = direccionAntes;
        this.accionDecidida = accionDecidida;
        this.razonamiento = razonamiento;
    }

    @Override
    public int tick() {
        return tick;
    }

    @Override
    public TipoEvento tipo() {
        return TipoEvento.DECISION_TOMADA;
    }
    
    public UUID getRobotId() { return robotId; }
    public String getNombreRobot() { return nombreRobot; }
    public String getTipoComportamiento() { return tipoComportamiento; }
    public Posicion getPosicionAntes() { return posicionAntes; }
    public double getDireccionAntes() { return direccionAntes; }
    public Accion getAccionDecidida() { return accionDecidida; }
    public String getRazonamiento() { return razonamiento; }
    
    public TipoEvento getTipo() { return tipo(); }
    
    @Override
    public String toString() {
        return String.format("[Tick %d] %s (%s) en (%.1f,%.1f) decidió: %s - Razón: %s",
            tick, nombreRobot, tipoComportamiento, posicionAntes.getX(), posicionAntes.getY(),
            accionDecidida.getClass().getSimpleName(), razonamiento);
    }
}