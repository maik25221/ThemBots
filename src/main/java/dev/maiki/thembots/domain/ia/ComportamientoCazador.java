package dev.maiki.thembots.domain.ia;

import dev.maiki.thembots.domain.model.DecisionIA;
import dev.maiki.thembots.domain.model.accion.*;
import dev.maiki.thembots.domain.percepcion.ContextoPercepcion;
import dev.maiki.thembots.domain.percepcion.VistaRobot;

import java.util.Comparator;

public class ComportamientoCazador implements dev.maiki.thembots.domain.ports.Comportamiento {

    @Override
    public Accion decidir(ContextoPercepcion contexto) {
        VistaRobot yo = contexto.miEstado();

        // Buscar el enemigo más débil que esté cerca (estrategia de cazador inteligente)
        VistaRobot objetivo = contexto.todosLosRobots().stream()
            .filter(r -> !r.getId().equals(yo.getId()) && !r.isDestruido())
            .min(Comparator.comparingDouble((VistaRobot r) -> r.getVida())
                          .thenComparingDouble(r -> r.getPosicion().distanciaA(yo.getPosicion())))
            .orElse(null);

        if (objetivo == null) return new AccionNada();

        double distancia = yo.getPosicion().distanciaA(objetivo.getPosicion());
        double dx = objetivo.getPosicion().getX() - yo.getPosicion().getX();
        double dy = objetivo.getPosicion().getY() - yo.getPosicion().getY();
        double anguloObjetivo = Math.atan2(dy, dx);
        double diferenciaAngulo = normalizarAngulo(anguloObjetivo - yo.getDireccion());

        // PRIORITARIO: Si puede disparar y está alineado, ¡DISPARAR!
        if (yo.getCooldown() == 0 && Math.abs(diferenciaAngulo) <= 0.15 && distancia <= 12) {
            return new AccionDisparar();
        }
        
        // Si no está mirando al objetivo, girar rápido
        if (Math.abs(diferenciaAngulo) > 0.15) {
            return new AccionGirar(Math.signum(diferenciaAngulo) * 0.4);
        }

        // Estrategia según distancia
        if (distancia <= 2.5) {
            // MUY CERCA: Retroceder un poco mientras mantiene la mira
            return new AccionMover(-0.5);
        } else if (distancia <= 8) {
            // DISTANCIA ÓPTIMA: Mantener posición y disparar
            if (yo.getCooldown() == 0) {
                return new AccionDisparar();
            } else {
                // Pequeños ajustes de posición mientras espera cooldown
                return new AccionMover(0.5);
            }
        } else {
            // LEJOS: Avanzar agresivamente
            return new AccionMover(Math.min(3.0, distancia * 0.4));
        }
    }
    
    private double normalizarAngulo(double angulo) {
        while (angulo > Math.PI) angulo -= 2 * Math.PI;
        while (angulo < -Math.PI) angulo += 2 * Math.PI;
        return angulo;
    }
    
    @Override
    public DecisionIA decidirConDetalle(ContextoPercepcion contexto) {
        VistaRobot yo = contexto.miEstado();

        // Buscar el enemigo más débil que esté cerca (estrategia de cazador inteligente)
        VistaRobot objetivo = contexto.todosLosRobots().stream()
            .filter(r -> !r.getId().equals(yo.getId()) && !r.isDestruido())
            .min(Comparator.comparingDouble((VistaRobot r) -> r.getVida())
                          .thenComparingDouble(r -> r.getPosicion().distanciaA(yo.getPosicion())))
            .orElse(null);

        if (objetivo == null) {
            return new DecisionIA(new AccionNada(), 
                "No hay enemigos detectados en la arena", 
                String.format("Robots visibles: %d, Todos destruidos", contexto.todosLosRobots().size()));
        }

        double distancia = yo.getPosicion().distanciaA(objetivo.getPosicion());
        double dx = objetivo.getPosicion().getX() - yo.getPosicion().getX();
        double dy = objetivo.getPosicion().getY() - yo.getPosicion().getY();
        double anguloObjetivo = Math.atan2(dy, dx);
        double diferenciaAngulo = normalizarAngulo(anguloObjetivo - yo.getDireccion());
        
        String contextoObservado = String.format("Objetivo: %s (vida: %.0f) a distancia %.1f, ángulo %.2f°, cooldown: %.0f",
            objetivo.getId().toString().substring(0,8), objetivo.getVida(), distancia, 
            Math.toDegrees(Math.abs(diferenciaAngulo)), yo.getCooldown());

        // PRIORITARIO: Si puede disparar y está alineado, ¡DISPARAR!
        if (yo.getCooldown() == 0 && Math.abs(diferenciaAngulo) <= 0.15 && distancia <= 12) {
            return new DecisionIA(new AccionDisparar(),
                String.format("DISPARO LETAL: Objetivo en mira perfecta (%.1f° desviación) dentro de rango (%.1f ≤ 12)", 
                    Math.toDegrees(Math.abs(diferenciaAngulo)), distancia),
                contextoObservado);
        }
        
        // Si no está mirando al objetivo, girar rápido
        if (Math.abs(diferenciaAngulo) > 0.15) {
            return new DecisionIA(new AccionGirar(Math.signum(diferenciaAngulo) * 0.4),
                String.format("GIRANDO hacia objetivo: %.1f° desviación, girando %.1f°", 
                    Math.toDegrees(Math.abs(diferenciaAngulo)), 
                    Math.toDegrees(Math.signum(diferenciaAngulo) * 0.4)),
                contextoObservado);
        }

        // Estrategia según distancia
        if (distancia < 4) {
            return new DecisionIA(new AccionMover(-2.0),
                String.format("RETROCEDIENDO: Demasiado cerca (%.1f < 4), retroceso táctico", distancia),
                contextoObservado);
        } else if (distancia > 8) {
            return new DecisionIA(new AccionMover(3.0),
                String.format("ACERCÁNDOSE: Fuera de rango óptimo (%.1f > 8), avance agresivo", distancia),
                contextoObservado);
        } else {
            return new DecisionIA(new AccionMover(1.5),
                String.format("PERSIGUIENDO: Distancia óptima (%.1f), mantener presión", distancia),
                contextoObservado);
        }
    }
}
