package dev.maiki.thembots.domain.service;


import dev.maiki.thembots.domain.model.EstadisticasRobot;
import dev.maiki.thembots.domain.model.Proyectil;
import dev.maiki.thembots.domain.model.Robot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Servicio que acumula estadísticas de combate para cada robot durante la simulación.
 */
public class AcumuladorEstadisticas {

    private final Map<UUID, EstadisticasRobot> stats = new HashMap<>();

    public void inicializar(List<Robot> robots) {
        for (Robot r : robots) {
            stats.put(r.getId(), new EstadisticasRobot(r.getId(), r.getNombre()));
        }
    }

    public void registrarTick(List<Robot> robots) {
        for (Robot r : robots) {
            if (r.estaActivo()) {
                stats.get(r.getId()).registrarTickVivo();
            }
        }
    }

    public void registrarDisparo(Robot robot) {
        stats.get(robot.getId()).registrarDisparo();
    }

    public void registrarImpacto(Proyectil proyectil, Robot objetivo) {
        stats.get(proyectil.getOrigen()).registrarImpacto(proyectil.getDano());
        stats.get(objetivo.getId()).registrarDanioRecibido(proyectil.getDano());
    }

    public void registrarDestruccion(Robot robot) {
        stats.get(robot.getId()).marcarDestruido();
    }

    public void finalizar(List<Robot> robots) {
        for (Robot r : robots) {
            stats.get(r.getId()).setVidaFinal(r.getVida().valor());
        }
    }

    public List<EstadisticasRobot> obtenerRanking() {
        return stats.values().stream()
            .sorted((a, b) -> Double.compare(b.getVidaFinal(), a.getVidaFinal()))
            .toList();
    }
}
