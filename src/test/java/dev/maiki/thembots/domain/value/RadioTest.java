package dev.maiki.thembots.domain.value;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Radio - Tests de objeto de valor")
class RadioTest {

    @Test
    @DisplayName("Crear radio con valor positivo válido")
    void crearRadioConValorPositivoValido() {
        Radio radio = new Radio(5.5);
        
        assertEquals(5.5, radio.valor());
    }

    @Test
    @DisplayName("Crear radio con valor cero lanza excepción")
    void crearRadioConValorCeroLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new Radio(0.0));
    }

    @Test
    @DisplayName("Crear radio con valor negativo lanza excepción")
    void crearRadioConValorNegativoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new Radio(-1.0));
    }

    @Test
    @DisplayName("Radios con mismo valor son iguales")
    void radiosConMismoValorSonIguales() {
        Radio radio1 = new Radio(3.14);
        Radio radio2 = new Radio(3.14);
        
        assertEquals(radio1, radio2);
        assertEquals(radio1.hashCode(), radio2.hashCode());
    }

    @Test
    @DisplayName("Radios con diferente valor no son iguales")
    void radiosConDiferenteValorNoSonIguales() {
        Radio radio1 = new Radio(3.14);
        Radio radio2 = new Radio(3.15);
        
        assertNotEquals(radio1, radio2);
    }

    @ParameterizedTest
    @DisplayName("Valores de radio válidos")
    @ValueSource(doubles = {0.1, 1.0, 2.5, 10.0, 100.0, 999.99})
    void valoresDeRadioValidos(double valor) {
        assertDoesNotThrow(() -> new Radio(valor));
        
        Radio radio = new Radio(valor);
        assertEquals(valor, radio.valor());
    }

    @ParameterizedTest
    @DisplayName("Valores de radio inválidos")
    @ValueSource(doubles = {0.0, -0.1, -1.0, -10.0, -100.0, Double.NEGATIVE_INFINITY})
    void valoresDeRadioInvalidos(double valor) {
        assertThrows(IllegalArgumentException.class, () -> new Radio(valor));
    }

    @Test
    @DisplayName("Radio infinito positivo es válido")
    void radioInfinitoPositivoEsValido() {
        assertDoesNotThrow(() -> new Radio(Double.POSITIVE_INFINITY));
        
        Radio radio = new Radio(Double.POSITIVE_INFINITY);
        assertEquals(Double.POSITIVE_INFINITY, radio.valor());
    }

    @Test
    @DisplayName("Radio NaN lanza excepción")
    void radioNaNLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new Radio(Double.NaN));
    }


    @Test
    @DisplayName("Verificar colisión entre dos radios")
    void verificarColisionEntreDosRadios() {
        Radio radio1 = new Radio(3.0);
        Radio radio2 = new Radio(2.0);
        Posicion pos1 = new Posicion(0.0, 0.0);
        Posicion pos2 = new Posicion(4.0, 0.0); // distancia = 4.0
        
        // Distancia (4.0) < suma de radios (5.0) = colisión
        assertTrue(radio1.colisionaCon(pos1, pos2, radio2));
    }

    @Test
    @DisplayName("No hay colisión cuando radios están separados")
    void noHayColisionCuandoRadiosEstanSeparados() {
        Radio radio1 = new Radio(2.0);
        Radio radio2 = new Radio(1.0);
        Posicion pos1 = new Posicion(0.0, 0.0);
        Posicion pos2 = new Posicion(5.0, 0.0); // distancia = 5.0
        
        // Distancia (5.0) > suma de radios (3.0) = no colisión
        assertFalse(radio1.colisionaCon(pos1, pos2, radio2));
    }

    @Test
    @DisplayName("Colisión exacta cuando radios se tocan")
    void colisionExactaCuandoRadiosSeTocan() {
        Radio radio1 = new Radio(3.0);
        Radio radio2 = new Radio(2.0);
        Posicion pos1 = new Posicion(0.0, 0.0);
        Posicion pos2 = new Posicion(5.0, 0.0); // distancia = suma de radios
        
        assertTrue(radio1.colisionaCon(pos1, pos2, radio2));
    }

    @Test
    @DisplayName("Radio es inmutable")
    void radioEsInmutable() {
        Radio radio = new Radio(7.5);
        
        double valorOriginal = radio.valor();
        double area = radio.area();
        double perimetro = radio.perimetro();
        
        // El valor no debe cambiar después de operaciones
        assertEquals(valorOriginal, radio.valor());
    }

    @Test
    @DisplayName("toString debe incluir valor de radio")
    void toStringDebeIncluirValorDeRadio() {
        Radio radio = new Radio(12.34);
        String str = radio.toString();
        
        assertTrue(str.contains("12.34"));
    }
}