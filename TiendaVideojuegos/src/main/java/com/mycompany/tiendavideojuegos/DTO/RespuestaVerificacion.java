package com.mycompany.tiendavideojuegos.DTO;

public class RespuestaVerificacion 
{
    private boolean yaLoTiene;
    
    public RespuestaVerificacion() 
    {
    }
    
    public RespuestaVerificacion(boolean yaLoTiene) 
    {
        this.yaLoTiene = yaLoTiene;
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