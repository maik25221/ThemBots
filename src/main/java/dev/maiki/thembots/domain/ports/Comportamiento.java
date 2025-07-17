package dev.maiki.thembots.domain.ports;

import dev.maiki.thembots.domain.model.accion.Accion;
import dev.maiki.thembots.domain.percepcion.ContextoPercepcion;

/**
 * Interfaz que define el comportamiento programático de un robot.
 * Cada implementación debe decidir qué acción tomar en base al contexto del combate.
 */
public interface Comportamiento {

    /**
     * Méthod invocado en cada tick de la simulación para decidir la acción del robot.
     *
     * @param contexto contexto completo del estado de la arena y del propio robot
     * @return acción a realizar este tick
     */
    Accion decidir(ContextoPercepcion contexto);
}
