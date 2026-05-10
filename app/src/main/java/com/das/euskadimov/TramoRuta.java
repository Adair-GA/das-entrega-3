package com.das.euskadimov;

public class TramoRuta {

    private String horaInicio;
    private String horaFin;
    private String tipo;
    private String descripcion;
    private String distancia;
    private String duracion;
    private String coste;

    public TramoRuta(String horaInicio, String horaFin, String tipo,
                     String descripcion, String distancia, String duracion, String coste) {
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.distancia = distancia;
        this.duracion = duracion;
        this.coste = coste;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getDistancia() {
        return distancia;
    }

    public String getDuracion() {
        return duracion;
    }

    public String getCoste() {
        return coste;
    }
}