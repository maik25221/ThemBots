package dev.maiki.thembots.application.usecase;

import dev.maiki.thembots.application.service.GestorDeSimulaciones;
import dev.maiki.thembots.domain.model.EventoDeCombate;
import dev.maiki.thembots.domain.service.SimuladorCombate;

import java.util.List;

/**
 * Caso de uso que ejecuta un único tick de una simulación activa.
 */
public class SimularUnTick {

    private final GestorDeSimulaciones gestor;

    public SimularUnTick(GestorDeSimulaciones gestor) {
        this.gestor = gestor;
    }

    public List<EventoDeCombate> ejecutar(String simulacionId) {
        SimuladorCombate simulador = gestor.obtenerSimulador(simulacionId);
        if (simulador.estaTerminado()) {
            throw new IllegalStateException("La simulación ya está finalizada.");
        }
        simulador.simularTick();
        return simulador.eventosDelTick(simulador.getTickActual() - 1);
    }
}
