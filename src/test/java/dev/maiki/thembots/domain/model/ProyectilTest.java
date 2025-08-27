package dev.maiki.thembots.domain.model;

import dev.maiki.thembots.domain.value.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Proyectil - Tests de entidad proyectil")
class ProyectilTest {

    private UUID proyectilId;
    private UUID robotId;
    private Posicion posicionInicial;
    private Vector direccionInicial;
    private double velocidad;
    private Radio radioProyectil;

    @BeforeEach
    void setUp() {
        proyectilId = UUID.randomUUID();
        robotId = UUID.randomUUID();
        posicionInicial = new Posicion(10.0, 15.0);
        direccionInicial = new Vector(1.0, 0.0); // Hacia la derecha
        velocidad = 5.0;
        radioProyectil = new Radio(0.5);
    }

    @Test
    @DisplayName("Crear proyectil con parámetros válidos")
    void crearProyectilConParametrosValidos() {
        double dmg = 25.0;
        
        Proyectil proyectil = new Proyectil(proyectilId, posicionInicial, direccionInicial,
                                          dmg, robotId, radioProyectil);

        assertEquals(proyectilId, proyectil.getId());
        assertEquals(posicionInicial, proyectil.getPosicion());
        assertEquals(direccionInicial, proyectil.getVector());
        assertEquals(dmg, proyectil.getDmg());
        assertEquals(robotId, proyectil.getOrigen());
        assertEquals(radioProyectil, proyectil.getRadio());
    }

    @Test
    @DisplayName("Proyectil con daño cero")
    void proyectilConDanioCero() {
        Proyectil proyectil = new Proyectil(proyectilId, posicionInicial, direccionInicial,
                                          0.0, robotId, radioProyectil);

        assertEquals(0.0, proyectil.getDmg());
        // Debería ser un proyectil válido incluso con daño 0
        assertNotNull(proyectil);
        assertEquals(proyectilId, proyectil.getId());
    }

    @Test
    @DisplayName("Proyectil con daño negativo")
    void proyectilConDanioNegativo() {
        // La implementación actual parece permitir daño negativo
        // Esto podría representar proyectiles curativos en el futuro
        double danioNegativo = -10.0;
        
        Proyectil proyectil = new Proyectil(proyectilId, posicionInicial, direccionInicial,
                                          danioNegativo, robotId, radioProyectil);

        assertEquals(danioNegativo, proyectil.getDmg());
        assertEquals(proyectilId, proyectil.getId());
    }

    @Test
    @DisplayName("Mover proyectil actualiza posición")
    void moverProyectilActualizaPosicion() {
        Proyectil proyectil = new Proyectil(proyectilId, posicionInicial, direccionInicial,
                                          25.0, robotId, radioProyectil);

        // Posición inicial
        assertEquals(posicionInicial, proyectil.getPosicion());

        // Mover proyectil
        proyectil.avanzar();

        // La posición debe haber cambiado según la dirección
        Posicion nuevaPosicion = proyectil.getPosicion();
        assertNotEquals(posicionInicial, nuevaPosicion);
        
        // Debe haberse movido en la dirección correcta
        assertTrue(nuevaPosicion.getX() > posicionInicial.getX());
        assertEquals(posicionInicial.getY(), nuevaPosicion.getY(), 0.000001);
    }

    @Test
    @DisplayName("Múltiples movimientos del proyectil")
    void multiplesMovimientosDelProyectil() {
        Vector direccionDiagonal = new Vector(1.0, 1.0).normalizar(); // 45 grados
        Proyectil proyectil = new Proyectil(proyectilId, posicionInicial, direccionDiagonal,
                                          25.0, robotId, radioProyectil);

        Posicion pos0 = proyectil.getPosicion();
        
        proyectil.avanzar();
        Posicion pos1 = proyectil.getPosicion();
        
        proyectil.avanzar();
        Posicion pos2 = proyectil.getPosicion();
        
        proyectil.avanzar();
        Posicion pos3 = proyectil.getPosicion();

        // Cada posición debe ser diferente
        assertNotEquals(pos0, pos1);
        assertNotEquals(pos1, pos2);
        assertNotEquals(pos2, pos3);

        // Debe moverse consistentemente en la misma dirección
        double deltaX1 = pos1.getX() - pos0.getX();
        double deltaY1 = pos1.getY() - pos0.getY();
        double deltaX2 = pos2.getX() - pos1.getX();
        double deltaY2 = pos2.getY() - pos1.getY();

        assertEquals(deltaX1, deltaX2, 0.000001);
        assertEquals(deltaY1, deltaY2, 0.000001);
    }

