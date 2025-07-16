package dev.maiki.thembots.domain.model.accion;

import dev.maiki.thembots.domain.model.enums.TipoAccion;

/**
 * Acción que indica que el robot desea disparar.
 * El disparo solo se ejecutará si el robot no está en cooldown.
 */
public class AccionDisparar implements Accion {

    @Override
    public TipoAccion tipo() {
        return TipoAccion.DISPARAR;
    }
}
