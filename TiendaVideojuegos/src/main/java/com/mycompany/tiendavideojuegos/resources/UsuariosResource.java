package com.mycompany.tiendavideojuegos.resources;

import com.mycompany.tiendavideojuegos.DTO.RespuestaError;
import com.mycompany.tiendavideojuegos.DTO.RespuestaExito;
import com.mycompany.tiendavideojuegos.DTO.RespuestaRecarga;
import com.mycompany.tiendavideojuegos.DTO.SolicitudLogin;
import com.mycompany.tiendavideojuegos.DTO.SolicitudRegistroEmpresa;
import com.mycompany.tiendavideojuegos.DTO.SolicitudSaldo;
import com.mycompany.tiendavideojuegos.DTO.UsuarioComunGamerDTO;
import com.mycompany.tiendavideojuegos.DTO.UsuarioDTO;
import com.mycompany.tiendavideojuegos.DTO.UsuarioEmpresaDTO;
import com.mycompany.tiendavideojuegos.services.UsuariosService;
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

@Path("usuarios")
public class UsuariosResource 
{
    private final UsuariosService service = new UsuariosService();
    
    @POST // /api/usuarios/login
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(SolicitudLogin credenciales)
    {
        try 
        {
            UsuarioDTO usuario = service.login(credenciales.getCorreo(), credenciales.getContraseña());
            if (usuario != null) 
            {
                return Response.ok(usuario).build();
            }
            return Response.status(Response.Status.UNAUTHORIZED).entity("Error: Datos inválidos").build();
        } 
        catch (Exception e) 
        {
            return Response.serverError().entity("Error: " + e.getMessage()).build();
        }
    }
    
    @POST // /api/usuarios/registro/gamer
    @Path("registro/gamer") 
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarGamer(UsuarioComunGamerDTO gamer) 
    {
        try
        {
            var resultado = service.registrarGamer(gamer);
            if (resultado != null) 
            {
                return Response.status(Response.Status.CREATED).entity(resultado).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: No se pudo registrar el Usuario gamer").build();
        }
        catch (Exception e) 
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error de validación: " + e.getMessage()).build();
        }
    }
    
