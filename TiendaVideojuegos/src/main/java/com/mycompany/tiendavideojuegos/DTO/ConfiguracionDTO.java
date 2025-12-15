package com.mycompany.tiendavideojuegos.DTO;
public class ConfiguracionDTO
{
    private int idConfiguracion;
    private float comisionGlobalActual;
    
    public int getIdConfiguracion()
    {
        return idConfiguracion;
    }

    public void setIdConfiguracion(int idConfiguracion)
    {
        this.idConfiguracion = idConfiguracion;
    }

    public float getComisionGlobalActual()
    {
        return comisionGlobalActual;
    }

    public void setComisionGlobalActual(float comisionGlobalActual)
    {
        this.comisionGlobalActual = comisionGlobalActual;
    }
}
