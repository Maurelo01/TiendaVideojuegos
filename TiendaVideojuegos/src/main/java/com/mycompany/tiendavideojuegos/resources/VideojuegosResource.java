package com.mycompany.tiendavideojuegos.resources;

import com.mycompany.tiendavideojuegos.DTO.MultimediaDTO;
import com.mycompany.tiendavideojuegos.DTO.RespuestaError;
import com.mycompany.tiendavideojuegos.DTO.VideojuegosDTO;
import com.mycompany.tiendavideojuegos.services.VideojuegosService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
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
    
    @PUT
    @Path("{id}/suspender") // /api/videojuegos/{id}/suspender
    @Produces(MediaType.APPLICATION_JSON)
    public Response suspenderJuego(@PathParam("id") int id) 
    {
        try 
        {
            if (service.suspenderJuego(id)) 
            {
                return Response.ok("Exito: La venta del juego ha sido suspendida y ya no aparecrá en el catálogo.").build();
            }
            return Response.status(Response.Status.NOT_FOUND).entity("Error: No se encontró el juego o no se pudo realizar la acción.").build();
        } 
        catch (Exception e) 
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: " + e.getMessage()).build();
        }
    }
    
    @PUT
    @Path("{id}/activar") // /api/videojuegos/{id}/activar
    @Produces(MediaType.APPLICATION_JSON)
    public Response activarJuego(@PathParam("id") int id) 
    {
        try 
        {
            if (service.activarJuego(id)) 
            {
                return Response.ok("Exito: El juego ha sido reactivado y está disponible en la tienda.").build();
            }
            return Response.status(Response.Status.NOT_FOUND).entity("Error: No se encontró el juego o no se pudo activar.").build();
        } 
        catch (Exception e) 
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: " + e.getMessage()).build();
        }
    }
    
    @POST
    @Path("{id}/multimedia") // /api/videojuegos/{id}/multimedia
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response agregarMultimedia(@PathParam("id") int id, List<MultimediaDTO> lista)
    {
        try 
        {
            if (service.agregarMultimedia(id, lista))
            {
                return Response.ok("Multimedia agregada").build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity("Error al guardar").build();
        }
        catch (Exception e)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    
    @GET
    @Path("{id}/multimedia") // /api/videojuegos/{id}/multimedia
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerMultimedia(@PathParam("id") int id)
    {
        try
        {
            List<MultimediaDTO> lista = service.listarMultimedia(id);
            if (lista.isEmpty())
            {
                return Response.ok(lista).build(); 
            }
            return Response.ok(lista).build();
        }
        catch (Exception e)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: " + e.getMessage()).build();
        }
    }
    
    @DELETE
    @Path("multimedia/{id}") // /api/videojuegos/multimedia/{id}
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminarMultimedia(@PathParam("id") int idMedia) 
    {
        try 
        {
            if (service.eliminarMultimedia(idMedia)) 
            {
                return Response.ok("Multimedia eliminada correctamente").build();
            }
            return Response.status(Response.Status.NOT_FOUND).entity("Error: No se encontró el archivo multimedia.").build();
        } 
        catch (Exception e) 
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: " + e.getMessage()).build();
        }
    }
    
    @GET
    @Path("buscar") // /api/videojuegos/buscar
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarJuegos(@QueryParam("titulo") String titulo, @QueryParam("idCategoria") Integer idCategoria) 
    {
        try 
        {
            List<VideojuegosDTO> resultados = service.buscar(titulo, idCategoria);
            return Response.ok(resultados).build();
        } 
        catch (Exception e) 
        {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al buscar: " + e.getMessage()).build();
        }
    }
    
    @GET
    @Path("{id}") // /api/videojuegos/{id}
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerPorId(@PathParam("id") int id)
    {
        try
        {
            return Response.ok(service.obtenerJuegoPorId(id)).build();
        }
        catch (Exception e)
        {
            return Response.status(Response.Status.NOT_FOUND).entity(new RespuestaError(e.getMessage())).build();
        }
    }
}
