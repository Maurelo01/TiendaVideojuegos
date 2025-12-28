package com.mycompany.tiendavideojuegos.DTO;
public class RespuestaError
{
    private String error;
    
    public RespuestaError() {}
    
    public RespuestaError(String error)
    {
        this.error = error;
    }
    
    public String getError()
    {
        return error;
    }
    
    public void setError(String error)
    {
        this.error = error;
    }
}
