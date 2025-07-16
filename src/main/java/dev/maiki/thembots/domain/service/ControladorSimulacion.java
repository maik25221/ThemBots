package dev.maiki.thembots.domain.service;


import dev.maiki.thembots.domain.model.Arena;
import dev.maiki.thembots.domain.model.EventoDeCombate;
import dev.maiki.thembots.domain.model.ResultadoCombate;

import java.util.List;
import java.util.Map;

/**
 * Servicio que mantiene el estado de una simulación única en memoria.
 * Puede ejecutar ticks, consultar historial y obtener resultados.
 */
public class ControladorSimulacion {

    private final SimuladorCombate simulador;

    public ControladorSimulacion(Arena arena, int tickMaximo) {
        this.simulador = new SimuladorCombate(arena, tickMaximo);
    }

    public void simularHastaElFinal() {
        simulador.simularHastaElFinal();
    }

    public void simularUnTick() {
        simulador.simularTick();
    }

    public boolean estaTerminada() {
        return simulador.estaTerminado();
    }

    public ResultadoCombate resultadoFinal() {
        return simulador.resultadoFinal();
    }

    public int getTickActual() {
        return simulador.getTickActual();
    }

    public Map<Integer, List<EventoDeCombate>> historialCompleto() {
        return simulador.historialCompleto();
    }

    public List<EventoDeCombate> eventosDelTick(int tick) {
        return simulador.eventosDelTick(tick);
    }
}
