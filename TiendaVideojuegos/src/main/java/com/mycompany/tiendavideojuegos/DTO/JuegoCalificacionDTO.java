package com.mycompany.tiendavideojuegos.DTO;

import java.io.Serializable;

public class JuegoCalificacionDTO implements Serializable
{
        private String titulo;
    private double promedio;
    private int totalReseñas;

    public JuegoCalificacionDTO() {}

    public JuegoCalificacionDTO(String titulo, double promedio, int totalReseñas)
    {
        this.titulo = titulo;
        this.promedio = promedio;
        this.totalReseñas = totalReseñas;
    }

    public String getTitulo()
    {
        return titulo;
    }

    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
    }

    public double getPromedio()
    {
        return promedio;
    }

    public void setPromedio(double promedio)
    {
        this.promedio = promedio;
    }

    public int getTotalReseñas()
    {
        return totalReseñas;
    }

    public void setTotalReseñas(int totalReseñas)
    {
        this.totalReseñas = totalReseñas;
    }
}
