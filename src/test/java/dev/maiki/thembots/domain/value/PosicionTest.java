package dev.maiki.thembots.domain.value;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Posicion - Tests de objeto de valor")
class PosicionTest {

    @Test
    @DisplayName("Crear posición con coordenadas válidas")
    void crearPosicionConCoordenadasValidas() {
        Posicion pos = new Posicion(10.5, 20.3);
        
        assertEquals(10.5, pos.x());
        assertEquals(20.3, pos.y());
        assertEquals(10.5, pos.getX());
        assertEquals(20.3, pos.getY());
    }

    @Test
    @DisplayName("Posiciones con mismas coordenadas son iguales")
    void posicionesConMismasCoordenadasSonIguales() {
        Posicion pos1 = new Posicion(5.0, 10.0);
        Posicion pos2 = new Posicion(5.0, 10.0);
        
        assertEquals(pos1, pos2);
        assertEquals(pos1.hashCode(), pos2.hashCode());
    }

    @Test
    @DisplayName("Posiciones con diferentes coordenadas no son iguales")
    void posicionesConDiferentesCoordenadasNoSonIguales() {
        Posicion pos1 = new Posicion(5.0, 10.0);
        Posicion pos2 = new Posicion(5.1, 10.0);
        
        assertNotEquals(pos1, pos2);
    }

    @ParameterizedTest
    @DisplayName("Calcular distancia euclidiana entre posiciones")
    @CsvSource({
        "0.0, 0.0, 3.0, 4.0, 5.0",      // Triángulo 3-4-5
        "0.0, 0.0, 0.0, 5.0, 5.0",      // Vertical
        "0.0, 0.0, 5.0, 0.0, 5.0",      // Horizontal
        "10.0, 10.0, 10.0, 10.0, 0.0",  // Misma posición
        "-2.0, -2.0, 2.0, 2.0, 5.656854249" // Valores negativos
    })
    void calcularDistanciaEuclidiana(double x1, double y1, double x2, double y2, double distanciaEsperada) {
        Posicion pos1 = new Posicion(x1, y1);
        Posicion pos2 = new Posicion(x2, y2);
        
        double distancia = pos1.distanciaA(pos2);
        
        assertEquals(distanciaEsperada, distancia, 0.000001);
    }

    @Test
    @DisplayName("Avanzar con vector devuelve nueva posición")
    void avanzarConVectorDevuelveNuevaPosicion() {
        Posicion inicial = new Posicion(10.0, 10.0);
        Vector vector = new Vector(3.0, 4.0);
        
        Posicion nueva = inicial.avanzar(vector);
        
        assertEquals(13.0, nueva.getX());
        assertEquals(14.0, nueva.getY());
        
        // La posición original no debe cambiar
        assertEquals(10.0, inicial.getX());
        assertEquals(10.0, inicial.getY());
    }

    @Test
    @DisplayName("Avanzar con vector negativo")
    void avanzarConVectorNegativo() {
        Posicion inicial = new Posicion(10.0, 10.0);
        Vector vector = new Vector(-3.0, -2.0);
        
        Posicion nueva = inicial.avanzar(vector);
        
        assertEquals(7.0, nueva.getX());
        assertEquals(8.0, nueva.getY());
    }

    @ParameterizedTest
    @DisplayName("Verificar si está dentro de límites rectangulares")
    @CsvSource({
        "5.0, 5.0, 10.0, 10.0, true",    // Dentro
        "0.0, 0.0, 10.0, 10.0, true",    // En esquina
        "10.0, 10.0, 10.0, 10.0, true",  // En límite
        "5.0, 15.0, 10.0, 10.0, false",  // Fuera por Y
        "15.0, 5.0, 10.0, 10.0, false",  // Fuera por X
        "-1.0, 5.0, 10.0, 10.0, false",  // Negativo en X
        "5.0, -1.0, 10.0, 10.0, false"   // Negativo en Y
    })
    void verificarDentroDeLimites(double x, double y, double ancho, double alto, boolean esperado) {
        Posicion pos = new Posicion(x, y);
        
        assertEquals(esperado, pos.dentroDeLimites(ancho, alto));
    }

    @Test
    @DisplayName("Posición es inmutable")
    void posicionEsInmutable() {
        Posicion pos = new Posicion(5.0, 10.0);
        Vector vector = new Vector(3.0, 4.0);
        
        Posicion nueva = pos.avanzar(vector);
        
        // Posición original no cambia
        assertEquals(5.0, pos.x());
        assertEquals(10.0, pos.y());
        
        // Nueva posición tiene los valores esperados
        assertEquals(8.0, nueva.x());
        assertEquals(14.0, nueva.y());
    }

    @Test
    @DisplayName("toString debe incluir coordenadas")
    void toStringDebeIncluirCoordenadas() {
        Posicion pos = new Posicion(12.5, 7.8);
        String str = pos.toString();
        
        assertTrue(str.contains("12.5"));
        assertTrue(str.contains("7.8"));
    }
}