package dev.maiki.thembots.domain.ia;

import dev.maiki.thembots.domain.model.accion.*;
import dev.maiki.thembots.domain.percepcion.ContextoPercepcion;
import dev.maiki.thembots.domain.percepcion.VistaRobot;

import java.util.Comparator;
import java.util.Random;

public class ComportamientoCobarde implements dev.maiki.thembots.domain.ports.Comportamiento {

    private final Random random = new Random();

    @Override
    public Accion decidir(ContextoPercepcion contexto) {
        VistaRobot yo = contexto.miEstado();

        // Buscar el enemigo más cercano
        VistaRobot enemigo = contexto.todosLosRobots().stream()
            .filter(r -> !r.getId().equals(yo.getId()) && !r.isDestruido())
            .min(Comparator.comparingDouble(r -> r.getPosicion().distanciaA(yo.getPosicion())))
            .orElse(null);

        if (enemigo == null) return new AccionNada();

        double distancia = yo.getPosicion().distanciaA(enemigo.getPosicion());
        double dx = enemigo.getPosicion().getX() - yo.getPosicion().getX();
        double dy = enemigo.getPosicion().getY() - yo.getPosicion().getY();
        double anguloEnemigo = Math.atan2(dy, dx);
        double diferenciaAngulo = anguloEnemigo - yo.getDireccion();

        // Normalizar ángulo a [-PI, PI]
        while (diferenciaAngulo > Math.PI) diferenciaAngulo -= 2 * Math.PI;
        while (diferenciaAngulo < -Math.PI) diferenciaAngulo += 2 * Math.PI;

        // Si el enemigo está muy cerca, alejarse
        if (distancia < 6) {
            // Girar en dirección opuesta al enemigo
            double anguloHuir = anguloEnemigo + Math.PI;
            double diferenciaHuir = anguloHuir - yo.getDireccion();
            while (diferenciaHuir > Math.PI) diferenciaHuir -= 2 * Math.PI;
            while (diferenciaHuir < -Math.PI) diferenciaHuir += 2 * Math.PI;

            if (Math.abs(diferenciaHuir) > 0.2) {
                return new AccionGirar(Math.signum(diferenciaHuir) * 0.4);
            }
            // Alejarse
            return new AccionMover(2);
        }

        // Si el enemigo está alineado y cerca, disparar
        if (distancia < 8 && Math.abs(diferenciaAngulo) < 0.2 && yo.getCooldown() == 0) {
            return new AccionDisparar();
        }

        // Si no hay peligro, moverse aleatoriamente o girar
        if (random.nextDouble() < 0.3) {
            return new AccionGirar((random.nextDouble() - 0.5) * 0.6);
        } else {
            return new AccionMover(1 + random.nextDouble());
        }
    }
}
