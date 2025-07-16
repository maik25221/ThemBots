package dev.maiki.thembots.application.usecase;

import dev.maiki.thembots.application.model.ConfiguracionCombate;
import dev.maiki.thembots.domain.model.Arena;
import dev.maiki.thembots.domain.model.ResultadoCombate;
import dev.maiki.thembots.domain.model.Robot;
import dev.maiki.thembots.domain.service.AcumuladorEstadisticas;
import dev.maiki.thembots.domain.service.SimuladorCombate;

import java.util.List;

/**
 * Caso de uso que ejecuta la simulación completa de un combate
 * a partir de una configuración inicial.
 */
public class SimularCombateCompleto {

    public ResultadoCombate ejecutar(ConfiguracionCombate config) {
        List<Robot> robots = config.crearRobots();
        Arena arena = new Arena(config.anchoArena(), config.altoArena(), robots, config.obstaculos());

        AcumuladorEstadisticas stats = new AcumuladorEstadisticas();
        stats.inicializar(robots);

        SimuladorCombate simulador = new SimuladorCombate(arena, config.tickMaximo());
        simulador.simularHastaElFinal();

        stats.finalizar(robots);
        return simulador.resultadoFinal();
    }
}

