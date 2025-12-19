package com.mycompany.tiendavideojuegos.resources;

import com.mycompany.tiendavideojuegos.DTO.EmpresaDTO;
import com.mycompany.tiendavideojuegos.DTO.UsuarioComunGamerDTO;
import com.mycompany.tiendavideojuegos.DTO.UsuarioDTO;
import com.mycompany.tiendavideojuegos.DTO.UsuarioEmpresaDTO;
import com.mycompany.tiendavideojuegos.services.UsuariosService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
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
    public Response login(UsuarioDTO credenciales)
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
    
    public static class RegistroEmpresaRequest
    {
        public EmpresaDTO empresa;
        public UsuarioEmpresaDTO usuario;
    }
    
    @POST // /api/usuarios/registro/empresa
    @Path("registro/empresa")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarEmpresa(RegistroEmpresaRequest request) 
    {
        try 
        {
            if (request == null || request.empresa == null || request.usuario == null) 
            {
                return Response.status(Response.Status.BAD_REQUEST).entity("Error: Datos de registro incompletos.").build();
            }
            boolean exito = service.registrarEmpresa(request.empresa, request.usuario);
            if (exito) 
            {
                return Response.status(Response.Status.CREATED).entity("Exito: Empresa y usuario administrador creados exitosamente").build();
            }
            return Response.serverError().entity("Error: No se pudo registrar la empresa. Intente nuevamente.").build();
        } 
        catch (Exception e) 
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error de validación: " + e.getMessage()).build();
        }
    }
}
