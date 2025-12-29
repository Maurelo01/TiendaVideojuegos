package com.mycompany.tiendavideojuegos.resources;

import com.mycompany.tiendavideojuegos.DTO.RespuestaError;
import com.mycompany.tiendavideojuegos.DTO.RespuestaExito;
import com.mycompany.tiendavideojuegos.DTO.RespuestaVerificacion;
import com.mycompany.tiendavideojuegos.DTO.SolicitudCompra;
import com.mycompany.tiendavideojuegos.services.VentasService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("ventas")
public class VentasResource 
{
    private final VentasService service = new VentasService();

    @POST
    @Path("comprar") // api/ventas/comprar
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response realizarCompra(SolicitudCompra solicitud)
    {
        try
        {
            if (service.procesarCompra(solicitud))
            {
                return Response.ok(new RespuestaExito("¡Compra exitosa! El juego ya fue añadido a tu biblioteca.")).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity(new RespuestaError("No se pudo completar la transacción.")).build();
        }
        catch (Exception e)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(new RespuestaError(e.getMessage())).build();
        }
    }
    
    @GET
    @Path("verificar/{idUsuario}/{idJuego}") // api/ventas/verificar/1/5
    @Produces(MediaType.APPLICATION_JSON)
    public Response verificarPropiedad(@PathParam("idUsuario") int idUsuario, @PathParam("idJuego") int idJuego)
    {
        try
        {
            boolean yaLoTiene = service.verificarJuegoEnBiblioteca(idUsuario, idJuego);
            return Response.ok(new RespuestaVerificacion(yaLoTiene)).build();
        }
        catch (Exception e)
        {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new RespuestaError(e.getMessage())).build();
        }
    }
    
    @GET
    @Path("reporte/empresa/{id}") // api/ventas/reporte/empresa/1?inicio=...&fin=...
    @Produces(MediaType.APPLICATION_JSON)
    public Response reporteEmpresa(@PathParam("id") int idEmpresa, @QueryParam("inicio") String inicio, @QueryParam("fin") String fin) 
    {
        try
        {
            return Response.ok(service.obtenerReporteEmpresa(idEmpresa, inicio, fin)).build();
        }
        catch (Exception e)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(new RespuestaError(e.getMessage())).build();
        }
    }

    @GET
    @Path("reporte/admin") // api/ventas/reporte/admin?inicio=...&fin=...
    @Produces(MediaType.APPLICATION_JSON)
    public Response reporteAdmin(@QueryParam("inicio") String inicio, @QueryParam("fin") String fin)
    {
        try
        {
            return Response.ok(service.obtenerReporteAdmin(inicio, fin)).build();
        }
        catch (Exception e)
        {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new RespuestaError(e.getMessage())).build();
        }
    }
}