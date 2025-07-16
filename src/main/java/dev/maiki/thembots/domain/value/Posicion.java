package dev.maiki.thembots.domain.value;

/**
 * Objeto de valor inmutable que representa una posición en el espacio 2D.
 * Implementado como un record (Java 16+) para aprovechar inmutabilidad y concisión.
 */
public record Posicion(double x, double y) {

    /**
     * Calcula la distancia euclídea entre esta posición y otra.
     */
    public double distanciaA(Posicion otra) {
        double dx = this.x - otra.x;
        double dy = this.y - otra.y;
        return Math.hypot(dx, dy);
    }

    /**
     * Devuelve una nueva posición al avanzar en la dirección del vector dado.
     */
    public Posicion avanzar(Vector vector) {
        return new Posicion(this.x + vector.dx(), this.y + vector.dy());
    }

    /**
     * Comprueba si esta posición está dentro de un rectángulo definido por los límites dados.
     */
    public boolean dentroDeLimites(double ancho, double alto) {
        return x >= 0 && y >= 0 && x <= ancho && y <= alto;
    }

    /**
     * Devuelve la coordenada X.
     */
    public double getX() {
        return x;
    }

    /**
     * Devuelve la coordenada Y.
     */
    public double getY() {
        return y;
    }

}
