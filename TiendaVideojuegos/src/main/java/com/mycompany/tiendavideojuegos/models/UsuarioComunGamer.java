package com.mycompany.tiendavideojuegos.models;

import com.mycompany.tiendavideojuegos.DTO.UsuarioComunGamerDTO;
import com.mycompany.tiendavideojuegos.configuracion.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;

public class UsuarioComunGamer
{
    public UsuarioComunGamerDTO registrarGamer(UsuarioComunGamerDTO gamerNuevo)
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        int idGenerado = -1;
        try
        {
            try (PreparedStatement psUsuario = conn.prepareStatement("INSERT INTO Usuario (correo, contraseña, rol) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) 
            {
                psUsuario.setString(1, gamerNuevo.getCorreo());
                psUsuario.setString(2, gamerNuevo.getContraseña());
                psUsuario.setString(3, "GAMER");
                psUsuario.executeUpdate();
                try (ResultSet rsIds = psUsuario.getGeneratedKeys()) 
                {
                    if (rsIds.next())
                    {
                        idGenerado = rsIds.getInt(1);
                        gamerNuevo.setIdUsuario(idGenerado);
                    }
                }
            }
            if (idGenerado != -1) 
            {
                try
                {
                    try (PreparedStatement psGamer = conn.prepareStatement("INSERT INTO Usuario_Comun_Gamer (id_usuario, nickname, fecha_nacimiento, telefono, pais, saldo_cartera) VALUES (?, ?, ?, ?, ?, ?)")) 
                    {
                        psGamer.setInt(1, idGenerado);
                        psGamer.setString(2, gamerNuevo.getNickname());
                        psGamer.setDate(3, gamerNuevo.getFechaNacimiento());
                        psGamer.setString(4, gamerNuevo.getTelefono());
                        psGamer.setString(5, gamerNuevo.getPais());
                        psGamer.setFloat(6, (float) 0.00);
                        psGamer.executeUpdate();
                    }
                }
                catch (Exception e) 
                {
                    System.err.println("Error creando usuario gamer, Borrando usuario sin tabla padre: " + idGenerado);
                    try (var psDel = conn.prepareStatement("DELETE FROM Usuario WHERE id_usuario = ?")) 
                    {
                        psDel.setInt(1, idGenerado);
                        psDel.executeUpdate();
                    }
                    return null;
                }
            } 
            else 
            {
                return null;
            }
        } 
        catch (Exception e) 
        {
            System.err.println("Error en registro: " + e.getMessage());
            return null;
        }
        return gamerNuevo;
    }
    
    public UsuarioComunGamerDTO obtenerPorId(int idUsuario) 
    {
        UsuarioComunGamerDTO gamer = null;
        Connection conn = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT u.id_usuario, u.correo, u.rol, u.fecha_registro, g.nickname, g.fecha_nacimiento, g.telefono, g.pais, g.saldo_cartera, g.avatar FROM Usuario u " + 
                "JOIN Usuario_Comun_Gamer g ON u.id_usuario = g.id_usuario WHERE u.id_usuario = ?")) 
        {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) 
            {
                if (rs.next()) 
                {
                    gamer = new UsuarioComunGamerDTO();
                    gamer.setIdUsuario(rs.getInt("id_usuario"));
                    gamer.setCorreo(rs.getString("correo"));
                    gamer.setRol(rs.getString("rol"));
                    gamer.setFechaRegistro(rs.getTimestamp("fecha_registro"));
                    gamer.setNickname(rs.getString("nickname"));
                    gamer.setFechaNacimiento(rs.getDate("fecha_nacimiento"));
                    gamer.setTelefono(rs.getString("telefono"));
                    gamer.setPais(rs.getString("pais"));
                    gamer.setSaldoCartera(rs.getFloat("saldo_cartera"));
                    byte[] imgBytes = rs.getBytes("avatar");
                    if (imgBytes != null && imgBytes.length > 0) 
                    {
                        String base64 = Base64.getEncoder().encodeToString(imgBytes);
                        gamer.setAvatar(base64);
                    }
                }
            }
        } 
        catch (Exception e) 
        {
            System.err.println("Error obteniendo gamer: " + e.getMessage());
        }
        return gamer;
    }
    
    public boolean actualizarPerfil(UsuarioComunGamerDTO gamer) 
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        boolean exito = false;
        try 
        {
            try (PreparedStatement psGamer = conn.prepareStatement("UPDATE Usuario_Comun_Gamer SET nickname = ?, telefono = ?, pais = ? WHERE id_usuario = ?")) 
            {
                psGamer.setString(1, gamer.getNickname());
                psGamer.setString(2, gamer.getTelefono());
                psGamer.setString(3, gamer.getPais());
                psGamer.setInt(4, gamer.getIdUsuario());
                psGamer.executeUpdate();
            }
            if (gamer.getAvatar() != null && !gamer.getAvatar().isEmpty()) 
            {
                try (PreparedStatement psAvatar = conn.prepareStatement("UPDATE Usuario_Comun_Gamer SET avatar = ? WHERE id_usuario = ?")) 
                {
                    byte[] imagenBytes = Base64.getDecoder().decode(gamer.getAvatar());
                    psAvatar.setBytes(1, imagenBytes); 
                    psAvatar.setInt(2, gamer.getIdUsuario());
                    psAvatar.executeUpdate();
                }
            }
            if (gamer.getContraseña() != null && !gamer.getContraseña().isEmpty()) 
            {
                try (PreparedStatement psUser = conn.prepareStatement("UPDATE Usuario SET contraseña = ? WHERE id_usuario = ?")) 
                {
                    psUser.setString(1, gamer.getContraseña());
                    psUser.setInt(2, gamer.getIdUsuario());
                    psUser.executeUpdate();
                }
            }
            exito = true;
        } 
        catch (Exception e) 
        {
            System.err.println("Error actualizando perfil gamer: " + e.getMessage());
            exito = false;
        } 
        return exito;
    }
    
    public boolean sumarSaldo(int idUsuario, float monto) 
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("UPDATE Usuario_Comun_Gamer SET saldo_cartera = saldo_cartera + ? WHERE id_usuario = ?")) 
        {
            ps.setFloat(1, monto);
            ps.setInt(2, idUsuario);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } 
        catch (SQLException e) 
        {
            System.err.println("Error recargando saldo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public float obtenerSaldo(int idUsuario) 
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT saldo_cartera FROM Usuario_Comun_Gamer WHERE id_usuario = ?")) 
        {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) 
            {
                if (rs.next()) 
                {
                    return rs.getFloat("saldo_cartera");
                }
            }
        } 
        catch (SQLException e) 
        {
            System.err.println("Error obteniendo saldo: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0f;
    }
}
