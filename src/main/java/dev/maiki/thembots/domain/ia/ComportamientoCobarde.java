package dev.maiki.thembots.domain.ia;

import dev.maiki.thembots.domain.model.accion.*;
import dev.maiki.thembots.domain.percepcion.ContextoPercepcion;
import dev.maiki.thembots.domain.percepcion.VistaRobot;

import java.util.Comparator;
import java.util.Random;

public class ComportamientoCobarde implements dev.maiki.thembots.domain.ports.Comportamiento {

    private final Random random = new Random(42); // Usar semilla fija para determinismo
    private int contadorEvasion = 0;

    @Override
    public Accion decidir(ContextoPercepcion contexto) {
        VistaRobot yo = contexto.miEstado();

        // Buscar la MAYOR amenaza (enemigo más cercano con más vida)
        VistaRobot amenaza = contexto.todosLosRobots().stream()
            .filter(r -> !r.getId().equals(yo.getId()) && !r.isDestruido())
            .min(Comparator.comparingDouble((VistaRobot r) -> r.getPosicion().distanciaA(yo.getPosicion()))
                          .thenComparingDouble(r -> -r.getVida())) // Priorizar enemigos más fuertes
            .orElse(null);

        if (amenaza == null) {
            // Sin enemigos, explorar aleatoriamente más rápido
            if (random.nextDouble() < 0.4) {
                return new AccionGirar((random.nextDouble() - 0.5) * 0.8);
            } else {
                return new AccionMover(2 + random.nextDouble() * 2);
            }
        }

        double distancia = yo.getPosicion().distanciaA(amenaza.getPosicion());
        double dx = amenaza.getPosicion().getX() - yo.getPosicion().getX();
        double dy = amenaza.getPosicion().getY() - yo.getPosicion().getY();
        double anguloEnemigo = Math.atan2(dy, dx);
        double diferenciaAngulo = normalizarAngulo(anguloEnemigo - yo.getDireccion());

        // SITUACIÓN CRÍTICA: Muy cerca del enemigo - HUIR PÁNICO
        if (distancia < 4) {
            contadorEvasion++;
            double anguloHuir = anguloEnemigo + Math.PI + (random.nextDouble() - 0.5) * 0.5; // Añadir ruido para evadir mejor
            double diferenciaHuir = normalizarAngulo(anguloHuir - yo.getDireccion());

            if (Math.abs(diferenciaHuir) > 0.1) {
                // Giro de pánico más rápido
                return new AccionGirar(Math.signum(diferenciaHuir) * 0.6);
            }
            // Huida a máxima velocidad
            return new AccionMover(3.0);
        }

        // OPORTUNIDAD: Si el enemigo está alineado y no muy cerca, ¡ATACAR!
        if (yo.getCooldown() == 0 && Math.abs(diferenciaAngulo) < 0.15 && distancia > 5 && distancia < 10) {
            return new AccionDisparar();
        }

        // EVASIÓN INTELIGENTE: Mantener distancia media
        if (distancia < 8) {
            // Movimiento evasivo en zigzag
            double anguloEvasivo = anguloEnemigo + Math.PI + Math.sin(contadorEvasion * 0.3) * 0.8;
            double diferenciaEvasivo = normalizarAngulo(anguloEvasivo - yo.getDireccion());

            if (Math.abs(diferenciaEvasivo) > 0.2) {
                contadorEvasion++;
                return new AccionGirar(Math.signum(diferenciaEvasivo) * 0.5);
            }
            return new AccionMover(2.5);
        }

        // POSICIÓN SEGURA: Movimiento táctico aleatorio pero inteligente
        if (distancia > 12 && yo.getCooldown() == 0 && Math.abs(diferenciaAngulo) < 0.3) {
            // Tiro de oportunidad desde lejos
            return new AccionDisparar();
        }

        // Movimiento errático pero direccionado
        if (random.nextDouble() < 0.4) {
            return new AccionGirar((random.nextDouble() - 0.5) * 1.0);
        } else {
            return new AccionMover(1.5 + random.nextDouble() * 2.5);
        }
    }
    
    private double normalizarAngulo(double angulo) {
        while (angulo > Math.PI) angulo -= 2 * Math.PI;
        while (angulo < -Math.PI) angulo += 2 * Math.PI;
        return angulo;
    }
}
