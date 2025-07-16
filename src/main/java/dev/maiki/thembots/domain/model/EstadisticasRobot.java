package dev.maiki.thembots.domain.model;

import java.util.UUID;

/**
 * Estadísticas acumuladas para un robot durante la simulación.
 * Sirven para análisis, ranking y resumen del combate.
 */
public class EstadisticasRobot {

    private final UUID robotId;
    private final String nombre;

    private int ticksVivo;
    private double vidaFinal;
    private boolean destruido;

    private int disparosRealizados;
    private int impactosLogrados;
    private double danoCausado;
    private double danoRecibido;

    public EstadisticasRobot(UUID robotId, String nombre) {
        this.robotId = robotId;
        this.nombre = nombre;
    }

    public void registrarTickVivo() {
        ticksVivo++;
    }

    public void registrarDisparo() {
        disparosRealizados++;
    }

    public void registrarImpacto(double dano) {
        impactosLogrados++;
        danoCausado += dano;
    }

    public void registrarDanioRecibido(double dano) {
        danoRecibido += dano;
    }

    public void marcarDestruido() {
        destruido = true;
    }

    public UUID getRobotId() {
        return robotId;
    }

    // Getters

    public String getNombre() {
        return nombre;
    }

    public int getTicksVivo() {
        return ticksVivo;
    }

    public double getVidaFinal() {
        return vidaFinal;
    }

    public void setVidaFinal(double vidaFinal) {
        this.vidaFinal = vidaFinal;
    }

    public boolean isDestruido() {
        return destruido;
    }

    public int getDisparosRealizados() {
        return disparosRealizados;
    }

    public int getImpactosLogrados() {
        return impactosLogrados;
    }

    public double getDanoCausado() {
        return danoCausado;
    }

    public double getDanoRecibido() {
        return danoRecibido;
    }
}
