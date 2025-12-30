package com.mycompany.tiendavideojuegos.models;

import java.sql.Date;

public class GrupoFamiliar
{
    private int idGrupo;
    private int idLider;
    private String nombreGrupo;
    private Date fechaCreacion;

    public GrupoFamiliar(int idGrupo, int idLider, String nombreGrupo, Date fechaCreacion) {
        this.idGrupo = idGrupo;
        this.idLider = idLider;
        this.nombreGrupo = nombreGrupo;
        this.fechaCreacion = fechaCreacion;
    }

    public int getIdGrupo()
    {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo)
    {
        this.idGrupo = idGrupo;
    }

    public int getIdLider()
    {
        return idLider;
    }

    public void setIdLider(int idLider)
    {
        this.idLider = idLider;
    }

    public String getNombreGrupo()
    {
        return nombreGrupo;
    }

    public void setNombreGrupo(String nombreGrupo)
    {
        this.nombreGrupo = nombreGrupo;
    }

    public Date getFechaCreacion()
    {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion)
    {
        this.fechaCreacion = fechaCreacion;
    }
}
