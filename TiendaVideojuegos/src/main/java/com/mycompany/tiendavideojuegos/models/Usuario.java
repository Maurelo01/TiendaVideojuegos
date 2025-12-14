package com.mycompany.tiendavideojuegos.models;

import com.mycompany.tiendavideojuegos.DTO.UsuarioComunGamerDTO;
import com.mycompany.tiendavideojuegos.DTO.UsuarioDTO;
import com.mycompany.tiendavideojuegos.configuracion.ConexionDB;
import java.sql.Connection;
import java.sql.ResultSet;

public class Usuario 
{
    protected int idUsuario;
    protected String correo;
    protected String contraseña;
    protected String rol;
    
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
    
    private UsuarioComunGamerDTO obtenerGamerCompleto(Connection conn, int idUser, ResultSet rsPadre) throws Exception 
    {
        UsuarioComunGamerDTO gamer = new UsuarioComunGamerDTO();
        llenarDatos(gamer, rsPadre);
        var ps = conn.prepareStatement("SELECT * FROM Usuario_Comun_Gamer WHERE id_usuario = ?");
        try (ps) 
        {
            ps.setInt(1, idUser);
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
}