    @Test
    @DisplayName("Proyectil se mueve en dirección específica")
    void proyectilSeMueveEnDireccionEspecifica() {
        // Proyectil moviéndose hacia arriba
        Vector direccionArriba = new Vector(0.0, 1.0);
        Proyectil proyectilArriba = new Proyectil(proyectilId, posicionInicial, direccionArriba,
                                                25.0, robotId, radioProyectil);

        Posicion posInicial = proyectilArriba.getPosicion();
        proyectilArriba.avanzar();
        Posicion posFinal = proyectilArriba.getPosicion();

        assertEquals(posInicial.getX(), posFinal.getX(), 0.000001); // X no cambia
        assertTrue(posFinal.getY() > posInicial.getY()); // Y aumenta

        // Proyectil moviéndose hacia la izquierda
        Vector direccionIzquierda = new Vector(-1.0, 0.0);
        Proyectil proyectilIzquierda = new Proyectil(UUID.randomUUID(), posicionInicial, direccionIzquierda,
                                                   25.0, robotId, radioProyectil);

        posInicial = proyectilIzquierda.getPosicion();
        proyectilIzquierda.avanzar();
        posFinal = proyectilIzquierda.getPosicion();

        assertTrue(posFinal.getX() < posInicial.getX()); // X disminuye
        assertEquals(posInicial.getY(), posFinal.getY(), 0.000001); // Y no cambia
    }

    @Test
    @DisplayName("Proyectil con vector de dirección cero no se mueve")
    void proyectilConVectorDeDireccionCeroNoSeMueve() {
        Vector direccionCero = new Vector(0.0, 0.0);
        Proyectil proyectil = new Proyectil(proyectilId, posicionInicial, direccionCero,
                                          25.0, robotId, radioProyectil);

        Posicion posicionAntes = proyectil.getPosicion();
        proyectil.avanzar();
        Posicion posicionDespues = proyectil.getPosicion();

        assertEquals(posicionAntes, posicionDespues);
    }

    @Test
    @DisplayName("Proyectil mantiene propiedades inmutables")
    void proyectilMantienePropiedadesInmutables() {
        double dmg = 30.0;
        Proyectil proyectil = new Proyectil(proyectilId, posicionInicial, direccionInicial,
                                          dmg, robotId, radioProyectil);

        // Estas propiedades no deben cambiar durante movimientos
        UUID idAntes = proyectil.getId();
        Vector direccionAntes = proyectil.getVector();
        double dmgAntes = proyectil.getDmg();
        UUID origenAntes = proyectil.getOrigen();
        Radio radioAntes = proyectil.getRadio();

        // Mover varias veces
        proyectil.avanzar();
        proyectil.avanzar();
        proyectil.avanzar();

        // Propiedades inmutables
        assertEquals(idAntes, proyectil.getId());
        assertEquals(direccionAntes, proyectil.getVector());
        assertEquals(dmgAntes, proyectil.getDmg());
        assertEquals(origenAntes, proyectil.getOrigen());
        assertEquals(radioAntes, proyectil.getRadio());
    }

    @Test
    @DisplayName("Proyectil con velocidad implícita")
    void proyectilConVelocidadImplicita() {
        // La velocidad está implícita en la magnitud del vector direccion
        Vector direccionRapida = new Vector(2.0, 0.0); // Velocidad 2
        Vector direccionLenta = new Vector(0.5, 0.0); // Velocidad 0.5

        Proyectil proyectilRapido = new Proyectil(UUID.randomUUID(), posicionInicial, direccionRapida,
                                                25.0, robotId, radioProyectil);
        Proyectil proyectilLento = new Proyectil(UUID.randomUUID(), posicionInicial, direccionLenta,
                                               25.0, robotId, radioProyectil);

        proyectilRapido.avanzar();
        proyectilLento.avanzar();

        double distanciaRapido = posicionInicial.distanciaA(proyectilRapido.getPosicion());
        double distanciaLento = posicionInicial.distanciaA(proyectilLento.getPosicion());

        assertTrue(distanciaRapido > distanciaLento);
        assertEquals(2.0, distanciaRapido, 0.000001);
        assertEquals(0.5, distanciaLento, 0.000001);
    }

    @Test
    @DisplayName("Proyectiles con diferentes IDs son distintos")
    void proyectilesConDiferentesIdsSonDistintos() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Proyectil p1 = new Proyectil(id1, posicionInicial, direccionInicial,
                                   25.0, robotId, radioProyectil);
        Proyectil p2 = new Proyectil(id2, posicionInicial, direccionInicial,
                                   25.0, robotId, radioProyectil);

        assertNotEquals(id1, id2);
        assertNotEquals(p1.getId(), p2.getId());
    }

    @Test
    @DisplayName("Proyectil con diferentes owners")
    void proyectilConDiferentesOwners() {
        UUID owner1 = UUID.randomUUID();
        UUID owner2 = UUID.randomUUID();

        Proyectil p1 = new Proyectil(proyectilId, posicionInicial, direccionInicial,
                                   25.0, owner1, radioProyectil);
        Proyectil p2 = new Proyectil(UUID.randomUUID(), posicionInicial, direccionInicial,
                                   25.0, owner2, radioProyectil);

        assertEquals(owner1, p1.getOrigen());
        assertEquals(owner2, p2.getOrigen());
        assertNotEquals(p1.getOrigen(), p2.getOrigen());
    }
}