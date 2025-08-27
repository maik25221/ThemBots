package dev.maiki.thembots.domain.model;

import dev.maiki.thembots.domain.model.accion.Accion;
import dev.maiki.thembots.domain.model.enums.TipoEvento;
import dev.maiki.thembots.domain.value.Posicion;

import java.util.UUID;

/**
 * Evento que registra la ejecución de acciones y sus resultados
 */
public class EventoAccion implements EventoDeCombate {
    
    private final int tick;
    private final UUID robotId;
    private final String nombreRobot;
    private final Accion accion;
    private final Posicion posicionAntes;
    private final Posicion posicionDespues;
    private final double direccionAntes;
    private final double direccionDespues;
    private final boolean exitoso;
    private final String detalle;
    
    public EventoAccion(int tick, UUID robotId, String nombreRobot, Accion accion,
                       Posicion posicionAntes, Posicion posicionDespues,
                       double direccionAntes, double direccionDespues,
                       boolean exitoso, String detalle) {
        this.tick = tick;
        this.robotId = robotId;
        this.nombreRobot = nombreRobot;
        this.accion = accion;
        this.posicionAntes = posicionAntes;
        this.posicionDespues = posicionDespues;
        this.direccionAntes = direccionAntes;
        this.direccionDespues = direccionDespues;
        this.exitoso = exitoso;
        this.detalle = detalle;
    }

    @Override
    public int tick() {
        return tick;
    }

    @Override
    public TipoEvento tipo() {
        return TipoEvento.ACCION_EJECUTADA;
    }
    
    public UUID getRobotId() { return robotId; }
    public String getNombreRobot() { return nombreRobot; }
    public Accion getAccion() { return accion; }
    public Posicion getPosicionAntes() { return posicionAntes; }
    public Posicion getPosicionDespues() { return posicionDespues; }
    public double getDireccionAntes() { return direccionAntes; }
    public double getDireccionDespues() { return direccionDespues; }
    public boolean isExitoso() { return exitoso; }
    public String getDetalle() { return detalle; }
    
    public TipoEvento getTipo() { return tipo(); }
    
    @Override
    public String toString() {
        String resultado = exitoso ? "✓" : "✗";
        return String.format("[Tick %d] %s %s %s: (%.1f,%.1f) → (%.1f,%.1f) - %s",
            tick, resultado, nombreRobot, accion.getClass().getSimpleName(),
            posicionAntes.getX(), posicionAntes.getY(),
            posicionDespues.getX(), posicionDespues.getY(),
            detalle);
    }
}