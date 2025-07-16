package dev.maiki.thembots.domain.model.accion;

import dev.maiki.thembots.domain.model.enums.TipoAccion;

/**
 * Acción que indica que el robot desea girar un cierto ángulo.
 * El valor puede ser positivo (horario) o negativo (antihorario).
 */
public class AccionGirar implements Accion {

    private final double angulo;

    public AccionGirar(double angulo) {
        this.angulo = angulo;
    }

    public double getAngulo() {
        return angulo;
    }

    @Override
    public TipoAccion tipo() {
        return TipoAccion.GIRAR;
    }
}
