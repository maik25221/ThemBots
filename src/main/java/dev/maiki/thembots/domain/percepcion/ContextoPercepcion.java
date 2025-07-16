package dev.maiki.thembots.domain.percepcion;

import dev.maiki.thembots.domain.model.Obstaculo;
import dev.maiki.thembots.domain.model.Proyectil;

import java.util.List;
import java.util.UUID;

/**
 * Interfaz que representa la percepción de un robot durante un tick.
 * Proporciona acceso completo al estado de la arena y del propio robot.
 */
public interface ContextoPercepcion {

    UUID idPropio();

    VistaRobot miEstado();

    List<VistaRobot> todosLosRobots();

    List<Proyectil> proyectiles();

    List<Obstaculo> obstaculos();

    double anchoArena();

    double altoArena();

    int tickActual();
}
