package dev.maiki.thembots.application.usecase;

import dev.maiki.thembots.application.model.ConfiguracionCombate;
import dev.maiki.thembots.application.model.InstanciaRobot;
import dev.maiki.thembots.domain.ia.ComportamientoCazador;
import dev.maiki.thembots.domain.ia.ComportamientoCobarde;
import dev.maiki.thembots.domain.model.Obstaculo;
import dev.maiki.thembots.domain.model.ResultadoCombate;
import dev.maiki.thembots.domain.model.accion.AccionDisparar;
import dev.maiki.thembots.domain.model.accion.AccionNada;
import dev.maiki.thembots.domain.ports.Comportamiento;
import dev.maiki.thembots.domain.value.Posicion;
import dev.maiki.thembots.domain.value.Radio;
import dev.maiki.thembots.domain.value.Vida;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SimularCombateCompleto - Tests de caso de uso principal")
class SimularCombateCompletoTest {

    private SimularCombateCompleto caso;
    private Comportamiento comportamientoAtacante;
    private Comportamiento comportamientoQuieto;

    @BeforeEach
    void setUp() {
        caso = new SimularCombateCompleto();
        comportamientoAtacante = contexto -> new AccionDisparar();
        comportamientoQuieto = contexto -> new AccionNada();
    }

    @Test
    @DisplayName("Ejecutar combate con configuración básica")
    void ejecutarCombateConConfiguracionBasica() {
        ConfiguracionCombate config = crearConfiguracionBasica();

        ResultadoCombate resultado = caso.ejecutar(config);

        assertNotNull(resultado);
        assertNotNull(resultado.getRanking());
        assertTrue(resultado.getRanking().size() > 0);
    }

    @Test
    @DisplayName("Combate con un solo robot termina inmediatamente")
    void combateConUnSoloRobotTerminaInmediatamente() {
        InstanciaRobot robotSolo = crearInstanciaRobot("SoloBot", 
                new Posicion(50.0, 50.0), new Vida(100.0), comportamientoQuieto);
        
        ConfiguracionCombate config = crearConfiguracion(100.0, 100.0, 10, 
                List.of(robotSolo), List.of());

        ResultadoCombate resultado = caso.ejecutar(config);

        assertFalse(resultado.isEmpate());
        assertEquals(robotSolo.getId(), resultado.getGanadorId());
        assertEquals(1, resultado.getRanking().size());
        assertEquals("SoloBot", resultado.getRanking().get(0).getNombre());
    }

    @Test
    @DisplayName("Combate entre atacante y víctima")
    void combateEntreAtacanteYVictima() {
        InstanciaRobot atacante = crearInstanciaRobot("Atacante", 
                new Posicion(10.0, 10.0), new Vida(100.0), comportamientoAtacante);
        InstanciaRobot victima = crearInstanciaRobot("Victima", 
                new Posicion(12.0, 10.0), new Vida(20.0), comportamientoQuieto);
        
        ConfiguracionCombate config = crearConfiguracion(100.0, 100.0, 20, 
                List.of(atacante, victima), List.of());

        ResultadoCombate resultado = caso.ejecutar(config);

        assertEquals(atacante.getId(), resultado.getGanadorId());
        assertFalse(resultado.isEmpate());
        
        // Verificar ranking
        assertEquals(2, resultado.getRanking().size());
        assertEquals("Atacante", resultado.getRanking().get(0).getNombre());
        assertTrue(resultado.getRanking().get(0).getVidaFinal() > 0);
        assertEquals("Victima", resultado.getRanking().get(1).getNombre());
        assertTrue(resultado.getRanking().get(1).isDestruido());
    }

