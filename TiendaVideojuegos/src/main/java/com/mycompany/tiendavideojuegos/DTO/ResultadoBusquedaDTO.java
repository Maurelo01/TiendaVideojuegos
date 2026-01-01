package com.mycompany.tiendavideojuegos.DTO;

import java.io.Serializable;

public class ResultadoBusquedaDTO implements Serializable 
{
    private int id;
    private String nombre;
    private String imagen;
    private String tipo;
    
    public ResultadoBusquedaDTO(){}

    public ResultadoBusquedaDTO(int id, String nombre, String imagen, String tipo)
    {
        this.id = id;
        this.nombre = nombre;
        this.imagen = imagen;
        this.tipo = tipo;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public String getImagen()
    {
        return imagen;
    }

    public void setImagen(String imagen)
    {
        this.imagen = imagen;
    }

    public String getTipo()
    {
        return tipo;
    }

    public void setTipo(String tipo)
    {
        this.tipo = tipo;
    }
}
