package dev.maiki.thembots.domain.value;

/**
 * Objeto de valor que representa un vector de movimiento en 2D.
 * Implementado como record para garantizar inmutabilidad.
 */
public record Vector(double dx, double dy) {

    /**
     * Devuelve la magnitud (longitud) del vector.
     */
    public double magnitud() {
        return Math.hypot(dx, dy);
    }

    /**
     * Devuelve un nuevo vector con la misma dirección y magnitud 1.
     * Si el vector es cero, devuelve un vector (0,0).
     */
    public Vector normalizar() {
        double mag = magnitud();
        return mag == 0 ? new Vector(0, 0) : new Vector(dx / mag, dy / mag);
    }

    /**
     * Devuelve un nuevo vector escalado por el factor dado.
     */
    public Vector escalar(double factor) {
        double nuevoDx = dx * factor;
        double nuevoDy = dy * factor;
        // Convierte -0.0 a 0.0 para evitar problemas en tests
        if (nuevoDx == 0.0) nuevoDx = 0.0;
        if (nuevoDy == 0.0) nuevoDy = 0.0;
        return new Vector(nuevoDx, nuevoDy);
    }

    /**
     * Suma este vector con otro y devuelve el resultado.
     */
    public Vector sumar(Vector otro) {
        return new Vector(this.dx + otro.dx, this.dy + otro.dy);
    }
}
