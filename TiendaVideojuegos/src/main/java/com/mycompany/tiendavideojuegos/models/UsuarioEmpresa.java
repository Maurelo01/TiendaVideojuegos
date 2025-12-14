package com.mycompany.tiendavideojuegos.models;

import com.mycompany.tiendavideojuegos.DTO.EmpresaDTO;
import com.mycompany.tiendavideojuegos.DTO.UsuarioEmpresaDTO;
import com.mycompany.tiendavideojuegos.configuracion.ConexionDB;
import java.sql.Statement;

public class UsuarioEmpresa
{
    public boolean registrarEmpresaYUsuario(EmpresaDTO empresa, UsuarioEmpresaDTO usuario)
    {
        ConexionDB connMySQL = new ConexionDB();
        var conn = connMySQL.conectar();
        boolean exito = false;
        // Antes de generar ids
        int idEmpresa = -1;
        int idUsuario = -1;
        try
        {
            // Insertar Empresa
            var psEmpresa = conn.prepareStatement("INSERT INTO Empresa (nombre_empresa, descripcion, imagen_banner) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            psEmpresa.setString(1, empresa.getNombreEmpresa());
            psEmpresa.setString(2, empresa.getDescripcion());
            psEmpresa.setString(3, empresa.getImagenBanner());
            psEmpresa.executeUpdate();
            var rsEmpresa = psEmpresa.getGeneratedKeys();
            if (rsEmpresa.next()) 
            {
                idEmpresa = rsEmpresa.getInt(1);
            }
            rsEmpresa.close();
            psEmpresa.close();
            
            // Insertar Usuario
            var psUsuario = conn.prepareStatement("INSERT INTO Usuario (correo, contraseña, rol) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            psUsuario.setString(1, usuario.getCorreo());
            psUsuario.setString(2, usuario.getContraseña());
            psUsuario.setString(3, "EMPRESA");
            psUsuario.executeUpdate();
            var rsUsuario = psUsuario.getGeneratedKeys();
            if (rsUsuario.next())
            {
                idUsuario = rsUsuario.getInt(1);
            }
            rsUsuario.close();
            psUsuario.close();
            
            // Insertar Usuario_Empresa
            if (idEmpresa != -1 && idUsuario != -1) 
            {
                var psUsuarioEmpresa = conn.prepareStatement("INSERT INTO Usuario_Empresa (id_usuario, id_empresa, nombre_empleado, fecha_nacimiento_empleado) VALUES (?, ?, ?, ?)");
                psUsuarioEmpresa.setInt(1, idUsuario);
                psUsuarioEmpresa.setInt(2, idEmpresa);
                psUsuarioEmpresa.setString(3, usuario.getNombreEmpleado());
                psUsuarioEmpresa.setDate(4, usuario.getFechaNacimiento());
                psUsuarioEmpresa.executeUpdate();
                psUsuarioEmpresa.close();
                exito = true;
            }
        }
        catch (Exception e)
        {
            System.err.println("Error al registrar empresa: " + e.getMessage());
            try 
            {
                if (idEmpresa != -1) 
                {
                    var psDelEmpresa = conn.prepareStatement("DELETE FROM Empresa WHERE id_empresa = ?");
                    psDelEmpresa.setInt(1, idEmpresa);
                    psDelEmpresa.executeUpdate();
                }
                if (idUsuario != -1) 
                {
                    var psDelUsuario = conn.prepareStatement("DELETE FROM Usuario WHERE id_usuario = ?");
                    psDelUsuario.setInt(1, idUsuario);
                    psDelUsuario.executeUpdate();
                }
            } 
            catch (Exception ex) 
            {
                System.err.println("Error registros usuario sin tabla padre: " + ex.getMessage());
            }
            finally 
            {
                connMySQL.desconectar(conn);
            }
        }
        return exito;
    }
}
