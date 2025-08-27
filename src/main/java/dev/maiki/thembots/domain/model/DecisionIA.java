package dev.maiki.thembots.domain.model;

import dev.maiki.thembots.domain.model.accion.Accion;

/**
 * Representa una decisión tomada por una IA junto con su razonamiento
 */
public class DecisionIA {
    
    private final Accion accion;
    private final String razonamiento;
    private final String contextoObservado;
    
    public DecisionIA(Accion accion, String razonamiento, String contextoObservado) {
        this.accion = accion;
        this.razonamiento = razonamiento;
        this.contextoObservado = contextoObservado;
    }
    
    public DecisionIA(Accion accion, String razonamiento) {
        this(accion, razonamiento, "");
    }
    
    public Accion getAccion() { return accion; }
    public String getRazonamiento() { return razonamiento; }
    public String getContextoObservado() { return contextoObservado; }
    
    @Override
    public String toString() {
        return String.format("%s: %s", accion.getClass().getSimpleName(), razonamiento);
    }
}