package dev.maiki.thembots;

import dev.maiki.thembots.application.service.ConsolaSerializadorSimulacion;
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
 * Demo con simulación en vivo - muestra cada tick en tiempo real
 */
public class DemoLive {

    public static void main(String[] args) {
        System.out.println("🤖 THEM BOTS - COMBATE EN VIVO 🤖");
        System.out.println("=================================");
        System.out.println();
        System.out.println("Leyenda:");
        System.out.println("  C = Cazador (IA agresiva)    c = cazador destruido");
        System.out.println("  O = Cobarde (IA evasiva)     o = cobarde destruido");  
        System.out.println("  P = Patrulla (IA defensiva)  p = patrulla destruido");
        System.out.println("  * = Proyectil en vuelo");
        System.out.println("  # = Obstáculo");
        System.out.println("  . = Espacio vacío");
        System.out.println();
        System.out.println("🎬 Iniciando en 3 segundos...");
        System.out.println();
        
        esperarSegundos(3);
        
        new DemoLive().ejecutarCombateEnVivo();
    }
    
    public void ejecutarCombateEnVivo() {
        // Crear arena con robots
        Arena arena = crearArenaEpica();
        
        // Crear simulador 
        SimuladorCombate simulador = new SimuladorCombate(arena, 50);
        
        int tick = 0;
        // Ejecutar simulación tick por tick mostrando cada paso
        while (!simulador.estaTerminado() && tick < 50) {
            // Limpiar pantalla
            System.out.print("\033[2J\033[H");
            
            System.out.println("🤖 THEM BOTS - Tick " + tick + " 🤖");
            
            // Mostrar eventos del tick anterior si existen
            if (tick > 0) {
                var eventos = simulador.eventosDelTick(tick - 1);
                String resumen = generarResumenTick(eventos);
                if (!resumen.isEmpty()) {
                    System.out.println("📢 " + resumen);
                }
            }
            System.out.println();

            // Mostrar arena actual
            mostrarArena(arena);
            
            // Mostrar estado de robots
            System.out.println("\n📊 Estado de robots:");
            for (Robot robot : arena.getRobots()) {
                String estado = robot.estaActivo() ? "🟢" : "💀";
                System.out.printf("  %s %s: (%.1f,%.1f) vida: %.0f, cooldown: %.0f\n", 
                    estado, robot.getNombre(), 
                    robot.getPosicion().getX(), robot.getPosicion().getY(),
                    robot.getVida().valor(), robot.getCooldown());
            }
            
            // Avanzar un tick
            simulador.simularTick();
            tick++;
            
            try {
                Thread.sleep(1000); // 1 segundo entre ticks para ver los cambios
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        // Resultado final
        System.out.print("\033[2J\033[H");
        System.out.println("🏆 === RESULTADO FINAL === 🏆");
        var resultado = simulador.resultadoFinal();
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
        
        System.out.println("\n🏆 ¡COMBATE FINALIZADO! 🏆");
        System.out.println("Gracias por ver Them Bots!");
    }
    
    private void mostrarArena(Arena arena) {
        final int ANCHO_CONSOLA = 40;
        final int ALTO_CONSOLA = 20;
        
        char[][] grid = new char[ALTO_CONSOLA][ANCHO_CONSOLA];
        for (int y = 0; y < ALTO_CONSOLA; y++) {
            for (int x = 0; x < ANCHO_CONSOLA; x++) {
                grid[y][x] = '.';
            }
        }

        // Obstáculos
        for (Obstaculo obstaculo : arena.getObstaculos()) {
            int[] pos = mapear(obstaculo.getPosicion(), arena.getAncho(), arena.getAlto());
            if (enRango(pos, ANCHO_CONSOLA, ALTO_CONSOLA)) grid[pos[1]][pos[0]] = '#';
        }

        // Proyectiles
        for (Proyectil proyectil : arena.getProyectiles()) {
            int[] pos = mapear(proyectil.getPosicion(), arena.getAncho(), arena.getAlto());
            if (enRango(pos, ANCHO_CONSOLA, ALTO_CONSOLA)) grid[pos[1]][pos[0]] = '*';
        }

        // Robots
        for (Robot robot : arena.getRobots()) {
            int[] pos = mapear(robot.getPosicion(), arena.getAncho(), arena.getAlto());
            if (enRango(pos, ANCHO_CONSOLA, ALTO_CONSOLA)) {
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
    }
    
    private int[] mapear(Posicion pos, double anchoArena, double altoArena) {
        int x = Math.max(0, Math.min(39, (int) (pos.getX() / anchoArena * 40)));
        int y = Math.max(0, Math.min(19, (int) (pos.getY() / altoArena * 20)));
        return new int[]{x, y};
    }

    private boolean enRango(int[] pos, int ancho, int alto) {
        return pos[0] >= 0 && pos[0] < ancho && pos[1] >= 0 && pos[1] < alto;
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
    
    private Arena crearArenaEpica() {
        // IDs fijos para consistencia
        UUID id1 = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        UUID id2 = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        UUID id3 = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
        
        // Arena 40x20 - mapeo 1:1 con la consola para mejor visibilidad
        
        // CAZADOR - Tank agresivo (esquina superior izquierda)
        Robot cazador = new Robot(
            id1, "Cazador", 
            new Posicion(3.0, 3.0), 0.0,
            new Vida(120.0), 0.0,
            new Radio(1.0), 
            new ComportamientoCazador()
        );
        
        // COBARDE - Escurridizo y rápido (esquina superior derecha)
        Robot cobarde = new Robot(
            id2, "Cobarde", 
            new Posicion(37.0, 3.0), Math.PI,
            new Vida(80.0), 0.0,
            new Radio(1.0), 
            new ComportamientoCobarde()
        );
        
        // PATRULLA - Defensor balanceado (centro inferior)
        Robot patrulla = new Robot(
            id3, "Patrulla", 
            new Posicion(20.0, 17.0), -Math.PI/2,
            new Vida(100.0), 0.0,
            new Radio(1.0), 
            new ComportamientoPatrulla(new Posicion(8.0, 17.0), new Posicion(32.0, 17.0))
        );
        
        // Obstáculos más pequeños para arena compacta
        List<Obstaculo> obstaculos = List.of(
            new Obstaculo(new Posicion(20.0, 10.0), new Radio(2.0)), // Centro
            new Obstaculo(new Posicion(12.0, 8.0), new Radio(1.5)),  // Izquierda
            new Obstaculo(new Posicion(28.0, 8.0), new Radio(1.5)),  // Derecha
            new Obstaculo(new Posicion(8.0, 12.0), new Radio(1.0)),  // Esquina
            new Obstaculo(new Posicion(32.0, 12.0), new Radio(1.0))  // Esquina
        );
        
        return new Arena(40.0, 20.0, List.of(cazador, cobarde, patrulla), obstaculos);
    }
    
    private static void esperarSegundos(int segundos) {
        try {
            Thread.sleep(segundos * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}