package com.mycompany.tiendavideojuegos.services;

import com.mycompany.tiendavideojuegos.DTO.ComentarioDTO;
import com.mycompany.tiendavideojuegos.models.Comentario;
import java.util.List;

public class ComentariosService 
{
    private final Comentario modelo = new Comentario();

    public boolean publicarComentario(ComentarioDTO comentario) throws Exception
    {
        if (comentario.getTexto() == null || comentario.getTexto().trim().isEmpty())
        {
            throw new Exception("El comentario no puede estar vacío.");
        }
        if (comentario.getCalificacion() < 1 || comentario.getCalificacion() > 5)
        {
            throw new Exception("La calificación debe estar entre 1 y 5 estrellas.");
        }
        return modelo.agregar(comentario);
    }

    public List<ComentarioDTO> listarComentarios(int idJuego)
    {
        return modelo.listarPorJuego(idJuego);
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
}