package dev.maiki.thembots.domain.model.accion;

import dev.maiki.thembots.domain.model.enums.TipoAccion;

/**
 * Acción que indica que el robot desea avanzar en su dirección actual.
 * La distancia representa la intensidad del movimiento.
 */
public class AccionMover implements Accion {

    private final double distancia;

    public AccionMover(double distancia) {
        this.distancia = distancia;
    }

    public double getDistancia() {
        return distancia;
    }

    @Override
    public TipoAccion tipo() {
        return TipoAccion.MOVER;
    }
}
