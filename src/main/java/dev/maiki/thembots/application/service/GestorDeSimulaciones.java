package dev.maiki.thembots.application.service;

import dev.maiki.thembots.domain.service.SimuladorCombate;

import java.util.HashMap;
import java.util.Map;

/**
 * Servicio de aplicación que gestiona múltiples simulaciones activas.
 */
public class GestorDeSimulaciones {

    private final Map<String, SimuladorCombate> simulaciones = new HashMap<>();

    public void registrarSimulador(String id, SimuladorCombate simulador) {
        simulaciones.put(id, simulador);
    }

    public SimuladorCombate obtenerSimulador(String id) {
        if (!simulaciones.containsKey(id)) {
            throw new IllegalArgumentException("Simulación no encontrada: " + id);
        }
        return simulaciones.get(id);
    }

    public void eliminarSimulador(String id) {
        simulaciones.remove(id);
    }
}
