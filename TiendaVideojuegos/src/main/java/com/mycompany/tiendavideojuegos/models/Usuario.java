package com.mycompany.tiendavideojuegos.models;

import com.mycompany.tiendavideojuegos.DTO.UsuarioComunGamerDTO;
import com.mycompany.tiendavideojuegos.DTO.UsuarioDTO;
import com.mycompany.tiendavideojuegos.DTO.UsuarioEmpresaDTO;
import com.mycompany.tiendavideojuegos.configuracion.ConexionDB;
import java.sql.Connection;
import java.sql.ResultSet;

public class Usuario 
{
    protected int idUsuario;
    protected String correo;
    protected String contraseña;
    protected String rol;
    
    public Usuario()
    {
    }
    
    public Usuario(String correo, String contraseña, String rol) 
    {
        this.correo = correo;
        this.contraseña = contraseña;
        this.rol = rol;
    }
    
    public UsuarioDTO login(String correo, String contraseña)
    {
        ConexionDB connMySQL = new ConexionDB();
        var conn = connMySQL.conectar();
        UsuarioDTO usuarioDTO = null;
        try
        {
            var ps = conn.prepareStatement("SELECT * FROM Usuario WHERE correo = ? AND contraseña = ?");
            ps.setString(1, correo);
            ps.setString(2, contraseña);
            var rs = ps.executeQuery();
                if (rs.next())
                {
                    int idUsuario = rs.getInt("id_usuario");
                    String rol = rs.getString("rol");
                    if ("GAMER".equals(rol))
                    {
                        usuarioDTO = obtenerGamerCompleto(conn, idUsuario, rs);
                    }
                    else if ("EMPRESA".equals(rol))
                    {
                        usuarioDTO = obtenerEmpresaCompleto(conn, idUsuario, rs);
                    }
                    else
                    {
                        usuarioDTO = new UsuarioDTO();
                        llenarDatos(usuarioDTO, rs);
                    }
                }
            rs.close();
            ps.close();
        }
        catch (Exception e)
        {
            System.err.println("Error en login: " + e.getMessage());
        }
        finally 
        {
            connMySQL.desconectar(conn);
        }
        return usuarioDTO;
    }
    
    private void llenarDatos(UsuarioDTO dto, ResultSet rs) throws Exception 
    {
        dto.setIdUsuario(rs.getInt("id_usuario"));
        dto.setCorreo(rs.getString("correo"));
        dto.setRol(rs.getString("rol"));
        dto.setFechaRegistro(rs.getTimestamp("fecha_registro"));
    }
    
    private UsuarioComunGamerDTO obtenerGamerCompleto(Connection conn, int idUsuario, ResultSet rsPadre) throws Exception 
    {
        UsuarioComunGamerDTO gamer = new UsuarioComunGamerDTO();
        llenarDatos(gamer, rsPadre);
        var ps = conn.prepareStatement("SELECT * FROM Usuario_Comun_Gamer WHERE id_usuario = ?");
        try (ps) 
        {
            ps.setInt(1, idUsuario);
            var rs = ps.executeQuery();
            if (rs.next()) 
            {
                gamer.setNickname(rs.getString("nickname"));
                gamer.setFechaNacimiento(rs.getDate("fecha_nacimiento"));
                gamer.setTelefono(rs.getString("telefono"));
                gamer.setPais(rs.getString("pais"));
                gamer.setSaldoCartera(rs.getFloat("saldo_cartera"));
            }
        }
        return gamer;
    }
    
    private UsuarioEmpresaDTO obtenerEmpresaCompleto(java.sql.Connection conn, int idUsuario, ResultSet rsPadre) throws Exception 
    {
        var empresa = new UsuarioEmpresaDTO();
        llenarDatos(empresa, rsPadre);
        var ps = conn.prepareStatement("SELECT ue.*, e.nombre_empresa FROM Usuario_Empresa ue JOIN Empresa e ON ue.id_empresa = e.id_empresa WHERE ue.id_usuario = ?");
        try (ps)
        {
            ps.setInt(1, idUsuario);
            var rs = ps.executeQuery();
            if (rs.next())
            {
                empresa.setIdEmpresa(rs.getInt("id_empresa"));
                empresa.setNombreEmpleado(rs.getString("nombre_empleado"));
                empresa.setFechaNacimiento(rs.getDate("fecha_nacimiento_empleado"));
                empresa.setNombreEmpresaAux(rs.getString("nombre_empresa"));
            }
            rs.close();
            ps.close();
        }
        return empresa;
    }
}
