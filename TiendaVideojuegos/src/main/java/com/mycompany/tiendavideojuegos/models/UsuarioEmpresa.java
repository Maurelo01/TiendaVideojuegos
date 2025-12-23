package com.mycompany.tiendavideojuegos.models;

import com.mycompany.tiendavideojuegos.DTO.EmpresaDTO;
import com.mycompany.tiendavideojuegos.DTO.UsuarioEmpresaDTO;
import com.mycompany.tiendavideojuegos.configuracion.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UsuarioEmpresa
{
    public boolean registrarEmpresaYUsuario(EmpresaDTO empresa, UsuarioEmpresaDTO usuario)
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        boolean exito = false;
        // Antes de generar ids
        int idEmpresa = -1;
        int idUsuario = -1;
        try
        {
            // Insertar Empresa
            try (PreparedStatement psEmpresa = conn.prepareStatement("INSERT INTO Empresa (nombre_empresa, descripcion, imagen_banner) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) 
            {   
                psEmpresa.setString(1, empresa.getNombreEmpresa());
                psEmpresa.setString(2, empresa.getDescripcion());
                psEmpresa.setString(3, empresa.getImagenBanner());
                psEmpresa.executeUpdate();
                try (ResultSet rsEmpresa = psEmpresa.getGeneratedKeys()) 
                {
                    if (rsEmpresa.next())
                    {
                        idEmpresa = rsEmpresa.getInt(1);
                    }
                }
            }
            // Insertar Usuario
            try (PreparedStatement psUsuario = conn.prepareStatement("INSERT INTO Usuario (correo, contraseña, rol) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) 
            {
                psUsuario.setString(1, usuario.getCorreo());
                psUsuario.setString(2, usuario.getContraseña());
                psUsuario.setString(3, "EMPRESA");
                psUsuario.executeUpdate();
                try (java.sql.ResultSet rsUsuario = psUsuario.getGeneratedKeys()) 
                {
                    if (rsUsuario.next())
                    {
                        idUsuario = rsUsuario.getInt(1);
                    }
                }
            }
            
            // Insertar Usuario_Empresa
            if (idEmpresa != -1 && idUsuario != -1) 
            {
                try (PreparedStatement psUsuarioEmpresa = conn.prepareStatement("INSERT INTO Usuario_Empresa (id_usuario, id_empresa, nombre_empleado, fecha_nacimiento_empleado) VALUES (?, ?, ?, ?)")) 
                {
                    psUsuarioEmpresa.setInt(1, idUsuario);
                    psUsuarioEmpresa.setInt(2, idEmpresa);
                    psUsuarioEmpresa.setString(3, usuario.getNombreEmpleado());
                    psUsuarioEmpresa.setDate(4, usuario.getFechaNacimiento());
                    psUsuarioEmpresa.executeUpdate();
                }
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
        }
        return exito;
    }
    
    public boolean agregarEmpleado(int idEmpresaExistente, UsuarioEmpresaDTO nuevoUsuario) 
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        int idUsuarioGenerado = -1;
        try 
        {
            try (PreparedStatement psUser = conn.prepareStatement( "INSERT INTO Usuario (correo, contraseña, rol) VALUES (?, ?, 'EMPRESA')", Statement.RETURN_GENERATED_KEYS)) 
            {
                psUser.setString(1, nuevoUsuario.getCorreo());
                psUser.setString(2, nuevoUsuario.getContraseña());
                int filasAfectadas = psUser.executeUpdate();
                if (filasAfectadas == 0) 
                {
                    throw new SQLException("No se pudo crear el usuario, ninguna fila afectada.");
                }

                try (ResultSet rs = psUser.getGeneratedKeys()) 
                {
                    if (rs.next()) 
                    {
                        idUsuarioGenerado = rs.getInt(1);
                    } 
                    else 
                    {
                        throw new SQLException("No se pudo obtener el id del usuario creado.");
                    }
                }
            }
            if (idUsuarioGenerado != -1)
            {
                try (PreparedStatement psUnir = conn.prepareStatement("INSERT INTO Usuario_Empresa (id_usuario, id_empresa, nombre_empleado, fecha_nacimiento_empleado) VALUES (?, ?, ?, ?)")) 
                {
                    psUnir.setInt(1, idUsuarioGenerado);
                    psUnir.setInt(2, idEmpresaExistente);
                    psUnir.setString(3, nuevoUsuario.getNombreEmpleado());
                    psUnir.setDate(4, nuevoUsuario.getFechaNacimiento());
                    psUnir.executeUpdate();
                    return true;
                }
            }
        } 
        catch (Exception e) 
        {
            System.err.println("Error en la agregar empleado: " + e.getMessage());
            if (idUsuarioGenerado != -1) 
            {
                try 
                {
                    System.out.println("Intentando borrar usuario id: " + idUsuarioGenerado);
                    try (PreparedStatement psDelete = conn.prepareStatement("DELETE FROM Usuario WHERE id_usuario = ?")) 
                    {
                        psDelete.setInt(1, idUsuarioGenerado);
                        psDelete.executeUpdate();
                        System.out.println("Exito: Usuario borrado.");
                    }
                }
                catch (SQLException ex) 
                {
                    System.err.println("CRÍTICO: No se pudo borrar la creación del usuario: " + ex.getMessage());
                }
            }
        }
        return false;
    }
}
