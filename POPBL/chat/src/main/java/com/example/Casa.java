package com.example;

import java.io.Serializable;

public class Casa implements Serializable {
    private int id;
    private String vendedor;
    private String descripcion;

    public Casa(int id, String vendedor, String descripcion) {
        this.id = id;
        this.vendedor = vendedor;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public String getVendedor() {
        return vendedor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return "Casa{id=" + id + ", vendedor='" + vendedor + "', descripcion='" + descripcion + "'}";
    }
}