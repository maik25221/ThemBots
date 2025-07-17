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
            System.out.println("\n=== Tick " + tick + " ===");

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

            // Imprimir cuadrícula
            for (int y = 0; y < ALTO_CONSOLA; y++) {
                for (int x = 0; x < ANCHO_CONSOLA; x++) {
                    System.out.print(grid[y][x]);
                }
                System.out.println();
            }

            // Imprimir eventos del tick
            for (EventoDeCombate evento : tickEntry.getValue()) {
                System.out.println("  - " + evento.tipo());
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Resultado final
        System.out.println("\n=== Resultado Final ===");
        if (resultado.isEmpate()) {
            System.out.println("Empate entre robots.");
        } else {
            System.out.println("Ganador: " + resultado.getGanadorId());
        }
        System.out.println("Ticks totales: " + resultado.getTotalTicks());
        System.out.println("Ranking:");
        resultado.getRanking().forEach(r -> {
            System.out.printf("  - %s (Vida: %.1f, Destruido: %s, Disparos: %d, Impactos: %d, Daño causado: %.1f, Daño recibido: %.1f)\n",
                              r.getNombre(), r.getVidaFinal(), r.isDestruido(), r.getDisparosRealizados(),
                              r.getImpactosLogrados(), r.getDanoCausado(), r.getDanoRecibido());
        });
    }

    // Mapea una posición real a la cuadrícula de consola
    private int[] mapear(Posicion pos, double anchoArena, double altoArena) {
        int x = (int) Math.round(pos.getX() / anchoArena * (ANCHO_CONSOLA - 1));
        int y = (int) Math.round(pos.getY() / altoArena * (ALTO_CONSOLA - 1));
        return new int[]{x, y};
    }

    private boolean enRango(int[] pos) {
        return pos[0] >= 0 && pos[0] < ANCHO_CONSOLA && pos[1] >= 0 && pos[1] < ALTO_CONSOLA;
    }
}
