package dev.maiki.thembots;

import dev.maiki.thembots.application.service.GeneradorInformeDetallado;
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
 * Demo que genera un informe detallado completo de una simulación
 */
public class DemoInformeDetallado {

    public static void main(String[] args) {
        System.out.println("📊 THEM BOTS - INFORME DETALLADO 📊");
        System.out.println("=====================================");
        System.out.println();
        
        new DemoInformeDetallado().generarInformeCompleto();
    }
    
    public void generarInformeCompleto() {
        // Crear arena de combate
        Arena arena = crearArenaCombate();
        
        // Ejecutar simulación completa
        SimuladorCombate simulador = new SimuladorCombate(arena, 25);
        simulador.simularHastaElFinal();
        
        // Generar informe detallado
        GeneradorInformeDetallado generador = new GeneradorInformeDetallado();
        GeneradorInformeDetallado.InformeCompleto informe = generador.generarInforme(
            arena, simulador.historialCompleto(), simulador.resultadoFinal()
        );
        
        // Mostrar informe
        mostrarInforme(informe);
    }
    
    private void mostrarInforme(GeneradorInformeDetallado.InformeCompleto informe) {
        System.out.println("🏆 RESULTADO FINAL:");
        System.out.println("==================");
        System.out.printf("Duración: %d ticks\n", informe.getTicksTotal());
        if (informe.isEmpate()) {
            System.out.println("Resultado: EMPATE");
        } else {
            var ganador = informe.getEstadisticas().stream()
                .filter(e -> e.getRobotId().equals(informe.getGanadorId()))
                .findFirst();
            if (ganador.isPresent()) {
                System.out.printf("Ganador: %s\n", ganador.get().getNombre());
            }
        }
        System.out.println();
        
        System.out.println("📈 ESTADÍSTICAS FINALES:");
        System.out.println("========================");
        informe.getEstadisticas().forEach(e -> {
            String estado = e.isDestruido() ? "💀" : "🏅";
            System.out.printf("%s %s: Vida %.1f | Disparos: %d | Impactos: %d | Daño: %.1f\n",
                estado, e.getNombre(), e.getVidaFinal(), 
                e.getDisparosRealizados(), e.getImpactosLogrados(), e.getDanoCausado());
        });
        System.out.println();
        
        System.out.println("📋 RESUMEN POR TICK (Primeros 10):");
        System.out.println("===================================");
        informe.getResumenTicks().stream()
            .limit(10)
            .forEach(this::mostrarResumenTick);
        
        System.out.println("\n🚨 EVENTOS IMPORTANTES:");
        System.out.println("=======================");
        informe.getEventosDestacados().stream()
            .limit(15)
            .forEach(e -> {
                System.out.printf("[Tick %d] %s: %s\n", e.getTick(), e.getTipo(), e.getDescripcion());
            });
        
        System.out.println("\n✨ INFORME GENERADO EXITOSAMENTE ✨");
        System.out.println("Este informe puede ser enviado al frontend para visualización completa.");
    }
    
    private void mostrarResumenTick(GeneradorInformeDetallado.ResumenTick tick) {
        System.out.printf("\n🎮 TICK %d:\n", tick.getTick());
        
        if (!tick.getDecisiones().isEmpty()) {
            System.out.println("  🧠 Decisiones:");
            tick.getDecisiones().forEach(d -> System.out.println("    • " + d));
        }
        
        if (!tick.getAcciones().isEmpty()) {
            System.out.println("  ⚡ Acciones:");
            tick.getAcciones().forEach(a -> System.out.println("    • " + a));
        }
        
        if (!tick.getImpactos().isEmpty()) {
            System.out.println("  💥 Impactos:");
            tick.getImpactos().forEach(i -> System.out.println("    • " + i));
        }
        
        if (tick.getDisparos() > 0 || tick.getDestrucciones() > 0) {
            System.out.printf("  📊 Resumen: %d disparos, %d destrucciones\n", 
                tick.getDisparos(), tick.getDestrucciones());
        }
    }
    
    private Arena crearArenaCombate() {
        // IDs deterministas
        UUID id1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID id2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID id3 = UUID.fromString("33333333-3333-3333-3333-333333333333");
        
        // Robots con diferentes estrategias
        Robot cazador = new Robot(
            id1, "Cazador", 
            new Posicion(5.0, 5.0), 0.0,
            new Vida(100.0), 0.0,
            new Radio(1.0), 
            new ComportamientoCazador()
        );
        
        Robot cobarde = new Robot(
            id2, "Cobarde", 
            new Posicion(35.0, 15.0), Math.PI,
            new Vida(80.0), 0.0,
            new Radio(1.0), 
            new ComportamientoCobarde()
        );
        
        Robot patrulla = new Robot(
            id3, "Patrulla", 
            new Posicion(20.0, 10.0), -Math.PI/2,
            new Vida(120.0), 0.0,
            new Radio(1.0), 
            new ComportamientoPatrulla(new Posicion(10.0, 10.0), new Posicion(30.0, 10.0))
        );
        
        // Obstáculos estratégicos
        List<Obstaculo> obstaculos = List.of(
            new Obstaculo(new Posicion(20.0, 5.0), new Radio(2.0)),
            new Obstaculo(new Posicion(15.0, 15.0), new Radio(1.5)),
            new Obstaculo(new Posicion(25.0, 15.0), new Radio(1.5))
        );
        
        return new Arena(40.0, 20.0, List.of(cazador, cobarde, patrulla), obstaculos);
    }
}