package com.mycompany.tiendavideojuegos.resources;

import com.mycompany.tiendavideojuegos.DTO.EmpresaDTO;
import com.mycompany.tiendavideojuegos.DTO.UsuarioComunGamerDTO;
import com.mycompany.tiendavideojuegos.DTO.UsuarioDTO;
import com.mycompany.tiendavideojuegos.DTO.UsuarioEmpresaDTO;
import com.mycompany.tiendavideojuegos.models.Usuario;
import com.mycompany.tiendavideojuegos.models.UsuarioComunGamer;
import com.mycompany.tiendavideojuegos.models.UsuarioEmpresa;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class UsuariosResource 
{
    private final Usuario modeloUsuario = new Usuario();
    private final UsuarioComunGamer modeloGamer = new UsuarioComunGamer();
    private final UsuarioEmpresa modeloEmpresa = new UsuarioEmpresa();
    
    @POST // /api/usuarios/login
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(UsuarioDTO credenciales)
    {
        try 
        {
            var usuario = modeloUsuario.login(credenciales.getCorreo(), credenciales.getContraseña());
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
        var resultado = modeloGamer.registrarGamer(gamer);
        if (resultado != null) 
        {
            return Response.status(Response.Status.CREATED).entity(resultado).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("Error: No se pudo registrar el Usuario gamer").build();
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
        boolean exito = modeloEmpresa.registrarEmpresaYUsuario(request.empresa, request.usuario);
        if (exito) 
        {
            return Response.status(Response.Status.CREATED).entity("Exito: Empresa creada exitosamente").build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("Error: Error al crear empresa").build();
    }
}
