package com.mycompany.tiendavideojuegos.DTO;

import java.io.Serializable;

public class ComparativaCalificacionDTO implements Serializable
{
    private String tituloJuego;
    private int miCalificacion;
    private float promedioComunidad;

    public ComparativaCalificacionDTO() {}

    public String getTituloJuego()
    {
        return tituloJuego;
    }

    public void setTituloJuego(String tituloJuego)
    {
        this.tituloJuego = tituloJuego;
    }

    public int getMiCalificacion() 
    {
        return miCalificacion;
    }

    public void setMiCalificacion(int miCalificacion)
    {
        this.miCalificacion = miCalificacion;
    }

    public float getPromedioComunidad()
    {
        return promedioComunidad;
    }

    public void setPromedioComunidad(float promedioComunidad)
    {
        this.promedioComunidad = promedioComunidad;
    }
}
