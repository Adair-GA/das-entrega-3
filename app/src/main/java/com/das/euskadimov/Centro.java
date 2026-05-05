package com.das.euskadimov;

public class Centro {
    private String nombre;
    private String ciudad;

    public Centro(String nombre, String ciudad) {
        this.nombre = nombre;
        this.ciudad = ciudad;
    }

    public String getNombre() { return nombre; }
    public String getCiudad() { return ciudad; }
}