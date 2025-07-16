package dev.maiki.thembots.domain.model.accion;

import dev.maiki.thembots.domain.model.enums.TipoAccion;

/**
 * Acción que representa la inactividad del robot durante un tick.
 * No se realiza ningún movimiento, giro ni disparo.
 */
public class AccionNada implements Accion {

    @Override
    public TipoAccion tipo() {
        return TipoAccion.NADA;
    }
}
