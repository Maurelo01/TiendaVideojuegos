package com.mycompany.tiendavideojuegos.DTO;

import java.io.Serializable;
import java.util.List;

public class GrupoFamiliarDTO implements Serializable
{
    private int idGrupo;
    private String nombreGrupo;
    private int idLider;
    private String nombreLider;
    private int totalMiembros;
    private List<UsuarioComunGamerDTO> miembros;

    public int getIdGrupo()
    {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo)
    {
        this.idGrupo = idGrupo;
    }

    public String getNombreGrupo()
    {
        return nombreGrupo;
    }

    public void setNombreGrupo(String nombreGrupo)
    {
        this.nombreGrupo = nombreGrupo;
    }

    public int getIdLider()
    {
        return idLider;
    }

    public void setIdLider(int idLider)
    {
        this.idLider = idLider;
    }

    public String getNombreLider()
    {
        return nombreLider;
    }

    public void setNombreLider(String nombreLider)
    {
        this.nombreLider = nombreLider;
    }

    public int getTotalMiembros()
    {
        return totalMiembros;
    }

    public void setTotalMiembros(int totalMiembros)
    {
        this.totalMiembros = totalMiembros;
    }

    public List<UsuarioComunGamerDTO> getMiembros()
    {
        return miembros;
    }

    public void setMiembros(List<UsuarioComunGamerDTO> miembros)
    {
        this.miembros = miembros;
    }
}