    @Test
    @DisplayName("Combate con límite de ticks")
    void combateConLimiteDeTicks() {
        InstanciaRobot robot1 = crearInstanciaRobot("Robot1", 
                new Posicion(10.0, 10.0), new Vida(100.0), comportamientoQuieto);
        InstanciaRobot robot2 = crearInstanciaRobot("Robot2", 
                new Posicion(90.0, 90.0), new Vida(100.0), comportamientoQuieto);
        
        // Límite muy bajo para forzar empate por tiempo
        ConfiguracionCombate config = crearConfiguracion(100.0, 100.0, 2, 
                List.of(robot1, robot2), List.of());

        ResultadoCombate resultado = caso.ejecutar(config);

        // Debería terminar por límite de tiempo
        assertTrue(resultado.getTickFinal() <= 2);
        
        // Con robots quietos y separados, debería haber empate o ganar el de mayor vida inicial
        assertEquals(2, resultado.getRanking().size());
    }

    @Test
    @DisplayName("Combate con obstáculos")
    void combateConObstaculos() {
        InstanciaRobot robot1 = crearInstanciaRobot("Robot1", 
                new Posicion(10.0, 10.0), new Vida(100.0), new ComportamientoCazador());
        InstanciaRobot robot2 = crearInstanciaRobot("Robot2", 
                new Posicion(30.0, 10.0), new Vida(100.0), comportamientoQuieto);
        
        // Obstáculo entre los robots
        Obstaculo obstaculo = new Obstaculo(new Posicion(20.0, 10.0), new Radio(3.0));
        
        ConfiguracionCombate config = crearConfiguracion(100.0, 100.0, 50, 
                List.of(robot1, robot2), List.of(obstaculo));

        ResultadoCombate resultado = caso.ejecutar(config);

        assertNotNull(resultado);
        assertEquals(2, resultado.getRanking().size());
        // El obstáculo debería afectar la dinámica del combate
    }

    @Test
    @DisplayName("Combate con múltiples robots")
    void combateConMultiplesRobots() {
        InstanciaRobot cazador = crearInstanciaRobot("Cazador", 
                new Posicion(25.0, 25.0), new Vida(120.0), new ComportamientoCazador());
        InstanciaRobot cobarde = crearInstanciaRobot("Cobarde", 
                new Posicion(75.0, 25.0), new Vida(80.0), new ComportamientoCobarde());
        InstanciaRobot atacante = crearInstanciaRobot("Atacante", 
                new Posicion(25.0, 75.0), new Vida(100.0), comportamientoAtacante);
        InstanciaRobot victima = crearInstanciaRobot("Victima", 
                new Posicion(75.0, 75.0), new Vida(60.0), comportamientoQuieto);
        
        ConfiguracionCombate config = crearConfiguracion(100.0, 100.0, 100, 
                List.of(cazador, cobarde, atacante, victima), List.of());

        ResultadoCombate resultado = caso.ejecutar(config);

        assertNotNull(resultado);
        assertEquals(4, resultado.getRanking().size());
        
        // Verificar que el ranking está ordenado por rendimiento
        for (int i = 0; i < resultado.getRanking().size() - 1; i++) {
            double vida1 = resultado.getRanking().get(i).getVidaFinal();
            double vida2 = resultado.getRanking().get(i + 1).getVidaFinal();
            assertTrue(vida1 >= vida2, "El ranking debe estar ordenado por vida final");
        }
    }

    @Test
    @DisplayName("Arena de diferentes tamaños")
    void arenaDeDiferentesTamanos() {
        InstanciaRobot robot1 = crearInstanciaRobot("Robot1", 
                new Posicion(5.0, 5.0), new Vida(100.0), comportamientoQuieto);
        InstanciaRobot robot2 = crearInstanciaRobot("Robot2", 
                new Posicion(15.0, 5.0), new Vida(100.0), comportamientoQuieto);
        
        // Arena muy pequeña
        ConfiguracionCombate configPequena = crearConfiguracion(20.0, 10.0, 10, 
                List.of(robot1, robot2), List.of());

        ResultadoCombate resultado = caso.ejecutar(configPequena);

        assertNotNull(resultado);
        assertEquals(2, resultado.getRanking().size());
    }

