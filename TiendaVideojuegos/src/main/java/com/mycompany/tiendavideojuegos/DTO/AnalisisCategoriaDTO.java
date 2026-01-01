package com.mycompany.tiendavideojuegos.DTO;

import java.io.Serializable;

public class AnalisisCategoriaDTO implements Serializable
{
    private String categoria;
    private int cantidad;

    public AnalisisCategoriaDTO() {}

    public AnalisisCategoriaDTO(String categoria, int cantidad)
    {
        this.categoria = categoria;
        this.cantidad = cantidad;
    }

    public String getCategoria()
    {
        return categoria;
    }

    public void setCategoria(String categoria)
    {
        this.categoria = categoria;
    }

    public int getCantidad()
    {
        return cantidad;
    }

    public void setCantidad(int cantidad)
    {
        this.cantidad = cantidad;
    }
}
