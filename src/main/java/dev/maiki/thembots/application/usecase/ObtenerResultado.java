package dev.maiki.thembots.application.usecase;

import dev.maiki.thembots.application.service.GestorDeSimulaciones;
import dev.maiki.thembots.domain.model.ResultadoCombate;
import dev.maiki.thembots.domain.service.SimuladorCombate;

/**
 * Caso de uso que devuelve el resultado final de una simulación terminada.
 */
public class ObtenerResultado {

    private final GestorDeSimulaciones gestor;

    public ObtenerResultado(GestorDeSimulaciones gestor) {
        this.gestor = gestor;
    }

    public ResultadoCombate ejecutar(String simulacionId) {
        SimuladorCombate simulador = gestor.obtenerSimulador(simulacionId);
        if (!simulador.estaTerminado()) {
            throw new IllegalStateException("La simulación aún no ha finalizado.");
        }
        return simulador.resultadoFinal();
    }
}
