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
    private int ticksEnCombate = 0;
    private VistaRobot ultimoEnemigo = null;

    public ComportamientoPatrulla(Posicion puntoA, Posicion puntoB) {
        this.puntoA = puntoA;
        this.puntoB = puntoB;
    }

    @Override
    public Accion decidir(ContextoPercepcion contexto) {
        VistaRobot yo = contexto.miEstado();
        
        // Buscar enemigo más peligroso (cerca y con más vida)
        VistaRobot enemigo = contexto.todosLosRobots().stream()
            .filter(r -> !r.getId().equals(yo.getId()) && !r.isDestruido())
            .min(Comparator.comparingDouble((VistaRobot r) -> r.getPosicion().distanciaA(yo.getPosicion()) - r.getVida() * 0.1))
            .orElse(null);

        if (enemigo != null) {
            double distancia = yo.getPosicion().distanciaA(enemigo.getPosicion());
            double dx = enemigo.getPosicion().getX() - yo.getPosicion().getX();
            double dy = enemigo.getPosicion().getY() - yo.getPosicion().getY();
            double anguloEnemigo = Math.atan2(dy, dx);
            double diferenciaAngulo = normalizarAngulo(anguloEnemigo - yo.getDireccion());
            
            ticksEnCombate++;
            ultimoEnemigo = enemigo;

            // MODO DEFENSOR AGRESIVO: Si enemigo está en rango de patrulla, interceptar
            if (distancia <= 10) {
                // Prioridad máxima: disparar si está alineado
                if (yo.getCooldown() == 0 && Math.abs(diferenciaAngulo) <= 0.2) {
                    return new AccionDisparar();
                }
                
                // Si no está alineado, girar hacia el enemigo
                if (Math.abs(diferenciaAngulo) > 0.15) {
                    return new AccionGirar(Math.signum(diferenciaAngulo) * 0.45);
                }
                
                // Mantener distancia táctica
                if (distancia < 4) {
                    // Demasiado cerca, retroceder
                    return new AccionMover(-1.5);
                } else if (distancia < 7) {
                    // Distancia buena, pequeños movimientos para mantener línea de tiro
                    return new AccionMover(0.5);
                } else {
                    // Acercarse para mejor puntería
                    return new AccionMover(2.0);
                }
            }
            
            // TIRO DE OPORTUNIDAD: Si enemigo lejano está alineado
            if (distancia <= 15 && yo.getCooldown() == 0 && Math.abs(diferenciaAngulo) <= 0.25) {
                return new AccionDisparar();
            }
        } else {
            // Sin enemigos, resetear contador
            ticksEnCombate = 0;
            ultimoEnemigo = null;
        }

        // PATRULLA MEJORADA: Cambiar destino según situación
        Posicion destino = haciaA ? puntoA : puntoB;
        
        // Si estuvo en combate recientemente, ir al punto más seguro
        if (ticksEnCombate > 0 && ultimoEnemigo != null) {
            double distA = ultimoEnemigo.getPosicion().distanciaA(puntoA);
            double distB = ultimoEnemigo.getPosicion().distanciaA(puntoB);
            destino = distA > distB ? puntoA : puntoB;
            haciaA = (destino == puntoA);
        } else {
            // Cambiar de punto si está cerca del actual
            if (yo.getPosicion().distanciaA(destino) < 2.0) {
                haciaA = !haciaA;
                destino = haciaA ? puntoA : puntoB;
            }
        }

        // Movimiento hacia destino
        double dx = destino.getX() - yo.getPosicion().getX();
        double dy = destino.getY() - yo.getPosicion().getY();
        double anguloDestino = Math.atan2(dy, dx);
        double diferenciaAngulo = normalizarAngulo(anguloDestino - yo.getDireccion());

        if (Math.abs(diferenciaAngulo) > 0.2) {
            return new AccionGirar(Math.signum(diferenciaAngulo) * 0.4);
        } else {
            double velocidad = ticksEnCombate > 0 ? 2.5 : 1.5; // Más rápido si hay combate
            return new AccionMover(Math.min(velocidad, yo.getPosicion().distanciaA(destino)));
        }
    }
    
    private double normalizarAngulo(double angulo) {
        while (angulo > Math.PI) angulo -= 2 * Math.PI;
        while (angulo < -Math.PI) angulo += 2 * Math.PI;
        return angulo;
    }
}
