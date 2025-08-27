package dev.maiki.thembots;

import dev.maiki.thembots.domain.ia.ComportamientoCazador;
import dev.maiki.thembots.domain.ia.ComportamientoCobarde;
import dev.maiki.thembots.domain.ia.ComportamientoPatrulla;
import dev.maiki.thembots.domain.model.*;
import dev.maiki.thembots.domain.service.SimuladorCombate;
import dev.maiki.thembots.domain.value.Posicion;
import dev.maiki.thembots.domain.value.Radio;
import dev.maiki.thembots.domain.value.Vida;

import java.util.List;
import java.util.UUID;

/**
 * Demo de debug para identificar problemas en la simulación
 */
public class DemoDebug {

    public static void main(String[] args) {
        System.out.println("🔍 THEM BOTS - DEBUG MODE 🔍");
        System.out.println("=============================");
        
        new DemoDebug().debugSimulacion();
    }
    
    public void debugSimulacion() {
        // Arena simple para debug
        Arena arena = crearArenaSimple();
        
        System.out.println("📍 Posiciones iniciales:");
        for (Robot robot : arena.getRobots()) {
            System.out.printf("  %s: (%.1f, %.1f) - Vida: %.0f, Dirección: %.2f\n", 
                robot.getNombre(), 
                robot.getPosicion().getX(), robot.getPosicion().getY(),
                robot.getVida().valor(), robot.getDireccion());
        }
        System.out.println();
        
        // Crear simulador para pocos ticks
        SimuladorCombate simulador = new SimuladorCombate(arena, 10);
        
        int tick = 0;
        while (!simulador.estaTerminado() && tick < 5) {
            System.out.printf("🎯 === TICK %d ===\n", tick);
            
            // Mostrar estado ANTES del tick
            System.out.println("ANTES:");
            for (Robot robot : arena.getRobots()) {
                System.out.printf("  %s: (%.1f, %.1f) - Vida: %.0f, Cooldown: %.0f\n", 
                    robot.getNombre(), 
                    robot.getPosicion().getX(), robot.getPosicion().getY(),
                    robot.getVida().valor(), robot.getCooldown());
            }
            
            // Ejecutar un tick
            simulador.simularTick();
            
            // Mostrar estado DESPUÉS del tick
            System.out.println("DESPUÉS:");
            for (Robot robot : arena.getRobots()) {
                System.out.printf("  %s: (%.1f, %.1f) - Vida: %.0f, Cooldown: %.0f\n", 
                    robot.getNombre(), 
                    robot.getPosicion().getX(), robot.getPosicion().getY(),
                    robot.getVida().valor(), robot.getCooldown());
            }
            
            // Mostrar proyectiles
            if (!arena.getProyectiles().isEmpty()) {
                System.out.println("PROYECTILES:");
                for (Proyectil p : arena.getProyectiles()) {
                    System.out.printf("  * (%.1f, %.1f)\n", p.getPosicion().getX(), p.getPosicion().getY());
                }
            }
            
            // Mostrar eventos
            var eventos = simulador.eventosDelTick(tick);
            if (!eventos.isEmpty()) {
                System.out.println("EVENTOS:");
                for (var evento : eventos) {
                    System.out.println("  - " + evento.tipo());
                }
            }
            
            System.out.println();
            tick++;
            
            try {
                Thread.sleep(2000); // 2 segundos entre ticks para ver los cambios
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        System.out.println("🏁 Debug completado!");
    }
    
    private Arena crearArenaSimple() {
        // IDs fijos
        UUID id1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID id2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID id3 = UUID.fromString("33333333-3333-3333-3333-333333333333");
        
        // Solo 2 robots muy separados para simplificar el debug
        Robot cazador = new Robot(
            id1, "Cazador", 
            new Posicion(5.0, 10.0), 0.0, // Mirando hacia la derecha
            new Vida(100.0), 0.0,
            new Radio(1.0), 
            new ComportamientoCazador()
        );
        
        Robot cobarde = new Robot(
            id2, "Cobarde", 
            new Posicion(35.0, 10.0), Math.PI, // Mirando hacia la izquierda
            new Vida(100.0), 0.0,
            new Radio(1.0), 
            new ComportamientoCobarde()
        );
        
        // Sin obstáculos para simplificar
        List<Obstaculo> obstaculos = List.of();
        
        return new Arena(40.0, 20.0, List.of(cazador, cobarde), obstaculos);
    }
}