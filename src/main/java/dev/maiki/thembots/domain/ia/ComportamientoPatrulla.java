package dev.maiki.thembots.domain.ia;

import dev.maiki.thembots.domain.model.accion.Accion;
import dev.maiki.thembots.domain.model.accion.AccionDisparar;
import dev.maiki.thembots.domain.model.accion.AccionGirar;
import dev.maiki.thembots.domain.model.accion.AccionMover;
import dev.maiki.thembots.domain.percepcion.ContextoPercepcion;
import dev.maiki.thembots.domain.percepcion.VistaRobot;
import dev.maiki.thembots.domain.value.Posicion;

import java.util.Comparator;

public class ComportamientoPatrulla implements dev.maiki.thembots.domain.ports.Comportamiento {

    private final Posicion puntoA;
    private final Posicion puntoB;
    private boolean haciaA = true;

    public ComportamientoPatrulla(Posicion puntoA, Posicion puntoB) {
        this.puntoA = puntoA;
        this.puntoB = puntoB;
    }

    @Override
    public Accion decidir(ContextoPercepcion contexto) {
        VistaRobot yo = contexto.miEstado();
        Posicion destino = haciaA ? puntoA : puntoB;

        // Cambiar de punto si está cerca
        if (yo.getPosicion().distanciaA(destino) < 1.0) {
            haciaA = !haciaA;
            destino = haciaA ? puntoA : puntoB;
        }

        // Buscar enemigo más cercano
        VistaRobot enemigo = contexto.todosLosRobots().stream()
            .filter(r -> !r.getId().equals(yo.getId()) && !r.isDestruido())
            .min(Comparator.comparingDouble(r -> r.getPosicion().distanciaA(yo.getPosicion())))
            .orElse(null);

        if (enemigo != null) {
            double dx = enemigo.getPosicion().getX() - yo.getPosicion().getX();
            double dy = enemigo.getPosicion().getY() - yo.getPosicion().getY();
            double anguloEnemigo = Math.atan2(dy, dx);
            double diferenciaAngulo = anguloEnemigo - yo.getDireccion();
            while (diferenciaAngulo > Math.PI) diferenciaAngulo -= 2 * Math.PI;
            while (diferenciaAngulo < -Math.PI) diferenciaAngulo += 2 * Math.PI;

            double distancia = yo.getPosicion().distanciaA(enemigo.getPosicion());
            // Disparar si está alineado y cerca
            if (Math.abs(diferenciaAngulo) < 0.2 && distancia < 8 && yo.getCooldown() == 0) {
                return new AccionDisparar();
            }
        }

        // Patrullar: girar hacia el destino si no está alineado
        double dx = destino.getX() - yo.getPosicion().getX();
        double dy = destino.getY() - yo.getPosicion().getY();
        double anguloDestino = Math.atan2(dy, dx);
        double diferenciaAngulo = anguloDestino - yo.getDireccion();
        while (diferenciaAngulo > Math.PI) diferenciaAngulo -= 2 * Math.PI;
        while (diferenciaAngulo < -Math.PI) diferenciaAngulo += 2 * Math.PI;

        if (Math.abs(diferenciaAngulo) > 0.2) {
            return new AccionGirar(Math.signum(diferenciaAngulo) * 0.3);
        } else {
            return new AccionMover(Math.min(2, yo.getPosicion().distanciaA(destino)));
        }
    }
}
