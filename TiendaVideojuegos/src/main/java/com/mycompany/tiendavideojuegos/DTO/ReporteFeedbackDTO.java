package com.mycompany.tiendavideojuegos.DTO;

import java.io.Serializable;
import java.util.List;

public class ReporteFeedbackDTO implements Serializable
{
    private List<JuegoCalificacionDTO> promedios;
    private List<ComentarioDTO> topComentarios;
    private List<ComentarioDTO> peoresComentarios;

    public List<JuegoCalificacionDTO> getPromedios()
    {
        return promedios;
    }

    public void setPromedios(List<JuegoCalificacionDTO> promedios)
    {
        this.promedios = promedios;
    }

    public List<ComentarioDTO> getTopComentarios()
    {
        return topComentarios;
    }

    public void setTopComentarios(List<ComentarioDTO> topComentarios)
    {
        this.topComentarios = topComentarios;
    }

    public List<ComentarioDTO> getPeoresComentarios()
    {
        return peoresComentarios;
    }

    public void setPeoresComentarios(List<ComentarioDTO> peoresComentarios)
    {
        this.peoresComentarios = peoresComentarios;
    }
}
