package dev.maiki.thembots.domain.model.accion;

import dev.maiki.thembots.domain.model.enums.TipoAccion;

/**
 * Interfaz base para todas las acciones que un robot puede ejecutar en un tick.
 * Cada acción concreta tendrá su propia clase que extiende esta interfaz.
 */
public interface Accion {

    /**
     * Tipo general de la acción (MOVER, GIRAR, DISPARAR, NADA).
     */
    TipoAccion tipo();
}
