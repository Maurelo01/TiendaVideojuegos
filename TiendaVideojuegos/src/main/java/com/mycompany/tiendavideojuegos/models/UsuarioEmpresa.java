package com.mycompany.tiendavideojuegos.models;

import com.mycompany.tiendavideojuegos.DTO.EmpresaDTO;
import com.mycompany.tiendavideojuegos.DTO.ResultadoBusquedaDTO;
import com.mycompany.tiendavideojuegos.DTO.UsuarioEmpresaDTO;
import com.mycompany.tiendavideojuegos.configuracion.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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
                if (empresa.getImagenBanner() != null && !empresa.getImagenBanner().isEmpty()) 
                {
                    byte[] imagenBytes = Base64.getDecoder().decode(empresa.getImagenBanner());
                    psEmpresa.setBytes(3, imagenBytes);
                }
                else
                {
                    psEmpresa.setNull(3, Types.BLOB);
                }
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
                try (ResultSet rsUsuario = psUsuario.getGeneratedKeys()) 
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
    
    public boolean agregarEmpleado(int idEmpresaExistente, UsuarioEmpresaDTO nuevoUsuario) throws Exception
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
        catch (SQLException e) 
        {
            System.err.println("Error en la agregar empleado: " + e.getMessage());
            if (e.getMessage().contains("Duplicate entry")) 
            {
                throw new Exception("El correo electrónico ya está registrado en el sistema.");
            }
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

    public boolean esEmpleadoDeEmpresa(int idUsuario, int idEmpresa) 
    {
        boolean esEmpleado = false;
        Connection conn = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM Usuario_Empresa WHERE id_usuario = ? AND id_empresa = ?")) 
        {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idEmpresa);
            try (ResultSet rs = ps.executeQuery()) 
            {
                if (rs.next()) 
                {
                    esEmpleado = true;
                }
            }
        } 
        catch (Exception e) 
        {
            System.err.println("Error verificando empleado: " + e.getMessage());
        }
        return esEmpleado;
    }
    
    public List<UsuarioEmpresaDTO> listarEmpleados(int idEmpresa) 
    {
        List<UsuarioEmpresaDTO> lista = new ArrayList<>();
        Connection conn = ConexionDB.getInstance().getConnection();
        String sql = "SELECT u.id_usuario, u.correo, ue.nombre_empleado, ue.fecha_nacimiento_empleado FROM Usuario_Empresa ue JOIN Usuario u ON ue.id_usuario = u.id_usuario WHERE ue.id_empresa = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setInt(1, idEmpresa);
            try (ResultSet rs = ps.executeQuery()) 
            {
                while (rs.next()) 
                {
                    UsuarioEmpresaDTO emp = new UsuarioEmpresaDTO();
                    emp.setIdUsuario(rs.getInt("id_usuario"));
                    emp.setCorreo(rs.getString("correo"));
                    emp.setNombreEmpleado(rs.getString("nombre_empleado"));
                    emp.setFechaNacimiento(rs.getDate("fecha_nacimiento_empleado"));
                    lista.add(emp);
                }
            }
        } 
        catch (Exception e) 
        {
            System.err.println("Error listando empleados: " + e.getMessage());
        }
        return lista;
    }
    
    public boolean eliminarUsuario(int idUsuario) 
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Usuario WHERE id_usuario = ?")) 
        {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } 

        catch (Exception e) 
        {
            System.err.println("Error eliminando usuario: " + e.getMessage());
            return false;
        }

    }
    
    public EmpresaDTO obtenerDatosPublicosEmpresa(int idEmpresa) 
    {
        EmpresaDTO empresa = null;
        Connection conn = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Empresa WHERE id_empresa = ?")) 
        {
            ps.setInt(1, idEmpresa);
            try (ResultSet rs = ps.executeQuery()) 
            {
                if (rs.next()) 
                {
                    empresa = new EmpresaDTO();
                    empresa.setIdEmpresa(rs.getInt("id_empresa"));
                    empresa.setNombreEmpresa(rs.getString("nombre_empresa"));
                    empresa.setDescripcion(rs.getString("descripcion"));
                    byte[] imgBytes = rs.getBytes("imagen_banner");
                    if (imgBytes != null && imgBytes.length > 0)
                    {
                        String base64 = Base64.getEncoder().encodeToString(imgBytes);
                        empresa.setImagenBanner(base64);
                    }
                    else
                    {
                        empresa.setImagenBanner(null);
                    }
                }
            }
        } 
        catch (Exception e) 
        {
            System.err.println("Error obteniendo empresa: " + e.getMessage());
        }
        return empresa;
    }
    
    public List<EmpresaDTO> listarTodas() 
    {
        List<EmpresaDTO> lista = new ArrayList<>();
        Connection conn = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Empresa"); ResultSet rs = ps.executeQuery()) 
        {
            while (rs.next()) 
            {
                EmpresaDTO emp = new EmpresaDTO();
                emp.setIdEmpresa(rs.getInt("id_empresa"));
                emp.setNombreEmpresa(rs.getString("nombre_empresa"));
                emp.setDescripcion(rs.getString("descripcion"));
                emp.setPorcentajeComisionEspecifica(rs.getFloat("porcentaje_comision_especifica"));
                emp.setEstado(rs.getString("estado"));
                byte[] imgBytes = rs.getBytes("imagen_banner");
                if (imgBytes != null && imgBytes.length > 0) 
                {
                    emp.setImagenBanner(Base64.getEncoder().encodeToString(imgBytes));
                }
                lista.add(emp);
            }
        }
        catch (Exception e)
        {
            System.err.println("Error listando empresas: " + e.getMessage());
        }
        return lista;
    }
    
    public boolean actualizarEmpresa(EmpresaDTO empresa) 
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        boolean actualizarImagen = (empresa.getImagenBanner() != null && !empresa.getImagenBanner().isEmpty());
        String conSinImagen;
        if (actualizarImagen) 
        {
            conSinImagen = "UPDATE Empresa SET nombre_empresa = ?, descripcion = ?, porcentaje_comision_especifica = ?, imagen_banner = ? WHERE id_empresa = ?";
        }
        else
        {
            conSinImagen = "UPDATE Empresa SET nombre_empresa = ?, descripcion = ?, porcentaje_comision_especifica = ? WHERE id_empresa = ?";
        }

        try (PreparedStatement ps = conn.prepareStatement(conSinImagen)) 
        {
            ps.setString(1, empresa.getNombreEmpresa());
            ps.setString(2, empresa.getDescripcion());
            if (empresa.getPorcentajeComisionEspecifica() > 0) 
            {
                ps.setFloat(3, empresa.getPorcentajeComisionEspecifica());
            }
            else
            {
                ps.setNull(3, Types.DECIMAL);
            }
            if (actualizarImagen)
            {
                byte[] imgBytes = Base64.getDecoder().decode(empresa.getImagenBanner());
                ps.setBytes(4, imgBytes);
                ps.setInt(5, empresa.getIdEmpresa());
            }
            else
            {
                ps.setInt(4, empresa.getIdEmpresa());
            }

            return ps.executeUpdate() > 0;
        } 
        catch (Exception e) 
        {
            System.err.println("Error actualizando empresa: " + e.getMessage());
            return false;
        }
    }
    
    public boolean cambiarEstado(int idEmpresa, String nuevoEstado) 
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("UPDATE Empresa SET estado = ? WHERE id_empresa = ?")) 
        {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idEmpresa);
            return ps.executeUpdate() > 0;
        } 
        catch (Exception e) 
        {
            System.err.println("Error cambiando estado empresa: " + e.getMessage());
            return false;
        }
    }
    
    public boolean esEmpresaSuspendida(int idUsuario) 
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT e.estado FROM Empresa e JOIN Usuario_Empresa ue ON e.id_empresa = ue.id_empresa WHERE ue.id_usuario = ?")) 
        {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) 
            {
                if (rs.next()) 
                {
                    String estado = rs.getString("estado");
                    return "SUSPENDIDO".equals(estado);
                }
            }
        } 
        catch (Exception e) 
        {
            System.err.println("Error verificando estado empresa: " + e.getMessage());
        }
        return false;
    }
    
    public List<ResultadoBusquedaDTO> buscarEmpresasPorNombre(String query)
    {
        List<ResultadoBusquedaDTO> resultados = new ArrayList<>();
        Connection conn = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT id_empresa, nombre_empresa, imagen_banner FROM Empresa WHERE nombre_empresa LIKE ? AND estado = 'ACTIVO' LIMIT 5"))
        {
            ps.setString(1, "%" + query + "%");
            try (ResultSet rs = ps.executeQuery()) 
            {
                while (rs.next())
                {
                    ResultadoBusquedaDTO dto = new ResultadoBusquedaDTO();
                    dto.setId(rs.getInt("id_empresa"));
                    dto.setNombre(rs.getString("nombre_empresa"));
                    dto.setTipo("EMPRESA");
                    byte[] imgBytes = rs.getBytes("imagen_banner");
                    if (imgBytes != null && imgBytes.length > 0)
                    {
                        dto.setImagen(Base64.getEncoder().encodeToString(imgBytes));
                    }
                    resultados.add(dto);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return resultados;
    }
}
