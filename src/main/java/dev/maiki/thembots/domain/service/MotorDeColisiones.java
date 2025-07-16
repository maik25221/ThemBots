package dev.maiki.thembots.domain.service;

import dev.maiki.thembots.domain.model.Obstaculo;
import dev.maiki.thembots.domain.model.Robot;
import dev.maiki.thembots.domain.value.Posicion;
import dev.maiki.thembots.domain.value.Radio;

import java.util.List;

/**
 * Servicio del dominio que detecta colisiones entre robots, obstáculos o proyectiles.
 */
public class MotorDeColisiones {

    /**
     * Verifica si una posición objetivo causaría una colisión con otro robot o con un obstáculo.
     */
    public boolean hayColision(Posicion nuevaPosicion, Radio radio, List<Robot> otrosRobots, List<Obstaculo> obstaculos) {
        for (Robot r : otrosRobots) {
            if (r.estaActivo() && r.getPosicion().distanciaA(nuevaPosicion) <= r.getRadio().valor() + radio.valor()) {
                return true;
            }
        }

        for (Obstaculo o : obstaculos) {
            if (o.getPosicion().distanciaA(nuevaPosicion) <= o.getRadio().valor() + radio.valor()) {
                return true;
            }
        }

        return false;
    }
}
