package dev.maiki.thembots.domain.value;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Vida - Tests de objeto de valor")
class VidaTest {

    @Test
    @DisplayName("Crear vida con valor positivo válido")
    void crearVidaConValorPositivoValido() {
        Vida vida = new Vida(100.0);
        
        assertEquals(100.0, vida.valor());
        assertTrue(vida.estaVivo());
    }

    @Test
    @DisplayName("Crear vida con valor cero")
    void crearVidaConValorCero() {
        Vida vida = new Vida(0.0);
        
        assertEquals(0.0, vida.valor());
        assertFalse(vida.estaVivo());
    }

    @Test
    @DisplayName("Crear vida con valor negativo se convierte a cero")
    void crearVidaConValorNegativoSeConvierteACero() {
        Vida vida = new Vida(-10.0);
        
        assertEquals(0.0, vida.valor());
        assertFalse(vida.estaVivo());
    }

    @Test
    @DisplayName("Vidas con mismo valor son iguales")
    void vidasConMismoValorSonIguales() {
        Vida vida1 = new Vida(75.5);
        Vida vida2 = new Vida(75.5);
        
        assertEquals(vida1, vida2);
        assertEquals(vida1.hashCode(), vida2.hashCode());
    }

    @Test
    @DisplayName("Vidas con diferente valor no son iguales")
    void vidasConDiferenteValorNoSonIguales() {
        Vida vida1 = new Vida(75.5);
        Vida vida2 = new Vida(75.6);
        
        assertNotEquals(vida1, vida2);
    }

    @Test
    @DisplayName("Restar daño devuelve nueva vida")
    void restarDanioDevuelveNuevaVida() {
        Vida vidaInicial = new Vida(100.0);
        
        Vida vidaDespues = vidaInicial.restar(30.0);
        
        assertEquals(70.0, vidaDespues.valor());
        // Vida original no cambia (inmutable)
        assertEquals(100.0, vidaInicial.valor());
    }

    @Test
    @DisplayName("Restar daño mayor que vida actual")
    void restarDanioMayorQueVidaActual() {
        Vida vidaInicial = new Vida(50.0);
        
        Vida vidaDespues = vidaInicial.restar(80.0);
        
        assertEquals(0.0, vidaDespues.valor());
        assertFalse(vidaDespues.estaVivo());
    }

    @Test
    @DisplayName("Restar daño exacto reduce vida a cero")
    void restarDanioExactoReduceVidaACero() {
        Vida vidaInicial = new Vida(25.0);
        
        Vida vidaDespues = vidaInicial.restar(25.0);
        
        assertEquals(0.0, vidaDespues.valor());
        assertFalse(vidaDespues.estaVivo());
    }

    @Test
    @DisplayName("Restar daño cero no cambia vida")
    void restarDanioCeroNoCambiaVida() {
        Vida vidaInicial = new Vida(80.0);
        
        Vida vidaDespues = vidaInicial.restar(0.0);
        
        assertEquals(80.0, vidaDespues.valor());
        assertTrue(vidaDespues.estaVivo());
    }

    @Test
    @DisplayName("Restar daño negativo incrementa vida")
    void restarDanioNegativoIncrementaVida() {
        Vida vida = new Vida(50.0);
        
        Vida vidaDespues = vida.restar(-10.0);
        
        assertEquals(60.0, vidaDespues.valor());
        assertTrue(vidaDespues.estaVivo());
    }

    @ParameterizedTest
    @DisplayName("Estado vivo correctamente determinado")
    @ValueSource(doubles = {0.1, 1.0, 50.0, 100.0, 999.9})
    void estadoVivoCorrectamenteDeterminado(double valorVida) {
        Vida vida = new Vida(valorVida);
        
        assertTrue(vida.estaVivo());
    }

    @Test
    @DisplayName("Estado muerto con vida cero")
    void estadoMuertoConVidaCero() {
        Vida vida = new Vida(0.0);
        
        assertFalse(vida.estaVivo());
    }

    @Test
    @DisplayName("Operaciones encadenadas de daño")
    void operacionesEncadenadasDeDanio() {
        Vida vidaInicial = new Vida(100.0);
        
        Vida vida1 = vidaInicial.restar(20.0);
        Vida vida2 = vida1.restar(30.0);
        Vida vida3 = vida2.restar(50.0);
        
        assertEquals(100.0, vidaInicial.valor());
        assertEquals(80.0, vida1.valor());
        assertEquals(50.0, vida2.valor());
        assertEquals(0.0, vida3.valor());
        
        assertTrue(vidaInicial.estaVivo());
        assertTrue(vida1.estaVivo());
        assertTrue(vida2.estaVivo());
        assertFalse(vida3.estaVivo());
    }

    @Test
    @DisplayName("Vida es inmutable")
    void vidaEsInmutable() {
        Vida vidaOriginal = new Vida(75.0);
        
        Vida vidaDespues = vidaOriginal.restar(25.0);
        
        // Vida original no debe cambiar
        assertEquals(75.0, vidaOriginal.valor());
        assertTrue(vidaOriginal.estaVivo());
        
        // Nueva vida tiene el valor correcto
        assertEquals(50.0, vidaDespues.valor());
        assertTrue(vidaDespues.estaVivo());
        
        assertNotEquals(vidaOriginal, vidaDespues);
    }

    @Test
    @DisplayName("toString debe incluir valor de vida")
    void toStringDebeIncluirValorDeVida() {
        Vida vida = new Vida(42.5);
        String str = vida.toString();
        
        assertTrue(str.contains("42.5"));
    }
}