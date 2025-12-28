package com.mycompany.tiendavideojuegos.DTO;
public class RespuestaExito 
{
    private String mensaje;
    
    public RespuestaExito(String mensaje) 
    {
        this.mensaje = mensaje;
    }
    
    public String getMensaje() 
    {
        return mensaje;
    }
    
    public void setMensaje(String mensaje) 
    {
        this.mensaje = mensaje;
    }
}