package dev.maiki.thembots.application.usecase;

import dev.maiki.thembots.application.model.ConfiguracionCombate;
import dev.maiki.thembots.domain.model.Arena;
import dev.maiki.thembots.domain.model.Robot;

import java.util.List;

/**
 * Caso de uso que crea una arena a partir de una configuración externa.
 */
public class CrearArenaDesdeConfiguracion {

    public Arena ejecutar(ConfiguracionCombate config) {
        List<Robot> robots = config.crearRobots();
        return new Arena(config.anchoArena(), config.altoArena(), robots, config.obstaculos());
    }
}