    @POST // /api/usuarios/registro/empresa
    @Path("registro/empresa")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarEmpresa(SolicitudRegistroEmpresa request) 
    {
        try 
        {
            if (request == null || request.getEmpresa() == null || request.getUsuario() == null) 
            {
                return Response.status(Response.Status.BAD_REQUEST).entity("Datos incompletos").build();
            }
            boolean exito = service.registrarEmpresa(request.getEmpresa(), request.getUsuario());
            if (exito) 
            {
                return Response.status(Response.Status.CREATED).entity("Exito: Empresa creada").build();
            }
            return Response.serverError().entity("Error al crear empresa").build();
        }
        catch (Exception e) 
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error validación: " + e.getMessage()).build();
        }
    }
    
    @POST
    @Path("empresa/{id}/empleado") // /api/usuarios/empresa/{id}/empleado
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response agregarEmpleado(@PathParam("id") int idEmpresa, UsuarioEmpresaDTO empleado)
    {
        try 
        {
            boolean exito = service.agregarUsuarioAEmpresa(idEmpresa, empleado);
            if (exito)
            {
                return Response.status(Response.Status.CREATED).entity("Exito: Empleado agregado a la empresa " + idEmpresa).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: No se pudo agregar el empleado. Verifique que la empresa exista.").build();

        }
        catch (Exception e)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error de validación: " + e.getMessage()).build();
        }
    }
    
    @GET
    @Path("empresa/{id}/empleados") // /api/usuarios/empresa/{id}/empleados
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarEmpleados(@PathParam("id") int idEmpresa) 
    {
        try 
        {
            return Response.ok(service.listarEmpleados(idEmpresa)).build();
        } 
        catch (Exception e) 
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("empresa/{idEmpresa}/empleado/{idEmpleado}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminarEmpleado(@PathParam("idEmpresa") int idEmpresa, @PathParam("idEmpleado") int idEmpleado)
    {
        try 
        {
            if (service.eliminarEmpleado(idEmpresa, idEmpleado))
            {
                return Response.ok("Exito: Empleado eliminado correctamente.").build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: No se pudo eliminar.").build();
        }
        catch (Exception e)
        {
            if (e.getMessage().contains("último empleado")) 
            {
                return Response.status(Response.Status.CONFLICT).entity("Error: " + e.getMessage()).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: " + e.getMessage()).build();
        }
    }
    
    @GET
    @Path("gamer/{id}") // /api/usuarios/gamer/{id}
    @Produces(MediaType.APPLICATION_JSON)
    public Response verPerfilGamer(@PathParam("id") int id) 
    {
        try 
        {
            return Response.ok(service.obtenerPerfilGamer(id)).build();
        } 
        catch (Exception e) 
        {
            return Response.status(Response.Status.NOT_FOUND).entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("gamer/{id}") // /api/usuarios/gamer/{id}
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editarPerfilGamer(@PathParam("id") int id, UsuarioComunGamerDTO gamer) 
    {
        try 
        {
            if (service.actualizarPerfilGamer(id, gamer)) 
            {
                return Response.ok("Exito: Perfil actualizado correctamente").build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: No se pudo actualizar").build();
        } 
        catch (Exception e) 
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("empresa/publica/{id}") // /api/usuarios/empresa/publica/{id}
    @Produces(MediaType.APPLICATION_JSON)
    public Response verPerfilEmpresa(@PathParam("id") int id) 
    {
        try 
        {
            return Response.ok(service.obtenerPerfilPublicoEmpresa(id)).build();
        } 
        catch (Exception e) 
        {
            return Response.status(Response.Status.NOT_FOUND).entity("Error: " + e.getMessage()).build();
        }
    }
    
    @POST
    @Path("gamer/{id}/recargar") // /api/usuarios/empresa/publica/{id}
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response recargarCartera(@PathParam("id") int idUsuario, SolicitudSaldo solicitud) 
    {
        try 
        {
            if (solicitud == null) 
            {
                return Response.status(Response.Status.BAD_REQUEST).entity(new RespuestaError("Error: La solicitud no puede estar vacía")).build();
            }

            float nuevoSaldo = service.recargarSaldo(idUsuario, solicitud.getMonto());
            RespuestaRecarga respuesta = new RespuestaRecarga("Recarga exitosa", nuevoSaldo);
            return Response.ok(respuesta).build();
        } 
        catch (IllegalArgumentException e) 
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(new RespuestaError("Error: " + e.getMessage())).build();
        }
        catch (Exception e) 
        {
            System.err.println("Error en recargarCartera: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new RespuestaError("Error interno del servidor")).build();
        }
    }
    
    @GET
    @Path("gamer/{id}/biblioteca") // /api/usuarios/gamer/{id}/biblioteca
    @Produces(MediaType.APPLICATION_JSON)
    public Response verBiblioteca(@PathParam("id") int id)
    {
        try
        {
            return Response.ok(service.obtenerBibliotecaGamer(id)).build();
        }
        catch (Exception e)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(new RespuestaError("Error al cargar biblioteca: " + e.getMessage())).build();
        }
    }
    
    @PUT
    @Path("gamer/{id}/biblioteca/{idJuego}/instalar") // api/usuarios/gamer/{id}/biblioteca/{id}/instalar
    @Produces(MediaType.APPLICATION_JSON)
    public Response instalarJuego(@PathParam("id") int idUsuario, @PathParam("idJuego") int idJuego)
    {
        try
        {
            if (service.instalarJuego(idUsuario, idJuego))
            {
                return Response.ok(new RespuestaExito("Juego instalado correctamente")).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity(new RespuestaError("No se pudo instalar.")).build();
        }
        catch (Exception e)
        {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new RespuestaError(e.getMessage())).build();
        }
    }
    
    @PUT
    @Path("gamer/{id}/biblioteca/{idJuego}/desinstalar") // api/usuarios/gamer/{id}/biblioteca/{id}/desinstalar
    @Produces(MediaType.APPLICATION_JSON)
    public Response desinstalarJuego(@PathParam("id") int idUsuario, @PathParam("idJuego") int idJuego) {
        try
        {
            if (service.desinstalarJuego(idUsuario, idJuego))
            {
                return Response.ok(new RespuestaExito("Juego desinstalado correctamente")).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity(new RespuestaError("No se pudo desinstalar.")).build();
        }
        catch (Exception e)
        {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new RespuestaError(e.getMessage())).build();
        }
    }
    
    @GET
    @Path("{idUsuario}/vista-publica") // api/usuarios/{id}/vista-publica
    @Produces(MediaType.APPLICATION_JSON)
    public Response verPerfilPublico(@PathParam("idUsuario") int idBuscado, @QueryParam("solicitante") int idSolicitante) 
    {
        try 
        {
            UsuarioComunGamerDTO perfil = service.obtenerPerfilPublico(idBuscado, idSolicitante);
            return Response.ok(perfil).build();
        } 
        catch (Exception e) 
        {
            return Response.status(Response.Status.NOT_FOUND).entity(new RespuestaError(e.getMessage())).build();
        }
    }

    @PUT
    @Path("{idUsuario}/privacidad") // api/usuarios/{id}/privacidad
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cambiarPrivacidad(@PathParam("idUsuario") int idUsuario, UsuarioComunGamerDTO dto)
    {
        try 
        {
            boolean exito = service.cambiarPrivacidad(idUsuario, dto.isPerfilPublico());
            if (exito) return Response.ok(new RespuestaExito("Privacidad actualizada.")).build();
            return Response.serverError().build();
        } 
        catch (Exception e) 
        {
            return Response.status(400).entity(new RespuestaError(e.getMessage())).build();
        }
    }
    
    @GET 
    @Path("buscar") // api/usuarios/buscar
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarUsuarios(@QueryParam("q") String query) 
    {
        try 
        {
            return Response.ok(service.buscarGeneral(query)).build();
        } 
        catch (Exception e) 
        {
            return Response.serverError().build();
        }
    }
    
    @GET
    @Path("gamer/{id}/analisis") // api/usuarios/gamer/{id}/analisis
    @Produces(MediaType.APPLICATION_JSON)
    public Response reporteAnalisisGamer(@PathParam("id") int idGamer) 
    {
        try 
        {
            return Response.ok(service.generarAnalisisGamer(idGamer)).build();
        } 
        catch (Exception e) 
        {
            return Response.serverError().build();
        }
    }
}
