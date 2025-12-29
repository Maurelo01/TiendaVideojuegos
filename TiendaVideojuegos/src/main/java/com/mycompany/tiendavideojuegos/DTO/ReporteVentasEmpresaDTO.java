package com.mycompany.tiendavideojuegos.DTO;

import java.io.Serializable;

public class ReporteVentasEmpresaDTO implements Serializable 
{
    private String tituloJuego;
    private int copiasVendidas;
    private float ingresosBrutos;
    private float comisionPlataforma;
    private float gananciaNeta;

    public String getTituloJuego()
    {
        return tituloJuego;
    }

    public void setTituloJuego(String tituloJuego)
    {
        this.tituloJuego = tituloJuego;
    }

    public int getCopiasVendidas()
    {
        return copiasVendidas;
    }

    public void setCopiasVendidas(int copiasVendidas)
    {
        this.copiasVendidas = copiasVendidas;
    }

    public float getIngresosBrutos()
    {
        return ingresosBrutos;
    }

    public void setIngresosBrutos(float ingresosBrutos)
    {
        this.ingresosBrutos = ingresosBrutos;
    }

    public float getComisionPlataforma()
    {
        return comisionPlataforma;
    }

    public void setComisionPlataforma(float comisionPlataforma)
    {
        this.comisionPlataforma = comisionPlataforma;
    }

    public float getGananciaNeta()
    {
        return gananciaNeta;
    }

    public void setGananciaNeta(float gananciaNeta)
    {
        this.gananciaNeta = gananciaNeta;
    }
}
