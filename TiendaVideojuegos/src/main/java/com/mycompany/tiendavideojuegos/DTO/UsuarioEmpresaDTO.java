package com.mycompany.tiendavideojuegos.DTO;

import java.sql.Date;

public class UsuarioEmpresaDTO extends UsuarioDTO
{
    private int idEmpresa;
    private Date fechaNacimientoEmpleado;
    private String nombreEmpleado;
    private String nombreEmpresa;
    
    public int getIdEmpresa()
    {
        return idEmpresa;
    }
    
    public void setIdEmpresa(int idEmpresa)
    {
        this.idEmpresa = idEmpresa;
    }
    
    public String getNombreEmpleado()
    {
        return nombreEmpleado;
    }
    
    public void setNombreEmpleado(String nombreEmpleado)
    {
        this.nombreEmpleado = nombreEmpleado;
    }
    
    public Date getFechaNacimiento()
    {
        return fechaNacimientoEmpleado;
    }
    
    public void setFechaNacimiento(Date fechaNacimiento)
    {
        this.fechaNacimientoEmpleado = fechaNacimientoEmpleado;
    }
    
    public String getNombreEmpresaAux()
    {
        return nombreEmpresa;
    }
    public void setNombreEmpresaAux(String nombreEmpresa)
    {
        this.nombreEmpresa = nombreEmpresa;
    }
}
