package dev.maiki.thembots.domain.value;

/**
 * Objeto de valor que representa una dirección angular en radianes.
 * Puede normalizarse entre 0 y 2π para mantener coherencia.
 */
public record Direccion(double angulo) {

    public Direccion {
        // Normalizar a [0, 2π)
        angulo = ((angulo % (2 * Math.PI)) + (2 * Math.PI)) % (2 * Math.PI);
    }

    /**
     * Devuelve una nueva dirección rotada un ángulo relativo (positivo o negativo).
     */
    public Direccion girar(double delta) {
        return new Direccion(this.angulo + delta);
    }

    /**
     * Calcula la diferencia mínima entre dos direcciones.
     */
    public double diferenciaCon(Direccion otra) {
        double diff = this.angulo - otra.angulo;
        return Math.atan2(Math.sin(diff), Math.cos(diff));
    }
}
