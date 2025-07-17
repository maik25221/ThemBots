package dev.maiki.thembots.domain.service;

import dev.maiki.thembots.domain.model.Obstaculo;
import dev.maiki.thembots.domain.model.Proyectil;
import dev.maiki.thembots.domain.model.Robot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Servicio del dominio que gestiona el avance y efecto de los proyectiles.
 */
public class MotorDeProyectiles {

    /**
     * Procesa el avance y colisiones de los proyectiles.
     * Elimina proyectiles que salen de la arena, impactan obstáculos o robots (activos o destruidos).
     * Llama a los callbacks de eliminación e impacto según corresponda.
     */
    public void procesarProyectiles(
        List<Proyectil> proyectiles,
        List<Robot> robots,
        double anchoArena,
        double altoArena,
        Consumer<Proyectil> alEliminar,
        Consumer<ImpactoProyectil> alImpactar,
        List<Obstaculo> obstaculos
    ) {
        // Copia para evitar ConcurrentModificationException
        List<Proyectil> pendientes = new ArrayList<>(proyectiles);

        for (Proyectil p : pendientes) {
            // Avanzar proyectil
            p.avanzar();

            // 1. Eliminar si sale de la arena
            if (!p.getPosicion().dentroDeLimites(anchoArena, altoArena)) {
                alEliminar.accept(p);
                continue;
            }

            // 2. Eliminar si impacta obstáculo
            boolean impactoObstaculo = false;
            for (Obstaculo o : obstaculos) {
                double distancia = o.getPosicion().distanciaA(p.getPosicion());
                double umbral = o.getRadio().valor() + p.getRadio().valor();
                if (distancia <= umbral) {
                    alEliminar.accept(p);
                    impactoObstaculo = true;
                    break;
                }
            }
            if (impactoObstaculo) continue;

            // 3. Eliminar si impacta cualquier robot (activo o destruido)
            for (Robot r : robots) {
                if (r.getId().equals(p.getOrigen())) continue; // No impacta al que lo disparó
                double distancia = r.getPosicion().distanciaA(p.getPosicion());
                double umbral = r.getRadio().valor() + p.getRadio().valor();
                if (distancia <= umbral) {
                    alImpactar.accept(new ImpactoProyectil(p, r));
                    alEliminar.accept(p);
                    break;
                }
            }
        }
    }

    /**
     * Representa un impacto de proyectil contra un robot.
     */
    public record ImpactoProyectil(Proyectil proyectil, Robot objetivo) {

    }
}
