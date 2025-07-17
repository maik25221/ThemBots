package dev.maiki.thembots.domain.ia;

import dev.maiki.thembots.domain.model.accion.*;
import dev.maiki.thembots.domain.percepcion.ContextoPercepcion;
import dev.maiki.thembots.domain.percepcion.VistaRobot;

import java.util.Comparator;

public class ComportamientoCazador implements dev.maiki.thembots.domain.ports.Comportamiento {

    @Override
    public Accion decidir(ContextoPercepcion contexto) {
        VistaRobot yo = contexto.miEstado();

        // Buscar el enemigo más cercano
        VistaRobot objetivo = contexto.todosLosRobots().stream()
            .filter(r -> !r.getId().equals(yo.getId()) && !r.isDestruido())
            .min(Comparator.comparingDouble(r -> r.getPosicion().distanciaA(yo.getPosicion())))
            .orElse(null);

        if (objetivo == null) return new AccionNada();

        double distancia = yo.getPosicion().distanciaA(objetivo.getPosicion());
        double dx = objetivo.getPosicion().getX() - yo.getPosicion().getX();
        double dy = objetivo.getPosicion().getY() - yo.getPosicion().getY();
        double anguloObjetivo = Math.atan2(dy, dx);
        double diferenciaAngulo = anguloObjetivo - yo.getDireccion();

        // Normalizar ángulo a [-PI, PI]
        while (diferenciaAngulo > Math.PI) diferenciaAngulo -= 2 * Math.PI;
        while (diferenciaAngulo < -Math.PI) diferenciaAngulo += 2 * Math.PI;

        // Si no está mirando al objetivo, girar
        if (Math.abs(diferenciaAngulo) > 0.2) {
            return new AccionGirar(Math.signum(diferenciaAngulo) * 0.3);
        }

        // Si está cerca, disparar; si no, avanzar
        if (distancia < 6 && yo.getCooldown() == 0) {
            return new AccionDisparar();
        } else if (distancia > 3) {
            return new AccionMover(Math.min(2, distancia - 2));
        } else {
            // Si está muy cerca, alejarse un poco
            return new AccionMover(-1);
        }
    }
}
