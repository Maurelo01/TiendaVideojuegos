package com.mycompany.tiendavideojuegos.DTO;
public class RespuestaRecarga
{
    private String mensaje;
    private float nuevoSaldo;
    
    public RespuestaRecarga() {}
    
    public RespuestaRecarga(String mensaje, float nuevoSaldo)
    {
        this.mensaje = mensaje;
        this.nuevoSaldo = nuevoSaldo;
    }
    
    public String getMensaje()
    {
        return mensaje;
    }
    
    public void setMensaje(String mensaje)
    {
        this.mensaje = mensaje;
    }
    
    public float getNuevoSaldo()
    {
        return nuevoSaldo;
    }
    
    public void setNuevoSaldo(float nuevoSaldo)
    {
        this.nuevoSaldo = nuevoSaldo;
    }
}