package com.mycompany.tiendavideojuegos.services;

import com.mycompany.tiendavideojuegos.DTO.ComentarioDTO;
import com.mycompany.tiendavideojuegos.DTO.ReporteFeedbackDTO;
import com.mycompany.tiendavideojuegos.models.Comentario;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComentariosService 
{
    private final Comentario modelo = new Comentario();

    public boolean publicarComentario(ComentarioDTO comentario) throws Exception
    {
        if (comentario.getTexto() == null || comentario.getTexto().trim().isEmpty())
        {
            throw new Exception("El comentario no puede estar vacío.");
        }
        if (comentario.getIdComentarioPrincipal() <= 0) 
        {
            if (comentario.getCalificacion() < 1 || comentario.getCalificacion() > 5)
            {
                throw new Exception("La calificación debe estar entre 1 y 5 estrellas.");
            }
        }
        return modelo.agregar(comentario);
    }

    public List<ComentarioDTO> listarComentarios(int idJuego)
    {
        List<ComentarioDTO> todos = modelo.listarPorJuego(idJuego);
        List<ComentarioDTO> raiz = new ArrayList<>();
        Map<Integer, ComentarioDTO> mapa = new HashMap<>();
        for (ComentarioDTO c : todos) 
        {
            mapa.put(c.getIdComentario(), c);
        }
        for (ComentarioDTO c : todos)
        {
            if (c.getIdComentarioPrincipal() > 0) 
            {
                ComentarioDTO padre = mapa.get(c.getIdComentarioPrincipal());
                if (padre != null) 
                {
                    padre.getRespuestas().add(c);
                }
            }
            else
            {
                raiz.add(c);
            }
        }
        return raiz;
    }

    public boolean editarComentario(ComentarioDTO comentario) throws Exception
    {
        if (comentario.getIdComentario() <= 0) throw new Exception("Id de comentario inválido.");
        if (comentario.getTexto() == null || comentario.getTexto().trim().isEmpty())
        {
            throw new Exception("El texto no puede estar vacío.");
        }
        boolean actualizado = modelo.actualizar(comentario);
        if (!actualizado)
        {
            throw new Exception("No se pudo editar. Verifique que el comentario exista y le pertenezca.");
        }
        return true;
    }

    public boolean eliminarComentario(int idComentario, int idUsuario) throws Exception 
    {
        if (idComentario <= 0) throw new Exception("ID inválido.");
        boolean eliminado = modelo.eliminar(idComentario, idUsuario);
        if (!eliminado)
        {
            throw new Exception("No se pudo eliminar. Verifique permisos.");
        }
        return true;
    }
    
    public boolean cambiarEstadoOculto(int idComentario, int idUsuario, boolean ocultar) throws Exception
    {
         if (!modelo.esModeradorDelComentario(idComentario, idUsuario))
        {
            throw new Exception("No tienes permisos para moderar este comentario.");
        }
        return modelo.cambiarVisibilidad(idComentario, ocultar);
    }
    
    public ReporteFeedbackDTO generarReporteFeedback(int idEmpresa) throws Exception
    {
        if (idEmpresa <= 0) throw new Exception("Id empresa inválido");
        ReporteFeedbackDTO reporte = new ReporteFeedbackDTO();
        reporte.setPromedios(modelo.obtenerPromediosPorEmpresa(idEmpresa));
        reporte.setTopComentarios(modelo.obtenerTopComentados(idEmpresa));
        reporte.setPeoresComentarios(modelo.obtenerPeoresComentarios(idEmpresa));
        return reporte;
    }
}