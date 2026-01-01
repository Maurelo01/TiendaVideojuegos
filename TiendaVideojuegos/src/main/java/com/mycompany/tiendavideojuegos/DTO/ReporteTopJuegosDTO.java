package com.mycompany.tiendavideojuegos.DTO;

import java.io.Serializable;

public class ReporteTopJuegosDTO implements Serializable
{
    private String titulo;
    private String nombreEmpresa;
    private int totalVentas;
    private float promedioCalificacion;
    private float puntajeBalance;

    public String getTitulo()
    {
        return titulo;
    }

    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
    }

    public String getNombreEmpresa()
    {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa)
    {
        this.nombreEmpresa = nombreEmpresa;
    }

    public int getTotalVentas()
    {
        return totalVentas;
    }

    public void setTotalVentas(int totalVentas)
    {
        this.totalVentas = totalVentas;
    }

    public float getPromedioCalificacion()
    {
        return promedioCalificacion;
    }

    public void setPromedioCalificacion(float promedioCalificacion)
    {
        this.promedioCalificacion = promedioCalificacion;
    }

    public float getPuntajeBalance()
    {
        return puntajeBalance;
    }

    public void setPuntajeBalance(float puntajeBalance)
    {
        this.puntajeBalance = puntajeBalance;
    }
}
