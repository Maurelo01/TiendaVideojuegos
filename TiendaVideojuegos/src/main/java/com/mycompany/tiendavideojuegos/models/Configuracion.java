package com.mycompany.tiendavideojuegos.models;

import com.mycompany.tiendavideojuegos.DTO.ConfiguracionDTO;
import com.mycompany.tiendavideojuegos.configuracion.ConexionDB;

public class Configuracion
{
    public ConfiguracionDTO obtenerConfiguracion()
    {
        ConexionDB connMySQL = new ConexionDB();
        var conn = connMySQL.conectar();
        ConfiguracionDTO configuracion = null;
        try
        {
            var ps = conn.prepareStatement("SELECT * FROM Configuracion_Sistema LIMIT 1");
            var rs = ps.executeQuery();
            if (rs.next())
            {
                configuracion = new ConfiguracionDTO();
                configuracion.setIdConfiguracion(rs.getInt("id_config"));
                configuracion.setComisionGlobalActual(rs.getFloat("comision_global_actual"));
            }
            rs.close();
            ps.close();
        }
        catch (Exception e)
        {
            System.err.println("Error obteniendo configuración: " + e.getMessage());
        }
        finally
        {
            connMySQL.desconectar(conn);
        }
        return configuracion;
    }
    
    public boolean actualizarComisionGlobal(float nuevaComision) 
    {
        ConexionDB connMySQL = new ConexionDB();
        var conn = connMySQL.conectar();
        boolean exito = false;
        float valorAnterior = 0;
        try 
        {
            // Guardar valor anterior
            var psLectura = conn.prepareStatement("SELECT comision_global_actual FROM Configuracion_Sistema LIMIT 1");
            var rs = psLectura.executeQuery();
            if (rs.next()) 
            {
                valorAnterior = rs.getFloat("comision_global_actual");
            }
            rs.close();
            psLectura.close();
            // Poner el nuevo valor
            var psUpdate = conn.prepareStatement("UPDATE Configuracion_Sistema SET comision_global_actual = ?");
            psUpdate.setFloat(1, nuevaComision);
            psUpdate.executeUpdate();
            psUpdate.close();
            // Ajustar el porcentaje en las empresas
            try 
            {
                // Si el porcentaje especifico > nueva global se baja al nuevo global
                var psAjuste = conn.prepareStatement("UPDATE Empresa SET porcentaje_comision_especifica = ? WHERE porcentaje_comision_especifica > ?");
                psAjuste.setFloat(1, nuevaComision);
                psAjuste.setFloat(2, nuevaComision);
                psAjuste.close();
                exito = true;
            }
            catch (Exception e)
            {
                // Si hay un fallo regresa al valor anterior
                System.err.println("Error ajustando empresas, revirtiendo cambio");
                if (valorAnterior != 0)
                {
                    var psRevertir = conn.prepareStatement("UPDATE Configuracion_Sistema SET comision_global_actual = ?");
                    psRevertir.setFloat(1, valorAnterior);
                    psRevertir.executeUpdate();
                    psRevertir.close();
                }
            }
        } 
        catch (Exception e) 
        {
            System.err.println("Error en actualización de comisión: " + e.getMessage());
        }
        finally
        {
            connMySQL.desconectar(conn);
        }
        return exito;
    }
}