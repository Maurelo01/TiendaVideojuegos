package com.mycompany.tiendavideojuegos.DTO;

import java.io.Serializable;
import java.sql.Date;

public class BibliotecaDTO implements Serializable {
    private int idJuego;
    private String titulo;
    private String imagen;
    private String estadoInstalacion;
    private Date fechaAdquisicion;

    public int getIdJuego()
    {
        return idJuego;
    }
    
    public void setIdJuego(int idJuego)
    {
        this.idJuego = idJuego;
    }

    public String getTitulo()
    {
        return titulo;
    }
    
    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
    }

    public String getImagen()
    {
        return imagen;
    }
    
    public void setImagen(String imagen)
    {
        this.imagen = imagen;
    }

    public String getEstadoInstalacion()
    {
        return estadoInstalacion;
    }
    
    public void setEstadoInstalacion(String estadoInstalacion)
    {
        this.estadoInstalacion = estadoInstalacion;
    }

    public Date getFechaAdquisicion()
    {
        return fechaAdquisicion;
    }
    
    public void setFechaAdquisicion(Date fechaAdquisicion)
    {
        this.fechaAdquisicion = fechaAdquisicion;
    }
}