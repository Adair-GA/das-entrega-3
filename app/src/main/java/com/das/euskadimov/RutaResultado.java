package com.das.euskadimov;

import java.util.List;

public class RutaResultado {

    private int id;
    private String horaInicio;
    private String horaFin;
    private String resumen;
    private String costeGeneralizado;
    private List<TramoRuta> tramos;
    private boolean desplegada;

    public RutaResultado(int id, String horaInicio, String horaFin,
                         String resumen, String costeGeneralizado,
                         List<TramoRuta> tramos) {
        this.id = id;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.resumen = resumen;
        this.costeGeneralizado = costeGeneralizado;
        this.tramos = tramos;
        this.desplegada = false;
    }

    public int getId() {
        return id;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public String getResumen() {
        return resumen;
    }

    public String getCosteGeneralizado() {
        return costeGeneralizado;
    }

    public List<TramoRuta> getTramos() {
        return tramos;
    }

    public boolean isDesplegada() {
        return desplegada;
    }

    public void setDesplegada(boolean desplegada) {
        this.desplegada = desplegada;
    }
}