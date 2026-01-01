package com.mycompany.tiendavideojuegos.resources;

import com.mycompany.tiendavideojuegos.DTO.RespuestaError;
import com.mycompany.tiendavideojuegos.DTO.RespuestaExito;
import com.mycompany.tiendavideojuegos.DTO.SolicitudPrestamoDTO; // Importamos el nuevo DTO
import com.mycompany.tiendavideojuegos.services.PrestamosService;
import com.mycompany.tiendavideojuegos.services.ReportesService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("prestamos")
public class PrestamosResource 
{
    private final PrestamosService service = new PrestamosService();
    private final ReportesService reporteService = new ReportesService();

    @POST
    @Path("solicitar") // api/prestamos/solicitar
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response solicitarPrestamo(SolicitudPrestamoDTO solicitud)
    {
        try 
        {
            if (solicitud.getIdUsuarioSolicitante() <= 0 || solicitud.getIdJuego() <= 0 || solicitud.getIdPropietario() <= 0)
            {
                return Response.status(Response.Status.BAD_REQUEST).entity(new RespuestaError("Faltan datos obligatorios para el préstamo.")).build();
            }

            if (service.solicitarPrestamo(solicitud.getIdUsuarioSolicitante(), solicitud.getIdJuego(), solicitud.getIdPropietario())) 
            {
                return Response.ok(new RespuestaExito("Juego instalado (prestado) correctamente.")).build();
            }
            
            return Response.status(Response.Status.BAD_REQUEST).entity(new RespuestaError("No se pudo realizar el préstamo.")).build();
        } 
        catch (Exception e) 
        {
            // Retornamos 409 Conflict si hay reglas de negocio violadas (como juego en uso)
            return Response.status(Response.Status.CONFLICT).entity(new RespuestaError(e.getMessage())).build();
        }
    }

    @PUT 
    @Path("devolver") // api/prestamos/devolver
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response devolverPrestamo(SolicitudPrestamoDTO solicitud) 
    {
        try 
        {
            if (service.devolverPrestamo(solicitud.getIdUsuarioSolicitante(), solicitud.getIdJuego())) 
            {
                return Response.ok(new RespuestaExito("Juego devuelto y desinstalado.")).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity(new RespuestaError("No se encontró un préstamo activo para devolver.")).build();
        } 
        catch (Exception e) 
        {
            return Response.serverError().entity(new RespuestaError(e.getMessage())).build();
        }
    }
    
    @GET
    @Path("reporte/usuario/{id}") // api/prestamos/reporte/usuario/{id}
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerReporteUso(@PathParam("id") int idUsuario) 
    {
        try 
        {
            return Response.ok(service.generarReporteUso(idUsuario)).build();
        } 
        catch (Exception e) 
        {
            return Response.serverError().build();
        }
    }
    
    @GET
    @Path("reporte/usuario/{id}/pdf")
    @Produces("application/pdf")
    public Response descargarUsoFamiliarPdf(@PathParam("id") int idUsuario) 
    {
        try 
        {
            var lista = service.generarReporteUso(idUsuario);
            Map<String, Object> params = new HashMap<>();
            params.put("TITULO_REPORTE", "Uso de Biblioteca Familiar");
            byte[] pdfBytes = reporteService.generarReportePDF("ReporteUsoFamiliar.jasper", params, lista);
            return Response.ok(pdfBytes).header("Content-Disposition", "attachment; filename=Uso_Familiar.pdf").build();
        } 
        catch (Exception e)
        {
            return Response.serverError().build();
        }
    }
}   