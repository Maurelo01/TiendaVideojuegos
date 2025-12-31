package com.mycompany.tiendavideojuegos.DTO;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ComentarioDTO implements Serializable
{
    private int idComentario;
    private int idGamer;
    private int idJuego;
    private int idComentarioPrincipal;
    private String nicknameGamer;
    private String texto;
    private int calificacion;
    private Timestamp fecha;
    private List<ComentarioDTO> respuestas = new ArrayList<>();

    public int getIdComentario()
    {
        return idComentario;
    }

    public void setIdComentario(int idComentario)
    {
        this.idComentario = idComentario;
    }

    public int getIdGamer() 
    {
        return idGamer;
    }

    public void setIdGamer(int idGamer)
    {
        this.idGamer = idGamer;
    }

    public int getIdJuego()
    {
        return idJuego;
    }

    public void setIdJuego(int idJuego)
    {
        this.idJuego = idJuego;
    }

    public String getNicknameGamer()
    {
        return nicknameGamer;
    }

    public void setNicknameGamer(String nicknameGamer)
    {
        this.nicknameGamer = nicknameGamer;
    }

    public String getTexto()
    {
        return texto;
    }

    public void setTexto(String texto)
    {
        this.texto = texto;
    }

    public int getCalificacion()
    {
        return calificacion;
    }

    public void setCalificacion(int calificacion)
    {
        this.calificacion = calificacion;
    }

    public Timestamp getFecha()
    {
        return fecha;
    }

    public void setFecha(Timestamp fecha)
    {
        this.fecha = fecha;
    }
    
    public int getIdComentarioPrincipal()
    {
        return idComentarioPrincipal;
    }

    public void setIdComentarioPrincipal(int idComentarioPrincipal)
    {
        this.idComentarioPrincipal = idComentarioPrincipal;
    }

    public List<ComentarioDTO> getRespuestas()
    {
        return respuestas;
    }

    public void setRespuestas(List<ComentarioDTO> respuestas)
    {
        this.respuestas = respuestas;
    }
}
