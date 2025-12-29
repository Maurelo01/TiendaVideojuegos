package com.mycompany.tiendavideojuegos.models;

import com.mycompany.tiendavideojuegos.DTO.ConfiguracionDTO;
import com.mycompany.tiendavideojuegos.configuracion.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Configuracion
{
    public ConfiguracionDTO obtenerConfiguracion()
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        ConfiguracionDTO configuracion = null;
        try
        {
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Configuracion_Sistema LIMIT 1"); ResultSet rs = ps.executeQuery()) 
            {
                if (rs.next())
                {
                    configuracion = new ConfiguracionDTO();
                    configuracion.setIdConfiguracion(rs.getInt("id_config"));
                    configuracion.setComisionGlobalActual(rs.getFloat("comision_global_actual"));
                }
            }
        }
        catch (Exception e)
        {
            System.err.println("Error obteniendo configuración: " + e.getMessage());
        }
        return configuracion;
    }
    
    public boolean actualizarComisionGlobal(float nuevaComision) 
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        boolean exito = false;
        float valorAnterior = 0;
        try 
        {
            // Guardar valor anterior
            try (PreparedStatement psLectura = conn.prepareStatement("SELECT comision_global_actual FROM Configuracion_Sistema LIMIT 1"); ResultSet rs = psLectura.executeQuery()) 
            {
                if (rs.next())
                {
                    valorAnterior = rs.getFloat("comision_global_actual");
                }
            }
            // Poner el nuevo valor
            try (PreparedStatement psUpdate = conn.prepareStatement("UPDATE Configuracion_Sistema SET comision_global_actual = ?")) 
            {
                psUpdate.setFloat(1, nuevaComision);
                psUpdate.executeUpdate();
            }
            // Ajustar el porcentaje en las empresas
            try 
            {
                // Si el porcentaje especifico > nueva global se baja al nuevo global
                try (PreparedStatement psAjuste = conn.prepareStatement("UPDATE Empresa SET porcentaje_comision_especifica = ? WHERE porcentaje_comision_especifica > ?")) 
                {
                    psAjuste.setFloat(1, nuevaComision);
                    psAjuste.setFloat(2, nuevaComision);
                    int afectados = psAjuste.executeUpdate();
                    if (afectados > 0)
                    {
                        System.out.println("Ajuste automático: Se actualizaron " + afectados + " empresas.");
                    }
                }
                exito = true;
            }
            catch (Exception e)
            {
                // Si hay un fallo regresa al valor anterior
                System.err.println("Error ajustando empresas, revirtiendo cambio");
                if (valorAnterior != 0)
                {
                    try (PreparedStatement psRevertir = conn.prepareStatement("UPDATE Configuracion_Sistema SET comision_global_actual = ?")) 
                    {
                        psRevertir.setFloat(1, valorAnterior);
                        psRevertir.executeUpdate();
                    }
                }
            }
        } 
        catch (Exception e) 
        {
            System.err.println("Error en actualización de comisión: " + e.getMessage());
        }
        return exito;
    }
}