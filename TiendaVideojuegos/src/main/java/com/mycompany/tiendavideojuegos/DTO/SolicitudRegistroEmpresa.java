package com.mycompany.tiendavideojuegos.DTO;

import java.io.Serializable;

public class SolicitudRegistroEmpresa implements Serializable
{
    private EmpresaDTO empresa;
    private UsuarioEmpresaDTO usuario; // admin de la empresa

    public SolicitudRegistroEmpresa() 
    {
    }

    public EmpresaDTO getEmpresa() 
    {
        return empresa;
    }

    public void setEmpresa(EmpresaDTO empresa) 
    {
        this.empresa = empresa;
    }

    public UsuarioEmpresaDTO getUsuario() 
    {
        return usuario;
    }

    public void setUsuario(UsuarioEmpresaDTO usuario) 
    {
        this.usuario = usuario;
    }
}
