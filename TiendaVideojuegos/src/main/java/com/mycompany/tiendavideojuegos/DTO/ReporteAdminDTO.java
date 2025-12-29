package com.mycompany.tiendavideojuegos.DTO;

import java.io.Serializable;

public class ReporteAdminDTO implements Serializable
{
    private String nombreEmpresa;
    private int totalVentas;
    private float totalIngresos;
    private float gananciaPlataforma;
    private float gananciaEmpresa;

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

    public float getTotalIngresos()
    {
        return totalIngresos;
    }

    public void setTotalIngresos(float totalIngresos)
    {
        this.totalIngresos = totalIngresos;
    }

    public float getGananciaPlataforma()
    {
        return gananciaPlataforma;
    }

    public void setGananciaPlataforma(float gananciaPlataforma)
    {
        this.gananciaPlataforma = gananciaPlataforma;
    }

    public float getGananciaEmpresa()
    {
        return gananciaEmpresa;
    }

    public void setGananciaEmpresa(float gananciaEmpresa)
    {
        this.gananciaEmpresa = gananciaEmpresa;
    }
}
