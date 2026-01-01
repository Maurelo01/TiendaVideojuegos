package com.mycompany.tiendavideojuegos.DTO;

import java.io.Serializable;

public class ReporteUsoFamiliarDTO implements Serializable 
{
    private String tituloJuego;
    private String nombrePropietario;
    private int vecesInstalado;
    private float promedioComunidad;

    public String getTituloJuego()
    {
        return tituloJuego;
    }

    public void setTituloJuego(String tituloJuego)
    {
        this.tituloJuego = tituloJuego;
    }

    public String getNombrePropietario()
    {
        return nombrePropietario;
    }

    public void setNombrePropietario(String nombrePropietario)
    {
        this.nombrePropietario = nombrePropietario;
    }

    public int getVecesInstalado()
    {
        return vecesInstalado;
    }

    public void setVecesInstalado(int vecesInstalado)
    {
        this.vecesInstalado = vecesInstalado;
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
