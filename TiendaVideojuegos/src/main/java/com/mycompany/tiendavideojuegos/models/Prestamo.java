package com.mycompany.tiendavideojuegos.models;

import java.sql.Timestamp;

public class Prestamo 
{
    private int idPrestamo;
    private int idUsuarioRecibe;
    private int idJuego;
    private int idUsuarioPropietario;
    private String estadoInstalacion;
    private Timestamp fechaPrestamo;
    private Timestamp fechaDevolucion;

    public int getIdPrestamo()
    {
        return idPrestamo;
    }

    public void setIdPrestamo(int idPrestamo)
    {
        this.idPrestamo = idPrestamo;
    }

    public int getIdUsuarioRecibe()
    {
        return idUsuarioRecibe;
    }

    public void setIdUsuarioRecibe(int idUsuarioRecibe)
    {
        this.idUsuarioRecibe = idUsuarioRecibe;
    }

    public int getIdJuego()
    {
        return idJuego;
    }

    public void setIdJuego(int idJuego)
    {
        this.idJuego = idJuego;
    }

    public int getIdUsuarioPropietario()
    {
        return idUsuarioPropietario;
    }

    public void setIdUsuarioPropietario(int idUsuarioPropietario) {
        this.idUsuarioPropietario = idUsuarioPropietario;
    }

    public String getEstadoInstalacion()
    {
        return estadoInstalacion;
    }

    public void setEstadoInstalacion(String estadoInstalacion)
    {
        this.estadoInstalacion = estadoInstalacion;
    }

    public Timestamp getFechaPrestamo()
    {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(Timestamp fechaPrestamo)
    {
        this.fechaPrestamo = fechaPrestamo;
    }

    public Timestamp getFechaDevolucion()
    {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(Timestamp fechaDevolucion)
    {
        this.fechaDevolucion = fechaDevolucion;
    }
}
