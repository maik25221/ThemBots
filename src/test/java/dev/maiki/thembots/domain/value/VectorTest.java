package dev.maiki.thembots.domain.value;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Vector - Tests de objeto de valor")
class VectorTest {

    @Test
    @DisplayName("Crear vector con componentes válidos")
    void crearVectorConComponentesValidos() {
        Vector vector = new Vector(3.0, 4.0);
        
        assertEquals(3.0, vector.dx());
        assertEquals(4.0, vector.dy());
    }

    @Test
    @DisplayName("Vectores con mismas componentes son iguales")
    void vectoresConMismosComponentesSonIguales() {
        Vector v1 = new Vector(1.5, -2.3);
        Vector v2 = new Vector(1.5, -2.3);
        
        assertEquals(v1, v2);
        assertEquals(v1.hashCode(), v2.hashCode());
    }

    @Test
    @DisplayName("Vectores con diferentes componentes no son iguales")
    void vectoresConDiferentesComponentesNoSonIguales() {
        Vector v1 = new Vector(1.5, -2.3);
        Vector v2 = new Vector(1.5, -2.4);
        
        assertNotEquals(v1, v2);
    }

    @ParameterizedTest
    @DisplayName("Calcular magnitud del vector")
    @CsvSource({
        "3.0, 4.0, 5.0",           // Vector 3-4-5
        "0.0, 5.0, 5.0",           // Vector vertical
        "5.0, 0.0, 5.0",           // Vector horizontal
        "0.0, 0.0, 0.0",           // Vector nulo
        "-3.0, -4.0, 5.0",         // Vector con componentes negativos
        "1.0, 1.0, 1.414213562"    // Vector diagonal unitario
    })
    void calcularMagnitudDelVector(double dx, double dy, double magnitudEsperada) {
        Vector vector = new Vector(dx, dy);
        
        assertEquals(magnitudEsperada, vector.magnitud(), 0.000001);
    }

    @Test
    @DisplayName("Escalar vector por factor positivo")
    void escalarVectorPorFactorPositivo() {
        Vector original = new Vector(2.0, 3.0);
        
        Vector escalado = original.escalar(2.5);
        
        assertEquals(5.0, escalado.dx());
        assertEquals(7.5, escalado.dy());
        
        // Vector original no cambia
        assertEquals(2.0, original.dx());
        assertEquals(3.0, original.dy());
    }

    @Test
    @DisplayName("Escalar vector por factor negativo")
    void escalarVectorPorFactorNegativo() {
        Vector original = new Vector(4.0, -2.0);
        
        Vector escalado = original.escalar(-1.5);
        
        assertEquals(-6.0, escalado.dx());
        assertEquals(3.0, escalado.dy());
    }

    @Test
    @DisplayName("Escalar vector por cero devuelve vector nulo")
    void escalarVectorPorCeroDevuelveVectorNulo() {
        Vector original = new Vector(10.0, -5.0);
        
        Vector escalado = original.escalar(0.0);
        
        assertEquals(0.0, escalado.dx());
        assertEquals(0.0, escalado.dy());
    }

    @Test
    @DisplayName("Normalizar vector devuelve vector unitario")
    void normalizarVectorDevuelveVectorUnitario() {
        Vector original = new Vector(3.0, 4.0);
        
        Vector normalizado = original.normalizar();
        
        assertEquals(0.6, normalizado.dx(), 0.000001);
        assertEquals(0.8, normalizado.dy(), 0.000001);
        assertEquals(1.0, normalizado.magnitud(), 0.000001);
    }

    @Test
    @DisplayName("Normalizar vector nulo devuelve vector cero")
    void normalizarVectorNuloDevuelveVectorCero() {
        Vector vectorNulo = new Vector(0.0, 0.0);
        
        Vector normalizado = vectorNulo.normalizar();
        
        assertEquals(0.0, normalizado.dx());
        assertEquals(0.0, normalizado.dy());
    }

    @Test
    @DisplayName("Sumar vectores devuelve nuevo vector")
    void sumarVectoresDevuelveNuevoVector() {
        Vector v1 = new Vector(3.0, 4.0);
        Vector v2 = new Vector(1.0, 2.0);
        
        Vector suma = v1.sumar(v2);
        
        assertEquals(4.0, suma.dx());
        assertEquals(6.0, suma.dy());
        
        // Vectores originales no cambian
        assertEquals(3.0, v1.dx());
        assertEquals(4.0, v1.dy());
        assertEquals(1.0, v2.dx());
        assertEquals(2.0, v2.dy());
    }

    @Test
    @DisplayName("Sumar vector con vector negativo")
    void sumarVectorConVectorNegativo() {
        Vector v1 = new Vector(5.0, 3.0);
        Vector v2 = new Vector(-2.0, -1.0);
        
        Vector suma = v1.sumar(v2);
        
        assertEquals(3.0, suma.dx());
        assertEquals(2.0, suma.dy());
    }

    @Test
    @DisplayName("Vector es inmutable")
    void vectorEsInmutable() {
        Vector original = new Vector(2.0, 3.0);
        
        Vector escalado = original.escalar(2.0);
        Vector normalizado = original.normalizar();
        
        // Vector original no cambia
        assertEquals(2.0, original.dx());
        assertEquals(3.0, original.dy());
        
        // Las operaciones devuelven nuevos vectores
        assertEquals(4.0, escalado.dx());
        assertEquals(6.0, escalado.dy());
        
        assertNotEquals(original, escalado);
        assertNotEquals(original, normalizado);
    }

    @Test
    @DisplayName("toString debe incluir componentes")
    void toStringDebeIncluirComponentes() {
        Vector vector = new Vector(-1.5, 2.7);
        String str = vector.toString();
        
        assertTrue(str.contains("-1.5") || str.contains("1.5"));
        assertTrue(str.contains("2.7"));
    }
}