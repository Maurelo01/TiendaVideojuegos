package com.mycompany.tiendavideojuegos.resources;

import com.mycompany.tiendavideojuegos.DTO.VideojuegosDTO;
import com.mycompany.tiendavideojuegos.models.Videojuegos;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("videojuegos")
public class VideojuegosResource 
{
    private final Videojuegos modelo = new Videojuegos();

    @POST // /api/videojuegos (Publicar juego completo)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response publicarJuego(VideojuegosDTO juego) 
    {
        if (modelo.publicar(juego)) 
        {
            return Response.status(Response.Status.CREATED).entity("Exito: Juego publicado").build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("Error: Error al publicar").build();
    }
    
    @GET // /api/videojuegos/empresa/{id}
    @Path("empresa/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerPorEmpresa(@PathParam("id") int idEmpresa) 
    {
        return Response.ok(modelo.listarPorEmpresa(idEmpresa)).build();
    }
}
