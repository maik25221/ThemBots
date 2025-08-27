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
 * Demo simple y limpia de simulación con visualización en consola.
 * 
 * Para ejecutar:
 * mvn compile exec:java -Dexec.mainClass="dev.maiki.thembots.DemoSimple"
 */
public class DemoSimple {

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
        
        new DemoSimple().ejecutarCombate();
    }
    
    public void ejecutarCombate() {
        // Crear arena con robots
        Arena arena = crearArenaEpica();
        
        // Crear simulador (más ticks para arena pequeña = más acción)
        SimuladorCombate simulador = new SimuladorCombate(arena, 50);
        
        // Logger para mostrar el combate
        ConsolaSerializadorSimulacion logger = new ConsolaSerializadorSimulacion();
        
        // Ejecutar simulación completa
        while (!simulador.estaTerminado()) {
            simulador.simularTick();
        }
        
        // Mostrar la simulación paso a paso
        logger.imprimirSimulacion(arena, simulador.historialCompleto(), 
                                 simulador.resultadoFinal());
        
        System.out.println("\n🏆 ¡COMBATE FINALIZADO! 🏆");
        System.out.println("Gracias por ver Them Bots!");
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