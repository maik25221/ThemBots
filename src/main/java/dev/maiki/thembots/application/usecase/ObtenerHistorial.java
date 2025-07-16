package dev.maiki.thembots.application.usecase;

import dev.maiki.thembots.application.service.GestorDeSimulaciones;
import dev.maiki.thembots.domain.model.EventoDeCombate;
import dev.maiki.thembots.domain.service.SimuladorCombate;

import java.util.List;
import java.util.Map;

/**
 * Caso de uso que devuelve el historial completo de eventos de una simulación.
 */
public class ObtenerHistorial {

    private final GestorDeSimulaciones gestor;

    public ObtenerHistorial(GestorDeSimulaciones gestor) {
        this.gestor = gestor;
    }

    public Map<Integer, List<EventoDeCombate>> ejecutar(String simulacionId) {
        SimuladorCombate simulador = gestor.obtenerSimulador(simulacionId);
        return simulador.historialCompleto();
    }
}
