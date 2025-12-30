package com.mycompany.tiendavideojuegos.DTO;

import java.io.Serializable;

public class SolicitudGrupoDTO implements Serializable {

    private int idLider;
    private String nombreGrupo;
    private int idGrupo;
    private int idUsuarioMiembro;
    private String nicknameMiembro;

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

    public int getIdGrupo()
    {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo)
    {
        this.idGrupo = idGrupo;
    }

    public int getIdUsuarioMiembro()
    {
        return idUsuarioMiembro;
    }

    public void setIdUsuarioMiembro(int idUsuarioMiembro)
    {
        this.idUsuarioMiembro = idUsuarioMiembro;
    }

    public String getNicknameMiembro()
    {
        return nicknameMiembro;
    }

    public void setNicknameMiembro(String nicknameMiembro)
    {
        this.nicknameMiembro = nicknameMiembro;
    }
}
