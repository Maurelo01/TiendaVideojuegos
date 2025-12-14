package com.mycompany.tiendavideojuegos.DTO;

import java.sql.Date;

public class UsuarioComunGamerDTO extends UsuarioDTO
{
    private String nickname;
    private Date fechaNacimiento;
    private String telefono;
    private String pais;
    private float saldoCartera;
    
    public String getNickname() 
    {
        return nickname; 
    }
    
    public void setNickname(String nickname) 
    {
        this.nickname = nickname; 
    }
    
    public float getSaldoCartera() 
    {
        return saldoCartera; 
    }
    
    public void setSaldoCartera(float saldoCartera) 
    {
        this.saldoCartera = saldoCartera; 
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
}
