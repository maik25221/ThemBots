package dev.maiki.thembots.application.service;


import dev.maiki.thembots.domain.model.*;
import dev.maiki.thembots.domain.value.Posicion;

import java.util.List;
import java.util.Map;

public class ConsolaSerializadorSimulacion {

    private static final int ANCHO_CONSOLA = 40;
    private static final int ALTO_CONSOLA = 20;

    public void imprimirSimulacion(Arena arena, Map<Integer, List<EventoDeCombate>> historial, ResultadoCombate resultado) {
        double anchoArena = arena.getAncho();
        double altoArena = arena.getAlto();

        for (Map.Entry<Integer, List<EventoDeCombate>> tickEntry : historial.entrySet()) {
            int tick = tickEntry.getKey();
            
            // Limpiar pantalla (aproximado para Windows/Linux)
            System.out.print("\033[2J\033[H");
            
            System.out.println("🤖 THEM BOTS - Tick " + tick + " 🤖");
            
            // Generar resumen de eventos importantes
            String resumen = generarResumenTick(tickEntry.getValue());
            if (!resumen.isEmpty()) {
                System.out.println("📢 " + resumen);
            }
            System.out.println();

            char[][] grid = new char[ALTO_CONSOLA][ANCHO_CONSOLA];
            for (int y = 0; y < ALTO_CONSOLA; y++) {
                for (int x = 0; x < ANCHO_CONSOLA; x++) {
                    grid[y][x] = '.';
                }
            }

            // Obstáculos
            for (Obstaculo obstaculo : arena.getObstaculos()) {
                int[] pos = mapear(obstaculo.getPosicion(), anchoArena, altoArena);
                if (enRango(pos)) grid[pos[1]][pos[0]] = '#';
            }

            // Proyectiles
            for (Proyectil proyectil : arena.getProyectiles()) {
                int[] pos = mapear(proyectil.getPosicion(), anchoArena, altoArena);
                if (enRango(pos)) grid[pos[1]][pos[0]] = '*';
            }

            // Robots
            for (Robot robot : arena.getRobots()) {
                int[] pos = mapear(robot.getPosicion(), anchoArena, altoArena);
                if (enRango(pos)) {
                    char c = robot.estaActivo() ? Character.toUpperCase(robot.getNombre().charAt(0)) : Character.toLowerCase(robot.getNombre().charAt(0));
                    grid[pos[1]][pos[0]] = c;
                }
            }

            // Imprimir cuadrícula con bordes
            System.out.println("┌" + "─".repeat(ANCHO_CONSOLA) + "┐");
            for (int y = 0; y < ALTO_CONSOLA; y++) {
                System.out.print("│");
                for (int x = 0; x < ANCHO_CONSOLA; x++) {
                    System.out.print(grid[y][x]);
                }
                System.out.println("│");
            }
            System.out.println("└" + "─".repeat(ANCHO_CONSOLA) + "┘");
            
            // Mostrar estado de robots
            System.out.println("\n📊 Estado de robots:");
            for (Robot robot : arena.getRobots()) {
                String estado = robot.estaActivo() ? "🟢" : "💀";
                int[] posConsola = mapear(robot.getPosicion(), anchoArena, altoArena);
                System.out.printf("  %s %s: (%.1f,%.1f)->[%d,%d] vida: %.0f, cooldown: %.0f\n", 
                    estado, robot.getNombre(), 
                    robot.getPosicion().getX(), robot.getPosicion().getY(),
                    posConsola[0], posConsola[1],
                    robot.getVida().valor(), robot.getCooldown());
            }

            try {
                Thread.sleep(800); // Más lento para ver mejor los cambios
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // Resultado final
        System.out.print("\033[2J\033[H");
        System.out.println("🏆 === RESULTADO FINAL === 🏆");
        if (resultado.isEmpate()) {
            System.out.println("🤝 ¡EMPATE! Varios robots sobrevivieron");
        } else {
            var ganador = arena.getRobots().stream()
                .filter(r -> r.getId().equals(resultado.getGanadorId()))
                .findFirst();
            if (ganador.isPresent()) {
                System.out.println("👑 GANADOR: " + ganador.get().getNombre());
            }
        }
        System.out.println("⏱️ Duración: " + resultado.getTickFinal() + " ticks");
        System.out.println("\n📈 Ranking final:");
        resultado.getRanking().forEach(r -> {
            String icono = r.isDestruido() ? "💀" : "🏅";
            System.out.printf("  %s %s - Vida: %.1f | Disparos: %d | Impactos: %d | Daño: %.1f\n",
                              icono, r.getNombre(), r.getVidaFinal(), r.getDisparosRealizados(),
                              r.getImpactosLogrados(), r.getDanoCausado());
        });
    }
    
    private String generarResumenTick(List<EventoDeCombate> eventos) {
        long disparos = eventos.stream().filter(e -> e.tipo().toString().contains("DISPARO")).count();
        long impactos = eventos.stream().filter(e -> e.tipo().toString().contains("IMPACTO")).count();
        long destrucciones = eventos.stream().filter(e -> e.tipo().toString().contains("DESTRUIDO")).count();
        
        if (destrucciones > 0) {
            return String.format("💥 %d robot(s) destruido(s)!", destrucciones);
        } else if (impactos > 0) {
            return String.format("🎯 %d impacto(s) registrado(s)!", impactos);
        } else if (disparos > 0) {
            return String.format("🔫 %d disparo(s) realizado(s)", disparos);
        }
        return "🤖 Robots en movimiento...";
    }

    // Mapea una posición real a la cuadrícula de consola
    private int[] mapear(Posicion pos, double anchoArena, double altoArena) {
        // Usar floor en lugar de round para mejor precisión visual
        int x = Math.max(0, Math.min(ANCHO_CONSOLA - 1, (int) (pos.getX() / anchoArena * ANCHO_CONSOLA)));
        int y = Math.max(0, Math.min(ALTO_CONSOLA - 1, (int) (pos.getY() / altoArena * ALTO_CONSOLA)));
        return new int[]{x, y};
    }

    private boolean enRango(int[] pos) {
        return pos[0] >= 0 && pos[0] < ANCHO_CONSOLA && pos[1] >= 0 && pos[1] < ALTO_CONSOLA;
    }
}
