package com.mycompany.tiendavideojuegos.DTO;

import java.sql.Date;

public class VideojuegosDTO 
{
    private int idJuego;
    private int idEmpresa;
    private String titulo;
    private String descripcion;
    private float precio;
    private String recursosMinimos;
    private String clasificacionEdad;
    private String estado;
    private Date fechaPublicacion;
    private boolean comentariosEstado;
    private String nombreEmpresa;
    
    public int getIdJuego() 
    {
        return idJuego;
    }
    
    public void setIdJuego(int idJuego) 
    {
        this.idJuego = idJuego;
    }

    public int getIdEmpresa() 
    {
        return idEmpresa;
    }
    
    public void setIdEmpresa(int idEmpresa) 
    {
        this.idEmpresa = idEmpresa;
    }

    public String getTitulo() 
    {
        return titulo;
    }
    
    public void setTitulo(String titulo) 
    {
        this.titulo = titulo;
    }

    public String getDescripcion() 
    {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) 
    {
        this.descripcion = descripcion;
    }

    public float getPrecio() 
    {
        return precio;
    }
    
    public void setPrecio(float precio) 
    {
        this.precio = precio;
    }

    public String getRecursosMinimos() 
    {
        return recursosMinimos;
    }
    
    public void setRecursosMinimos(String recursosMinimos) 
    {
        this.recursosMinimos = recursosMinimos;
    }

    public String getClasificacionEdad() 
    {
        return clasificacionEdad;
    }
    
    public void setClasificacionEdad(String clasificacionEdad) 
    {
        this.clasificacionEdad = clasificacionEdad;
    }

    public String getEstado() 
    {
        return estado;
    }
    
    public void setEstado(String estado) 
    {
        this.estado = estado;
    }

    public Date getFechaPublicacion() 
    {
        return fechaPublicacion;
    }
    
    public void setFechaPublicacion(Date fechaPublicacion) 
    {
        this.fechaPublicacion = fechaPublicacion;
    }

    public boolean isComentariosEstado() 
    {
        return comentariosEstado;
    }
    
    public void setComentariosEstado(boolean comentariosEstado) 
    {
        this.comentariosEstado = comentariosEstado;
    }

    public String getNombreEmpresa() 
    {
        return nombreEmpresa;
    }
    public void setNombreEmpresa(String nombreEmpresa)
    {
        this.nombreEmpresa = nombreEmpresa;
    }
}
