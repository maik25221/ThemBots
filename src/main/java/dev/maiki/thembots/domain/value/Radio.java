package dev.maiki.thembots.domain.value;

/**
 * Objeto de valor que representa el radio de una entidad circular.
 * Usado en robots, obstáculos y proyectiles para calcular colisiones.
 */
public record Radio(double valor) {

    public Radio {
        if (valor <= 0) {
            throw new IllegalArgumentException("El radio debe ser positivo");
        }
    }

    /**
     * Calcula si dos objetos con radio colisionan, dado otro centro y su radio.
     */
    public boolean colisionaCon(Posicion centro1, Posicion centro2, Radio otroRadio) {
        double distancia = centro1.distanciaA(centro2);
        return distancia <= (this.valor + otroRadio.valor);
    }
}
