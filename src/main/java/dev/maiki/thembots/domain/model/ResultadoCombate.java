package dev.maiki.thembots.domain.model;


import java.util.List;
import java.util.UUID;

/**
 * Resultado final de una simulación de combate.
 * Contiene el ganador, estadísticas y si hubo empate.
 */
public class ResultadoCombate {

    private final int totalTicks;
    private final UUID ganadorId;
    private final boolean empate;
    private final List<EstadisticasRobot> ranking;

    public ResultadoCombate(int totalTicks, UUID ganadorId, boolean empate, List<EstadisticasRobot> ranking) {
        this.totalTicks = totalTicks;
        this.ganadorId = ganadorId;
        this.empate = empate;
        this.ranking = ranking;
    }

    public int getTotalTicks() {
        return totalTicks;
    }

    public UUID getGanadorId() {
        return ganadorId;
    }

    public boolean isEmpate() {
        return empate;
    }

    public List<EstadisticasRobot> getRanking() {
        return ranking;
    }
}
