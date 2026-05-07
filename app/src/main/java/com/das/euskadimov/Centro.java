package com.das.euskadimov;

public class Centro {

    private int id;
    private String universidad;
    private String nombre;
    private String ciudad;
    private String direccion;
    private double latitud;
    private double longitud;

    public Centro(int id, String universidad, String nombre, String ciudad,
                  String direccion, double latitud, double longitud) {
        this.id = id;
        this.universidad = universidad;
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Centro(String nombre, String ciudad) {
        this.id = -1;
        this.universidad = "";
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.direccion = "";
        this.latitud = 0.0;
        this.longitud = 0.0;
    }

    public int getId() {
        return id;
    }

    public String getUniversidad() {
        return universidad;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }
}