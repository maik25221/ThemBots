package dev.maiki.thembots;

import dev.maiki.thembots.application.model.ConfiguracionCombate;
import dev.maiki.thembots.application.model.InstanciaRobot;
import dev.maiki.thembots.application.service.ConsolaSerializadorSimulacion;
import dev.maiki.thembots.domain.ia.ComportamientoCazador;
import dev.maiki.thembots.domain.ia.ComportamientoCobarde;
import dev.maiki.thembots.domain.ia.ComportamientoPatrulla;
import dev.maiki.thembots.domain.model.*;
import dev.maiki.thembots.domain.ports.Comportamiento;
import dev.maiki.thembots.domain.service.SimuladorCombate;
import dev.maiki.thembots.domain.value.Posicion;
import dev.maiki.thembots.domain.value.Radio;
import dev.maiki.thembots.domain.value.Vida;

import java.util.List;
import java.util.UUID;

/**
 * Demo simple de simulación con visualización en consola.
 * 
 * Para ejecutar:
 * mvn compile exec:java -Dexec.mainClass="dev.maiki.thembots.SimulacionDemo"
 * 
 * O compilar y ejecutar directamente:
 * javac -cp target/classes src/main/java/dev/maiki/thembots/SimulacionDemo.java
 * java -cp target/classes dev.maiki.thembots.SimulacionDemo
 */
public class SimulacionDemo {

    public static void main(String[] args) {
        System.out.println("🤖 THEM BOTS - DEMO DE SIMULACION 🤖");
        System.out.println("===================================");
        System.out.println();
        System.out.println("Leyenda:");
        System.out.println("  C = Cazador (IA agresiva)    c = cazador destruido");
        System.out.println("  O = Cobarde (IA evasiva)     o = cobarde destruido");  
        System.out.println("  P = Patrulla (IA defensiva)  p = patrulla destruido");
        System.out.println("  * = Proyectil en vuelo");
        System.out.println("  # = Obstáculo");
        System.out.println("  . = Espacio vacío");
        System.out.println();
        System.out.println("Presiona Ctrl+C para detener la simulación");
        System.out.println();
        
        // Pausa para leer la información
        esperarSegundos(3);
        
        new SimulacionDemo().ejecutarDemo();
    }
    
    public void ejecutarDemo() {
        // Crear arena y configuración
        Arena arena = crearArenaDemo();
        
        // Crear simulador con límite de ticks (permitir más acción)
        SimuladorCombate simulador = new SimuladorCombate(arena, 80);
        
        // Logger de consola
        ConsolaSerializadorSimulacion logger = new ConsolaSerializadorSimulacion();
        
        // Ejecutar simulación paso a paso
        while (!simulador.estaTerminado()) {
            simulador.simularTick();
        }
        
        // Mostrar simulación completa
        logger.imprimirSimulacion(arena, simulador.historialCompleto(), 
                                 simulador.resultadoFinal());
    }
    
    private Arena crearArenaDemo() {
        // Crear robots con comportamientos diferentes
        UUID id1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID id2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID id3 = UUID.fromString("33333333-3333-3333-3333-333333333333");
        
        // CAZADOR: Tanque agresivo con mucha vida pero lento
        Robot cazador = new Robot(
            id1, "Cazador", 
            new Posicion(15.0, 15.0), 0.0,
            new Vida(140.0), 0.0,
            new Radio(2.0), 
            new ComportamientoCazador()
        );
        
        // COBARDE: Rápido y esquivo pero frágil
        Robot cobarde = new Robot(
            id2, "Cobarde", 
            new Posicion(85.0, 15.0), Math.PI,
            new Vida(90.0), 0.0,
            new Radio(1.5), // Más pequeño = más esquivo
            new ComportamientoCobarde()
        );
        
        // PATRULLA: Defensor balanceado con buena posición estratégica
        Robot patrulla = new Robot(
            id3, "Patrulla", 
            new Posicion(50.0, 85.0), -Math.PI/2,
            new Vida(110.0), 0.0,
            new Radio(2.0), 
            new ComportamientoPatrulla(new Posicion(30.0, 85.0), new Posicion(70.0, 85.0))
        );
        
        // Obstáculos que crean chokepoints y táctica
        List<Obstaculo> obstaculos = List.of(
            new Obstaculo(new Posicion(50.0, 50.0), new Radio(5.0)), // Centro - crea separación
            new Obstaculo(new Posicion(30.0, 30.0), new Radio(3.0)), // Bloqueo parcial
            new Obstaculo(new Posicion(70.0, 30.0), new Radio(3.0)), // Simetría
            new Obstaculo(new Posicion(20.0, 65.0), new Radio(2.5)), // Cobertura para cobarde
            new Obstaculo(new Posicion(80.0, 65.0), new Radio(2.5))  // Cobertura para cazador
        );
        
        return new Arena(100.0, 100.0, List.of(cazador, cobarde, patrulla), obstaculos);
    }
    
    private static void esperarSegundos(int segundos) {
        try {
            Thread.sleep(segundos * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}