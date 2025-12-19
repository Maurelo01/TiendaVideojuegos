package com.mycompany.tiendavideojuegos.resources;

import com.mycompany.tiendavideojuegos.DTO.BannerDTO;
import com.mycompany.tiendavideojuegos.DTO.CategoriaDTO;
import com.mycompany.tiendavideojuegos.DTO.ConfiguracionDTO;
import com.mycompany.tiendavideojuegos.services.AdminService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
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
    
    @POST // /api/admin/config (Actualizar comisión y ajustar empresas)
    @Path("config")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateConfig(ConfiguracionDTO config) 
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
    public Response addBanner(BannerDTO banner) 
    {
        if(service.agregarBanner(banner)) return Response.ok().build();
        return Response.serverError().build();
    }
}
