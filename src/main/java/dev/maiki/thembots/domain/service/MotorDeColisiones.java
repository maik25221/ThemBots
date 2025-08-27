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

    /**
     * Verifica si un movimiento desde una posición inicial a una final causaría colisión.
     * Chequea tanto la posición final como posibles intersecciones en el camino.
     */
    public boolean hayColisionEnCamino(Posicion posicionInicial, Posicion nuevaPosicion, Radio radio, 
                                       List<Robot> otrosRobots, List<Obstaculo> obstaculos) {
        // Primero verificar colisión en destino
        if (hayColision(nuevaPosicion, radio, otrosRobots, obstaculos)) {
            return true;
        }
        
        // Verificar colisiones en el camino con obstáculos
        for (Obstaculo o : obstaculos) {
            if (hayColisionLinea(posicionInicial, nuevaPosicion, radio, o.getPosicion(), o.getRadio())) {
                return true;
            }
        }
        
        // Verificar colisiones en el camino con otros robots
        for (Robot r : otrosRobots) {
            if (r.estaActivo() && hayColisionLinea(posicionInicial, nuevaPosicion, radio, r.getPosicion(), r.getRadio())) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Verifica si una línea de movimiento (con radio) intersecta con un círculo.
     */
    private boolean hayColisionLinea(Posicion inicio, Posicion fin, Radio radioMovil, 
                                     Posicion centroObstaculo, Radio radioObstaculo) {
        // Distancia mínima entre la línea de movimiento y el centro del obstáculo
        double distanciaMinima = distanciaPuntoALinea(centroObstaculo, inicio, fin);
        return distanciaMinima <= radioMovil.valor() + radioObstaculo.valor();
    }

    /**
     * Calcula la distancia más corta de un punto a una línea.
     */
    private double distanciaPuntoALinea(Posicion punto, Posicion lineaInicio, Posicion lineaFin) {
        double A = punto.getX() - lineaInicio.getX();
        double B = punto.getY() - lineaInicio.getY();
        double C = lineaFin.getX() - lineaInicio.getX();
        double D = lineaFin.getY() - lineaInicio.getY();

        double dot = A * C + B * D;
        double lenSq = C * C + D * D;
        
        if (lenSq == 0) {
            // La línea es un punto
            return punto.distanciaA(lineaInicio);
        }
        
        double param = dot / lenSq;
        
        double xx, yy;
        if (param < 0) {
            xx = lineaInicio.getX();
            yy = lineaInicio.getY();
        } else if (param > 1) {
            xx = lineaFin.getX();
            yy = lineaFin.getY();
        } else {
            xx = lineaInicio.getX() + param * C;
            yy = lineaInicio.getY() + param * D;
        }
        
        double dx = punto.getX() - xx;
        double dy = punto.getY() - yy;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
