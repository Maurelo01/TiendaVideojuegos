package com.mycompany.tiendavideojuegos.DTO;

import java.io.Serializable;

public class SolicitudPrestamoDTO implements Serializable 
{
    private int idUsuarioSolicitante;
    private int idJuego;
    private int idPropietario;

    public SolicitudPrestamoDTO(){}
    
    public SolicitudPrestamoDTO(int idUsuarioSolicitante, int idJuego, int idPropietario) 
    {
        this.idUsuarioSolicitante = idUsuarioSolicitante;
        this.idJuego = idJuego;
        this.idPropietario = idPropietario;
    }

    public int getIdUsuarioSolicitante()
    {
        return idUsuarioSolicitante;
    }

    public void setIdUsuarioSolicitante(int idUsuarioSolicitante)
    {
        this.idUsuarioSolicitante = idUsuarioSolicitante;
    }

    public int getIdJuego()
    {
        return idJuego;
    }

    public void setIdJuego(int idJuego)
    {
        this.idJuego = idJuego;
    }

    public int getIdPropietario()
    {
        return idPropietario;
    }

    public void setIdPropietario(int idPropietario)
    {
        this.idPropietario = idPropietario;
    }
}