    @Test
    @DisplayName("Combate determinista con misma configuración")
    void combateDeterministaConMismaConfiguracion() {
        ConfiguracionCombate config = crearConfiguracionBasica();

        ResultadoCombate resultado1 = caso.ejecutar(config);
        ResultadoCombate resultado2 = caso.ejecutar(config);

        // Los resultados deben ser idénticos (determinismo)
        assertEquals(resultado1.getTickFinal(), resultado2.getTickFinal());
        assertEquals(resultado1.isEmpate(), resultado2.isEmpate());
        assertEquals(resultado1.getGanadorId(), resultado2.getGanadorId());
        assertEquals(resultado1.getRanking().size(), resultado2.getRanking().size());
        
        for (int i = 0; i < resultado1.getRanking().size(); i++) {
            assertEquals(resultado1.getRanking().get(i).getRobotId(), 
                        resultado2.getRanking().get(i).getRobotId());
            assertEquals(resultado1.getRanking().get(i).getVidaFinal(), 
                        resultado2.getRanking().get(i).getVidaFinal());
        }
    }

    @Test
    @DisplayName("Estadísticas se generan correctamente")
    void estadisticasSeGeneranCorrectamente() {
        InstanciaRobot atacante = crearInstanciaRobot("Atacante", 
                new Posicion(10.0, 10.0), new Vida(100.0), comportamientoAtacante);
        InstanciaRobot victima = crearInstanciaRobot("Victima", 
                new Posicion(12.0, 10.0), new Vida(50.0), comportamientoQuieto);
        
        ConfiguracionCombate config = crearConfiguracion(100.0, 100.0, 20, 
                List.of(atacante, victima), List.of());

        ResultadoCombate resultado = caso.ejecutar(config);

        // Verificar estadísticas básicas
        assertEquals(2, resultado.getRanking().size());
        resultado.getRanking().forEach(stats -> {
            assertNotNull(stats.getRobotId());
            assertNotNull(stats.getNombre());
            assertTrue(stats.getVidaFinal() >= 0);
        });
        
        // El atacante debería tener estadísticas de disparos
        var statsAtacante = resultado.getRanking().stream()
                .filter(s -> s.getNombre().equals("Atacante"))
                .findFirst();
        assertTrue(statsAtacante.isPresent());
    }

    // Métodos auxiliares
    private ConfiguracionCombate crearConfiguracionBasica() {
        InstanciaRobot robot1 = crearInstanciaRobot("TestBot1", 
                new Posicion(20.0, 20.0), new Vida(100.0), comportamientoQuieto);
        InstanciaRobot robot2 = crearInstanciaRobot("TestBot2", 
                new Posicion(80.0, 20.0), new Vida(100.0), comportamientoQuieto);
        
        return crearConfiguracion(100.0, 100.0, 50, List.of(robot1, robot2), List.of());
    }

    private ConfiguracionCombate crearConfiguracion(double ancho, double alto, int tickMaximo,
                                                   List<InstanciaRobot> robots, List<Obstaculo> obstaculos) {
        // Crear una implementación simple para testing
        return new ConfiguracionCombate() {
            @Override
            public double anchoArena() { return ancho; }
            @Override
            public double altoArena() { return alto; }
            @Override
            public int tickMaximo() { return tickMaximo; }
            @Override
            public List<Obstaculo> obstaculos() { return obstaculos; }
            @Override
            public List<dev.maiki.thembots.domain.model.Robot> crearRobots() {
                return robots.stream().map(InstanciaRobot::aEntidad).toList();
            }
        };
    }

    private InstanciaRobot crearInstanciaRobot(String nombre, Posicion posicion, 
                                             Vida vida, Comportamiento comportamiento) {
        // Crear una implementación simple para testing
        return new InstanciaRobot() {
            private final UUID id = UUID.randomUUID();
            
            public UUID getId() { return id; }
            
            @Override
            public dev.maiki.thembots.domain.model.Robot aEntidad() {
                return new dev.maiki.thembots.domain.model.Robot(
                    id, nombre, posicion, 0.0, vida, 0.0, new Radio(1.5), comportamiento
                );
            }
        };
    }
}