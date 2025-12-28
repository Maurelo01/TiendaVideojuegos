package com.mycompany.tiendavideojuegos.DTO;

import java.io.Serializable;
import java.sql.Date;

public class SolicitudCompra implements Serializable
{
    private int idUsuario;
    private int idJuego;
    private Date fechaSimulada;
    private boolean yaLoTiene;
    
    public SolicitudCompra(boolean yaLoTiene) 
    {
        this.yaLoTiene = yaLoTiene;
    }
    
    public int getIdUsuario()
    {
        return idUsuario;
    }
    
    public void setIdUsuario(int idUsuario)
    {
        this.idUsuario = idUsuario;
    }
    
    public int getIdJuego()
    {
        return idJuego;
    }
    
    public void setIdJuego(int idJuego)
    {
        this.idJuego = idJuego;
    }
    
    public Date getFechaSimulada()
    {
        return fechaSimulada;
    }
    
    public void setFechaSimulada(Date fechaSimulada)
    {
        this.fechaSimulada = fechaSimulada;
    }
    
    public boolean isYaLoTiene() 
    {
        return yaLoTiene;
    }
    
    public void setYaLoTiene(boolean yaLoTiene) 
    {
        this.yaLoTiene = yaLoTiene;
    }
}
