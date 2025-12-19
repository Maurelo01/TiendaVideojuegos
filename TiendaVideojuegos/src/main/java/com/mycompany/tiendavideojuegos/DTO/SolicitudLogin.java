package com.mycompany.tiendavideojuegos.DTO;

import java.io.Serializable;

public class SolicitudLogin implements Serializable
{
    private String correo;
    private String contraseña;

    public SolicitudLogin() 
    {
    }

    public SolicitudLogin(String correo, String contraseña) 
    {
        this.correo = correo;
        this.contraseña = contraseña;
    }

    public String getCorreo() 
    {
        return correo;
    }

    public void setCorreo(String correo) 
    {
        this.correo = correo;
    }

    public String getContraseña() 
    {
        return contraseña;
    }

    public void setContraseña(String contraseña) 
    {
        this.contraseña = contraseña;
    }
}
