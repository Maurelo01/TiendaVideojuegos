package com.mycompany.tiendavideojuegos.DTO;

import java.io.Serializable;
import java.sql.Date;

public class HistorialComprasDTO implements Serializable 
{
    private Date fechaCompra;
    private String tituloJuego;
    private float precioPagado;

    public Date getFechaCompra()
    {
        return fechaCompra;
    }

    public void setFechaCompra(Date fechaCompra)
    {
        this.fechaCompra = fechaCompra;
    }

    public String getTituloJuego()
    {
        return tituloJuego;
    }

    public void setTituloJuego(String tituloJuego)
    {
        this.tituloJuego = tituloJuego;
    }

    public float getPrecioPagado()
    {
        return precioPagado;
    }

    public void setPrecioPagado(float precioPagado)
    {
        this.precioPagado = precioPagado;
    }
}
