package com.mycompany.tiendavideojuegos.resources;

import com.mycompany.tiendavideojuegos.DTO.BannerDTO;
import com.mycompany.tiendavideojuegos.DTO.CategoriaDTO;
import com.mycompany.tiendavideojuegos.DTO.ConfiguracionDTO;
import com.mycompany.tiendavideojuegos.models.Banner;
import com.mycompany.tiendavideojuegos.models.Categoria;
import com.mycompany.tiendavideojuegos.models.Configuracion;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path ("admin")
public class AdminResource 
{
    private final Categoria modeloCat = new Categoria();
    private final Configuracion modeloConfig = new Configuracion();
    private final Banner modeloBanner = new Banner();

    @GET // /api/admin/categorias/{id}
    @Path("categorias")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCategorias() 
    {
        return Response.ok(modeloCat.listar()).build();
    }

    @POST // /api/admin/categorias
    @Path("categorias")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response crearCategoria(CategoriaDTO cat) 
    {
        if(modeloCat.crear(cat)) return Response.ok().build();
        return Response.serverError().build();
    }

    @GET // /api/admin/config/{id}
    @Path("config")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConfig() 
    {
        return Response.ok(modeloConfig.obtenerConfiguracion()).build();
    }
    
    @POST // /api/admin/config (Actualizar comisión y ajustar empresas)
    @Path("config")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateConfig(ConfiguracionDTO config) 
    {
        if(modeloConfig.actualizarComisionGlobal(config.getComisionGlobalActual())) 
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
        return Response.ok(modeloBanner.listarBannersActivos()).build();
    }

    @POST // /api/admin/banner
    @Path("banner")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addBanner(BannerDTO banner) 
    {
        if(modeloBanner.agregar(banner)) return Response.ok().build();
        return Response.serverError().build();
    }
}
