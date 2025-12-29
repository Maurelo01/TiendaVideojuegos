package com.mycompany.tiendavideojuegos.services;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

public class ReportesService 
{
    public byte[] generarReportePDF(String nombreReporte, Map<String, Object> parametros, List<?> datos) throws Exception 
    {
        // Cargar el archivo .jasper desde el src/main/resources
        InputStream reporteStream = getClass().getClassLoader().getResourceAsStream("reportes/" + nombreReporte);
        if (reporteStream == null) 
        {
            throw new Exception("No se encontró la plantilla del reporte: " + nombreReporte);
        }
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reporteStream);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(datos);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}