package dev.maiki.thembots.domain.value;

/**
 * Objeto de valor que representa la vida de un robot.
 * Es inmutable: aplicar daño genera una nueva instancia.
 */
public record Vida(double valor) {

    public Vida {
        if (valor < 0) valor = 0; // No permitir valores negativos
    }

    /**
     * Aplica daño y devuelve una nueva instancia de Vida reducida.
     */
    public Vida restar(double cantidad) {
        return new Vida(Math.max(0, this.valor - cantidad));
    }

    /**
     * Indica si el robot está aún con vida.
     */
    public boolean estaVivo() {
        return this.valor > 0;
    }
}
