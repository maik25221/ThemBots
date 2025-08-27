package dev.maiki.thembots.application.service;

import dev.maiki.thembots.domain.model.*;
import dev.maiki.thembots.domain.model.enums.TipoEvento;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio que genera informes detallados de simulación para frontend
 */
public class GeneradorInformeDetallado {
    
    public InformeCompleto generarInforme(Arena arena, 
                                        Map<Integer, List<EventoDeCombate>> historial, 
                                        ResultadoCombate resultado) {
        
        // Generar resumen por tick
        List<ResumenTick> resumenTicks = historial.entrySet().stream()
            .map(entry -> generarResumenTick(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
        
        // Generar estadísticas de robots
        List<EstadisticasRobot> estadisticas = resultado.getRanking();
        
        // Generar eventos importantes
        List<EventoImportante> eventosDestacados = extraerEventosImportantes(historial);
        
        return new InformeCompleto(
            resultado.getTickFinal(),
            resultado.isEmpate(),
            resultado.getGanadorId(),
            resumenTicks,
            estadisticas,
            eventosDestacados
        );
    }
    
    private ResumenTick generarResumenTick(int tick, List<EventoDeCombate> eventos) {
        List<String> decisiones = eventos.stream()
            .filter(e -> e.tipo() == TipoEvento.DECISION_TOMADA)
            .map(e -> (EventoDecision) e)
            .map(ed -> String.format("%s: %s", ed.getNombreRobot(), ed.getRazonamiento()))
            .collect(Collectors.toList());
        
        List<String> acciones = eventos.stream()
            .filter(e -> e.tipo() == TipoEvento.ACCION_EJECUTADA)
            .map(e -> (EventoAccion) e)
            .map(ea -> String.format("%s %s %s", 
                ea.isExitoso() ? "✓" : "✗", 
                ea.getNombreRobot(), 
                ea.getDetalle()))
            .collect(Collectors.toList());
        
        List<String> impactos = eventos.stream()
            .filter(e -> e.tipo() == TipoEvento.IMPACTO_DETECTADO)
            .map(e -> {
                if (e instanceof EventoImpacto) {
                    EventoImpacto ei = (EventoImpacto) e;
                    return String.format("🎯 %s impactó a %s (%.0f daño)", 
                        ei.getNombreDisparador(), ei.getNombreObjetivo(), ei.getDanoInfligido());
                } else {
                    return "🎯 Impacto registrado";
                }
            })
            .collect(Collectors.toList());
        
        long disparos = eventos.stream().filter(e -> e.tipo() == TipoEvento.DISPARO_EJECUTADO).count();
        long destrucciones = eventos.stream().filter(e -> e.tipo() == TipoEvento.ROBOT_DESTRUIDO).count();
        
        return new ResumenTick(tick, decisiones, acciones, impactos, (int)disparos, (int)destrucciones);
    }
    
    private List<EventoImportante> extraerEventosImportantes(Map<Integer, List<EventoDeCombate>> historial) {
        return historial.entrySet().stream()
            .flatMap(entry -> entry.getValue().stream()
                .filter(e -> e.tipo() == TipoEvento.IMPACTO_DETECTADO || 
                           e.tipo() == TipoEvento.ROBOT_DESTRUIDO ||
                           e.tipo() == TipoEvento.DISPARO_EJECUTADO)
                .map(e -> new EventoImportante(entry.getKey(), e.tipo().toString(), e.toString())))
            .collect(Collectors.toList());
    }
    
    // Clases de datos para el informe
    public static class InformeCompleto {
        private final int ticksTotal;
        private final boolean empate;
        private final java.util.UUID ganadorId;
        private final List<ResumenTick> resumenTicks;
        private final List<EstadisticasRobot> estadisticas;
        private final List<EventoImportante> eventosDestacados;
        
        public InformeCompleto(int ticksTotal, boolean empate, java.util.UUID ganadorId,
                             List<ResumenTick> resumenTicks, List<EstadisticasRobot> estadisticas,
                             List<EventoImportante> eventosDestacados) {
            this.ticksTotal = ticksTotal;
            this.empate = empate;
            this.ganadorId = ganadorId;
            this.resumenTicks = resumenTicks;
            this.estadisticas = estadisticas;
            this.eventosDestacados = eventosDestacados;
        }
        
        // Getters
        public int getTicksTotal() { return ticksTotal; }
        public boolean isEmpate() { return empate; }
        public java.util.UUID getGanadorId() { return ganadorId; }
        public List<ResumenTick> getResumenTicks() { return resumenTicks; }
        public List<EstadisticasRobot> getEstadisticas() { return estadisticas; }
        public List<EventoImportante> getEventosDestacados() { return eventosDestacados; }
    }
    
    public static class ResumenTick {
        private final int tick;
        private final List<String> decisiones;
        private final List<String> acciones;
        private final List<String> impactos;
        private final int disparos;
        private final int destrucciones;
        
        public ResumenTick(int tick, List<String> decisiones, List<String> acciones, 
                         List<String> impactos, int disparos, int destrucciones) {
            this.tick = tick;
            this.decisiones = decisiones;
            this.acciones = acciones;
            this.impactos = impactos;
            this.disparos = disparos;
            this.destrucciones = destrucciones;
        }
        
        // Getters
        public int getTick() { return tick; }
        public List<String> getDecisiones() { return decisiones; }
        public List<String> getAcciones() { return acciones; }
        public List<String> getImpactos() { return impactos; }
        public int getDisparos() { return disparos; }
        public int getDestrucciones() { return destrucciones; }
    }
    
    public static class EventoImportante {
        private final int tick;
        private final String tipo;
        private final String descripcion;
        
        public EventoImportante(int tick, String tipo, String descripcion) {
            this.tick = tick;
            this.tipo = tipo;
            this.descripcion = descripcion;
        }
        
        // Getters
        public int getTick() { return tick; }
        public String getTipo() { return tipo; }
        public String getDescripcion() { return descripcion; }
    }
}