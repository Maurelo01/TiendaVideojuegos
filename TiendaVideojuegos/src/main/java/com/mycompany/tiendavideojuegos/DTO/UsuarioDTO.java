package com.mycompany.tiendavideojuegos.DTO;

import java.sql.Date;
import java.sql.Timestamp;

public class UsuarioDTO 
{
    private int idUsuario;
    private String correo;
    private String contraseña;
    private String rol;
    private String nickname;
    private Date fechaNacimiento;
    private String telefono;
    private String pais;
    private float saldoCartera;
    
    
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
    
    public String getNickname() 
    {
        return nickname; 
    }
    
    public void setNickname(String nickname) 
    {
        this.nickname = nickname; 
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

    public Date getFechaNacimiento() 
    {
        return fechaNacimiento; 
    }
    
    public void setFechaNacimiento(Date fechaNacimiento) 
    {
        this.fechaNacimiento = fechaNacimiento; 
    }
    
    public String getTelefono() 
    {
        return telefono; 
    }
    
    public void setTelefono(String telefono) 
    {
        this.telefono = telefono; 
    }

    public String getPais() 
    {
        return pais; 
    }
    
    public void setPais(String pais) 
    {
        this.pais = pais;
    }

    public float getSaldoCartera() 
    {
        return saldoCartera; 
    }
    
    public void setSaldoCartera(float saldoCartera) 
    {
        this.saldoCartera = saldoCartera; 
    }
}
