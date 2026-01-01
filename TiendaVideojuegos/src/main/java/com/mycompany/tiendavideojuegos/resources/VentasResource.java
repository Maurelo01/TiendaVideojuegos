package com.mycompany.tiendavideojuegos.resources;

import com.mycompany.tiendavideojuegos.DTO.RespuestaError;
import com.mycompany.tiendavideojuegos.DTO.RespuestaExito;
import com.mycompany.tiendavideojuegos.DTO.RespuestaVerificacion;
import com.mycompany.tiendavideojuegos.DTO.SolicitudCompra;
import com.mycompany.tiendavideojuegos.services.ComentariosService;
import com.mycompany.tiendavideojuegos.services.ReportesService;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Path("ventas")
public class VentasResource 
{
    private final ComentariosService comentariosService = new ComentariosService();
    private final VentasService service = new VentasService();
    private final ReportesService reporteService = new ReportesService();

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
    
    @GET
    @Path("historial/{idUsuario}") // api/ventas/historial/{idUsuario}
    @Produces(MediaType.APPLICATION_JSON)
    public Response verHistorial(@PathParam("idUsuario") int idUsuario)
    {
        try
        {
            return Response.ok(service.obtenerHistorial(idUsuario)).build();
        }
        catch (Exception e)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(new RespuestaError("Error al cargar historial: " + e.getMessage())).build();
        }
    }
      
    @GET
    @Path("reporte/admin/pdf")  // api/ventas/reporte/admin/pdf
    @Produces("application/pdf")
    public Response descargarReporteAdminPdf(@QueryParam("inicio") String inicio, @QueryParam("fin") String fin) 
    {
        try 
        {
            var listaDatos = service.obtenerReporteAdmin(inicio, fin);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("FECHA_INICIO", inicio != null ? inicio : "Histórico");
            parametros.put("FECHA_FIN", fin != null ? fin : "Actualidad");
            parametros.put("TITULO_REPORTE", "Reporte Financiero Global");
            byte[] pdfBytes = reporteService.generarReportePDF("ReporteAdmin.jasper", parametros, listaDatos);
            return Response.ok(pdfBytes).header("Content-Disposition", "attachment; filename=Reporte_Admin.pdf").build();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return Response.serverError().entity("Error generando PDF: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("reporte/empresa/{id}/pdf") // api/ventas/reporte/empresa/{id}/pdf
    @Produces("application/pdf")
    public Response descargarReporteEmpresaPdf(@PathParam("id") int idEmpresa, @QueryParam("inicio") String inicio, @QueryParam("fin") String fin) 
    {
        try 
        {
            var listaDatos = service.obtenerReporteEmpresa(idEmpresa, inicio, fin);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("FECHA_INICIO", inicio != null ? inicio : "Histórico");
            parametros.put("FECHA_FIN", fin != null ? fin : "Actualidad");
            parametros.put("TITULO_REPORTE", "Reporte de Ventas por Juego");
            byte[] pdfBytes = reporteService.generarReportePDF("ReporteEmpresa.jasper", parametros, listaDatos);
            return Response.ok(pdfBytes).header("Content-Disposition", "attachment; filename=Reporte_Empresa_" + idEmpresa + ".pdf").build();
        } 
        catch (Exception e) 
        {
            return Response.serverError().entity("Error generando PDF: " + e.getMessage()).build();
        }
    }
    
    @GET
    @Path("historial/{id}/pdf") // api/ventas/reporte/historial/{id}/pdf
    @Produces("application/pdf")
    public Response descargarHistorialPdf(@PathParam("id") int idUsuario) 
    {
        try 
        {
            var listaDatos = service.obtenerHistorial(idUsuario);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("TITULO_REPORTE", "Historial de Compras");
            byte[] pdfBytes = reporteService.generarReportePDF("HistorialUsuario.jasper", parametros, listaDatos);
            return Response.ok(pdfBytes).header("Content-Disposition", "attachment; filename=Mis_Compras.pdf").build();
        } 
        catch (Exception e) 
        {
            return Response.serverError().entity("Error generando PDF: " + e.getMessage()).build();
        }
    }
    
    @GET
    @Path("reporte/ranking-compradores") // api/ventas/reporte/ranking-compradores
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRankingCompradores() 
    {
        try 
        {
            return Response.ok(service.obtenerRankingCompradores()).build();
        } 
        catch (Exception e) 
        {
            return Response.serverError().build();
        }
    }
    
    @GET
    @Path("reporte/ranking-reviewers") // api/ventas/reporte/ranking-reviewers
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRankingReviewers() 
    {
        try 
        {
            return Response.ok(service.obtenerRankingReviewers()).build();
        } 
        catch (Exception e) 
        {
            return Response.serverError().build();
        }
    }
    
    @GET
    @Path("reporte/top-juegos") // api/ventas/reporte/top-juegos?categoria=1&edad=T
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopJuegos(@QueryParam("categoria") Integer idCategoria, @QueryParam("edad") String edad) 
    {
        try 
        {
            return Response.ok(service.obtenerTopJuegos(idCategoria, edad)).build();
        } 
        catch (Exception e) 
        {
            return Response.serverError().build();
        }
    }
    
    @GET
    @Path("reporte/empresa/{id}/top5") // api/ventas/reporte/empresa/{id}/top5?inicio=...&fin=...
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTop5Empresa(@PathParam("id") int idEmpresa, @QueryParam("inicio") String inicio, @QueryParam("fin") String fin) 
    {
        try 
        {
            return Response.ok(service.obtenerTop5Empresa(idEmpresa, inicio, fin)).build();
        } 
        catch (Exception e) 
        {
            return Response.serverError().build();
        }
    }
    
    @GET
    @Path("reporte/ranking/pdf") // api/ventas/reporte/ranking/pdf
    @Produces("application/pdf")
    public Response descargarRankingPdf() 
    {
        try 
        {
            var compradores = service.obtenerRankingCompradores();
            var reviewers = service.obtenerRankingReviewers();
            Map<String, Object> params = new HashMap<>();
            params.put("TITULO_REPORTE", "Ranking de Usuarios");
            params.put("ReviewersDS", new JRBeanCollectionDataSource(reviewers));
            byte[] pdfBytes = reporteService.generarReportePDF("ReporteRanking.jasper", params, compradores);
            return Response.ok(pdfBytes).header("Content-Disposition", "attachment; filename=Ranking_Usuarios.pdf").build();
        } 
        catch (Exception e)
        {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @GET
    @Path("reporte/top-juegos/pdf") 
    @Produces("application/pdf")
    public Response descargarTopJuegosPdf(@QueryParam("categoria") Integer idCategoria, @QueryParam("edad") String edad) 
    {
        try 
        {
            var lista = service.obtenerTopJuegos(idCategoria, edad);
            Map<String, Object> params = new HashMap<>();
            params.put("TITULO_REPORTE", "Top Ventas y Calidad");
            String infoFiltro = "Filtros: ";
            if (idCategoria != null && idCategoria > 0) infoFiltro += "Categoría ID " + idCategoria + " ";
            else infoFiltro += "Todas las Categorías ";
            if (edad != null && !edad.isEmpty()) infoFiltro += "| Edad: " + edad;
            else infoFiltro += "| Edad: Todas";
            params.put("FILTRO_INFO", infoFiltro);
            byte[] pdfBytes = reporteService.generarReportePDF("ReporteTopJuegos.jasper", params, lista);
            return Response.ok(pdfBytes).header("Content-Disposition", "attachment; filename=Top_Juegos_Calidad.pdf").build();
        } 
        catch (Exception e)
        {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }
    
    @GET
    @Path("reporte/feedback/{id}/pdf") 
    @Produces("application/pdf")
    public Response descargarFeedbackPdf(@PathParam("id") int idEmpresa) 
    {
        try 
        {
            var feedback = comentariosService.generarReporteFeedback(idEmpresa);
            Map<String, Object> params = new HashMap<>();
            params.put("TITULO_REPORTE", "Reporte de Feedback y Calidad");
            params.put("PromediosDS", new JRBeanCollectionDataSource(feedback.getPromedios()));
            params.put("TopComentariosDS", new JRBeanCollectionDataSource(feedback.getTopComentarios()));
            params.put("PeoresComentariosDS", new JRBeanCollectionDataSource(feedback.getPeoresComentarios()));
            byte[] pdfBytes = reporteService.generarReportePDF("ReporteFeedback.jasper", params, Arrays.asList(new Object()));
            return Response.ok(pdfBytes).header("Content-Disposition", "attachment; filename=Feedback_Empresa.pdf").build();
        } 
        catch (Exception e)
        {
            e.printStackTrace(); return Response.serverError().build();
        }
    }

    @GET
    @Path("reporte/empresa/{id}/top5/pdf") 
    @Produces("application/pdf")
    public Response descargarTop5Pdf(@PathParam("id") int idEmpresa, @QueryParam("inicio") String inicio, @QueryParam("fin") String fin) 
    {
        try 
        {
            var lista = service.obtenerTop5Empresa(idEmpresa, inicio, fin);
            Map<String, Object> params = new HashMap<>();
            params.put("TITULO_REPORTE", "Top 5 Juegos Más Vendidos");
            params.put("RANGO_FECHAS", "Del " + inicio + " al " + fin);
            byte[] pdfBytes = reporteService.generarReportePDF("ReporteTop5.jasper", params, lista);
            return Response.ok(pdfBytes).header("Content-Disposition", "attachment; filename=Top5_Ventas.pdf").build();
        } 
        catch (Exception e)
        {
            return Response.serverError().build();
        }
    }
}