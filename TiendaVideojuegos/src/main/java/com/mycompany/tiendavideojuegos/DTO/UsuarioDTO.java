package com.mycompany.tiendavideojuegos.DTO;

import java.sql.Timestamp;

public class UsuarioDTO 
{
    int idUsuario;
    String correo;
    String contraseña;
    String rol;
    Timestamp fechaRegistro;
    
    public int getIdUsuario() 
    {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) 
    {
        this.idUsuario = idUsuario;
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

    public void setcontraseña(String contraseña) 
    {
        this.contraseña = contraseña;
    }

    public String getRol() 
    {
        return rol;
    }

    public void setRol(String rol) 
    {
        this.rol = rol;
    }

    public Timestamp getFechaRegistro() 
    {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) 
    {
        this.fechaRegistro = fechaRegistro;
    }
}
