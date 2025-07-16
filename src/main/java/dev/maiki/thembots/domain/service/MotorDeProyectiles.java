package dev.maiki.thembots.domain.service;

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
     * Avanza todos los proyectiles activos una vez y detecta impactos.
     * Ejecuta un callback por cada impacto detectado.
     */
    public void procesarProyectiles(List<Proyectil> proyectiles, List<Robot> robots, double ancho, double alto,
                                    Consumer<Proyectil> alSalir,
                                    Consumer<Impacto> alImpactar) {

        List<Proyectil> pendientes = new ArrayList<>(proyectiles);

        for (Proyectil p : pendientes) {
            p.avanzar();

            if (!p.getPosicion().dentroDeLimites(ancho, alto)) {
                alSalir.accept(p);
                continue;
            }

            for (Robot r : robots) {
                if (r.getId().equals(p.getOrigen()) || !r.estaActivo()) continue;

                double distancia = r.getPosicion().distanciaA(p.getPosicion());
                double umbral = r.getRadio().valor() + p.getRadio().valor();

                if (distancia <= umbral) {
                    alImpactar.accept(new Impacto(p, r));
                    break;
                }
            }
        }
    }

    /**
     * Registro del impacto entre un proyectil y un robot.
     */
    public record Impacto(Proyectil proyectil, Robot objetivo) {

    }
}
