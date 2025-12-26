package com.mycompany.tiendavideojuegos.resources;

import com.mycompany.tiendavideojuegos.DTO.BannerDTO;
import com.mycompany.tiendavideojuegos.DTO.CategoriaDTO;
import com.mycompany.tiendavideojuegos.DTO.ConfiguracionDTO;
import com.mycompany.tiendavideojuegos.DTO.EmpresaDTO;
import com.mycompany.tiendavideojuegos.services.AdminService;
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

@Path ("admin")
public class AdminResource 
{
    private final AdminService service = new AdminService();
    
    @GET // /api/admin/categorias/{id}
    @Path("categorias")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCategorias() 
    {
        return Response.ok(service.listarCategorias()).build();
    }

    @POST // /api/admin/categorias
    @Path("categorias")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response crearCategoria(CategoriaDTO cat) 
    {
        try 
        {
            if(service.crearCategoria(cat)) 
            {
                return Response.status(Response.Status.CREATED).entity("Exito: Categoría creada correctamente").build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: No se pudo crear la categoría.").build();
        } 
        catch (Exception e) 
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error de validación: " + e.getMessage()).build();
        }
    }

    @GET // /api/admin/config/{id}
    @Path("config")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConfig() 
    {
        return Response.ok(service.obtenerConfiguracion()).build();
    }
    
    @PUT // /api/admin/config (Actualizar comisión y ajustar empresas)
    @Path("config")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizarConfig(ConfiguracionDTO config) 
    {
        if(service.actualizarComisionGlobal(config.getComisionGlobalActual())) 
        {
             return Response.ok("Exito: Comisión actualizada y empresas ajustadas").build();
        }
        return Response.serverError().build();
    }

    @GET // /api/admin/banner/{id}
    @Path("banner")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBanner() 
    {
        return Response.ok(service.listarBannersActivos()).build();
    }

    @POST // /api/admin/banner
    @Path("banner")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response añadirBanner(BannerDTO banner) 
    {
        if(service.agregarBanner(banner)) return Response.ok().build();
        return Response.serverError().build();
    }
    
    @PUT
    @Path("categorias/{id}") // /api/admin/categorias/{id}
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editarCategoria(@PathParam("id") int id, @QueryParam("confirmar") boolean confirmar, CategoriaDTO cat) 
    {
        try 
        {
            cat.setIdCategoria(id);
            if(service.editarCategoria(cat, confirmar)) 
            {
                return Response.ok("Exito: Categoría actualizada correctamente").build();
            }
            return Response.status(Response.Status.NOT_FOUND).entity("Error: No se encontró la categoría a editar.").build();
        } 
        catch (Exception e) 
        {
            if (e.getMessage().startsWith("Advertencia")) 
            {
                return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity("Error de validación: " + e.getMessage()).build();
        }
    }
    
    @DELETE
    @Path("categorias/{id}") // /api/admin/categorias/{id}
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminarCategoria(@PathParam("id") int id, @QueryParam("confirmar") boolean confirmar)
    {
        try 
        {
            if(service.eliminarCategoria(id, confirmar)) 
            {
                return Response.ok("Exito: Categoría eliminada correctamente").build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: No se pudo eliminar la categoría.").build();
        } 
        catch (Exception e) 
        {
            if (e.getMessage().startsWith("Advertencia")) 
            {
                return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: " + e.getMessage()).build();
        }
    }
    
    @GET
    @Path("empresas") // /api/admin/empresas
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarEmpresas() 
    {
        return Response.ok(service.listarEmpresas()).build();
    }
    
    @PUT
    @Path("empresas/{id}") // /api/admin/empresas/{id}
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editarEmpresa(@PathParam("id") int id, EmpresaDTO empresa) 
    {
        try 
        {
            empresa.setIdEmpresa(id);
            if (service.editarEmpresa(empresa)) 
            {
                return Response.ok("Exito: Empresa actualizada").build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: No se pudo actualizar").build();
        } 
        catch (Exception e) 
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: " + e.getMessage()).build();
        }
    }
    
    @PUT
    @Path("empresas/{id}/suspender") // /api/admin/empresas/{id}/suspender
    @Produces(MediaType.APPLICATION_JSON)
    public Response suspenderEmpresa(@PathParam("id") int id) 
    {
        try 
        {
            if(service.suspenderEmpresa(id)) return Response.ok("Exito: Empresa suspendida").build();
            return Response.serverError().build();
        }
        catch(Exception e)
        {
            return Response.status(400).entity(e.getMessage()).build();
        }
    }
    
    @PUT
    @Path("empresas/{id}/activar") ///api/admin/empresas/{id}/activar 
    @Produces(MediaType.APPLICATION_JSON)
    public Response activarEmpresa(@PathParam("id") int id)
    {
        try 
        {
            if (service.activarEmpresa(id))
            {
                return Response.ok("Exito: Empresa activada").build();
            }
            return Response.serverError().build();
        }
        catch (Exception e)
        {
            return Response.status(400).entity(e.getMessage()).build();
        }
    }
}
