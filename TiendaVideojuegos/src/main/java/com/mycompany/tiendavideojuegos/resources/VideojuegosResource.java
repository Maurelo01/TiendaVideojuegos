package com.mycompany.tiendavideojuegos.resources;

import com.mycompany.tiendavideojuegos.DTO.VideojuegosDTO;
import com.mycompany.tiendavideojuegos.services.VideojuegosService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("videojuegos")
public class VideojuegosResource 
{
    private final VideojuegosService service = new VideojuegosService();
    
    @POST // /api/videojuegos (Publicar juego completo)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response publicarJuego(VideojuegosDTO juego) 
    {
        try
        {
            if (service.publicarJuego(juego)) 
            {
                return Response.status(Response.Status.CREATED).entity("Exito: Juego publicado").build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Error al publicar").build();
        }
        catch (Exception e) 
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error de validación: " + e.getMessage()).build();
        }
    }
    
    @GET // /api/videojuegos/empresa/{id}
    @Path("empresa/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerPorEmpresa(@PathParam("id") int idEmpresa) 
    {
        try 
        {
            return Response.ok(service.obtenerJuegosPorEmpresa(idEmpresa)).build();
        } 
        catch (Exception e) 
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: " + e.getMessage()).build();
        }
    }
    
    @GET // /api/videojuegos
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarCatalogo() 
    {
        try 
        {
            List<VideojuegosDTO> juegos = service.obtenerJuegosPublicos();
            return Response.ok(juegos).build();
        } 
        catch (Exception e) 
        {
            return Response.serverError().entity("Error al cargar catálogo").build();
        }
    }
    
    @PUT
    @Path("{id}") // /api/videojuegos/{id}
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editarJuego(@PathParam("id") int id, VideojuegosDTO juego) 
    {
        try 
        {
            juego.setIdJuego(id);
            if (service.editarJuego(juego)) 
            {
                return Response.ok("Exito: Juego actualizado exitosamente").build();
            }
            return Response.status(Response.Status.NOT_FOUND).entity("Error: No se encontró el juego o no se pudo actualizar.").build();
        } 
        catch (Exception e) 
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error de validación: " + e.getMessage()).build();
        }
    }
}
