package com.mycompany.tiendavideojuegos.resources;

import com.mycompany.tiendavideojuegos.DTO.GrupoFamiliarDTO;
import com.mycompany.tiendavideojuegos.DTO.RespuestaError;
import com.mycompany.tiendavideojuegos.DTO.RespuestaExito;
import com.mycompany.tiendavideojuegos.DTO.SolicitudGrupoDTO;
import com.mycompany.tiendavideojuegos.services.GruposService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("grupos")
public class GruposResource 
{
    private final GruposService service = new GruposService();

    @POST // /api/grupos
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response crearGrupo(SolicitudGrupoDTO solicitud) 
    {
        try 
        {
            if (service.crearGrupo(solicitud)) 
            {
                return Response.status(Response.Status.CREATED).entity(new RespuestaExito("Grupo familiar creado exitosamente.")).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity(new RespuestaError("No se pudo crear el grupo.")).build();
        } 
        catch (Exception e) 
        {
            return Response.status(Response.Status.CONFLICT).entity(new RespuestaError(e.getMessage())).build();
        }
    }

    @GET
    @Path("usuario/{idUsuario}") // /api/grupos/usuario/{id}
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerGrupoUsuario(@PathParam("idUsuario") int idUsuario) 
    {
        try 
        {
            GrupoFamiliarDTO grupo = service.obtenerGrupoDelUsuario(idUsuario);
            if (grupo != null) 
            {
                return Response.ok(grupo).build();
            }
            return Response.status(Response.Status.NO_CONTENT).build(); 
        } 
        catch (Exception e) 
        {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new RespuestaError("Error al consultar grupo: " + e.getMessage())).build();
        }
    }
    
    @POST
    @Path("miembros/agregar") // /api/grupos/miembros/agregar
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response agregarMiembro(SolicitudGrupoDTO solicitud) 
    {
        try 
        {
            if (service.agregarMiembro(solicitud)) 
            {
                return Response.ok(new RespuestaExito("Miembro agregado exitosamente.")).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity(new RespuestaError("No se pudo agregar al miembro.")).build();
        } 
        catch (Exception e) 
        {
            return Response.status(Response.Status.CONFLICT).entity(new RespuestaError(e.getMessage())).build();
        }
    }

    @POST
    @Path("miembros/expulsar") // /api/grupos/miembros/expulsar
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response expulsarMiembro(SolicitudGrupoDTO solicitud) 
    {
        try 
        {
            if (service.expulsarMiembro(solicitud)) 
            {
                return Response.ok(new RespuestaExito("Miembro expulsado del grupo.")).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity(new RespuestaError("No se pudo expulsar al miembro.")).build();
        } 
        catch (Exception e) 
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(new RespuestaError(e.getMessage())).build();
        }
    }
    
    @DELETE
    @Path("{idGrupo}") // /api/grupos/{idGrupo}?idLider=1
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminarGrupo(@PathParam("idGrupo") int idGrupo, @QueryParam("idLider") int idLider) 
    {
        try 
        {
            if (idLider <= 0) 
            {
                return Response.status(Response.Status.BAD_REQUEST).entity(new RespuestaError("Se requiere el id del líder para autorizar la eliminación.")).build();
            }

            if (service.eliminarGrupo(idLider, idGrupo)) 
            {
                return Response.ok(new RespuestaExito("El grupo familiar ha sido disuelto permanentemente.")).build();
            }
            
            return Response.status(Response.Status.NOT_FOUND).entity(new RespuestaError("No se encontró el grupo o no se pudo eliminar.")).build();
        } 
        catch (Exception e) 
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(new RespuestaError(e.getMessage())).build();
        }
    }
    
    @DELETE
    @Path("{idGrupo}/salir") // api/grupos/{idGrupo}/salir?idUsuario=4
    @Produces(MediaType.APPLICATION_JSON)
    public Response salirDelGrupo(@PathParam("idGrupo") int idGrupo, @QueryParam("idUsuario") int idUsuario) 
    {
        try 
        {
            if (idUsuario <= 0) 
            {
                return Response.status(Response.Status.BAD_REQUEST).entity(new RespuestaError("Id de usuario inválido.")).build();
            }

            if (service.salirGrupo(idUsuario, idGrupo)) 
            {
                return Response.ok(new RespuestaExito("Has salido del grupo familiar correctamente.")).build();
            }
            
            return Response.status(Response.Status.BAD_REQUEST).entity(new RespuestaError("No se pudo salir del grupo.")).build();
        } 
        catch (Exception e) 
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(new RespuestaError(e.getMessage())).build();
        }
    }
}