package com.mycompany.tiendavideojuegos.resources;

import com.mycompany.tiendavideojuegos.DTO.ComentarioDTO;
import com.mycompany.tiendavideojuegos.DTO.RespuestaError;
import com.mycompany.tiendavideojuegos.DTO.RespuestaExito;
import com.mycompany.tiendavideojuegos.services.ComentariosService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("comentarios")
public class ComentariosResource 
{
    private final ComentariosService service = new ComentariosService();

    @GET
    @Path("juego/{idJuego}") // api/comentarios/juego/{idJuego}
    @Produces(MediaType.APPLICATION_JSON)
    public Response listar(@PathParam("idJuego") int idJuego) 
    {
        return Response.ok(service.listarComentarios(idJuego)).build();
    }

     
    @POST // api/comentarios
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response publicar(ComentarioDTO comentario)
    {
        try
        {
            if (service.publicarComentario(comentario))
            {
                return Response.status(Response.Status.CREATED).entity(new RespuestaExito("Comentario publicado.")).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity(new RespuestaError("No se pudo publicar.")).build();
        }
        catch (Exception e)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(new RespuestaError(e.getMessage())).build();
        }
    }

    @PUT
    @Path("{id}") // api/comentarios/{id}
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editar(@PathParam("id") int id, ComentarioDTO comentario)
    {
        try
        {
            comentario.setIdComentario(id);
            if (service.editarComentario(comentario))
            {
                return Response.ok(new RespuestaExito("Comentario actualizado.")).build();
            }
            return Response.status(Response.Status.NOT_FOUND).entity(new RespuestaError("No se encontró el comentario o no tienes permisos.")).build();
        }
        catch (Exception e)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(new RespuestaError(e.getMessage())).build();
        }
    }

    @DELETE
    @Path("{id}/usuario/{idUsuario}") // api/comentarios/{id}/usuario/{idUsuario}
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminar(@PathParam("id") int id, @PathParam("idUsuario") int idUsuario)
    {
        try
        {
            if (service.eliminarComentario(id, idUsuario))
            {
                return Response.ok(new RespuestaExito("Comentario eliminado.")).build();
            }
            return Response.status(Response.Status.NOT_FOUND).entity(new RespuestaError("No se pudo eliminar.")).build();
        }
        catch (Exception e)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(new RespuestaError(e.getMessage())).build();
        }
    }
    
    @PUT
    @Path("{id}/moderacion") // api/comentarios/{id}/moderacion?idUsuario=1&ocultar=true
    @Produces(MediaType.APPLICATION_JSON)
    public Response moderarComentario(@PathParam("id") int idComentario, @QueryParam("idUsuario") int idUsuario, @QueryParam("ocultar") boolean ocultar) 
    {
        try 
        {
            service.cambiarEstadoOculto(idComentario, idUsuario, ocultar);
            String estado = ocultar ? "ocultado" : "visible";
            return Response.ok(new RespuestaExito("Comentario ahora está " + estado + ".")).build();
        } 
        catch (Exception e) 
        {
            return Response.status(Response.Status.FORBIDDEN).entity(new RespuestaError(e.getMessage())).build();
        }
    }
    
    @GET
    @Path("reporte/feedback/{idEmpresa}") // api/comentarios/reporte/feedback/{id}
    @Produces(MediaType.APPLICATION_JSON)
    public Response reporteFeedback(@PathParam("idEmpresa") int idEmpresa) 
    {
        try 
        {
            return Response.ok(service.generarReporteFeedback(idEmpresa)).build();
        } 
        catch (Exception e) 
        {
            return Response.serverError().build();
        }
    }
}