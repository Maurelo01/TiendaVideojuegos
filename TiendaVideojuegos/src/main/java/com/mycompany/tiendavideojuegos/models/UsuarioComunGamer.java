package com.mycompany.tiendavideojuegos.models;

import com.mycompany.tiendavideojuegos.DTO.UsuarioComunGamerDTO;
import com.mycompany.tiendavideojuegos.configuracion.ConexionDB;
import java.sql.Connection;
import java.sql.Statement;

public class UsuarioComunGamer
{
    public UsuarioComunGamerDTO registrarGamer(UsuarioComunGamerDTO gamerNuevo)
    {
        ConexionDB connMySQL = new ConexionDB();
        Connection conn = connMySQL.conectar();
        int idGenerado = -1;
        try
        {
            var psUsuario = conn.prepareStatement("INSERT INTO Usuario (correo, contraseña, rol) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            psUsuario.setString(1, gamerNuevo.getCorreo());
            psUsuario.setString(2, gamerNuevo.getContraseña());
            psUsuario.setString(3, "GAMER"); 
            psUsuario.executeUpdate();
            var rsIds = psUsuario.getGeneratedKeys();
            if (rsIds.next()) 
            {
                idGenerado = rsIds.getInt(1);
                gamerNuevo.setIdUsuario(idGenerado);
            }
            rsIds.close();
            psUsuario.close();
            if (idGenerado != -1) 
            {
                try
                {
                    var psGamer = conn.prepareStatement("INSERT INTO Usuario_Comun_Gamer (id_usuario, nickname, fecha_nacimiento, telefono, pais, saldo_cartera) VALUES (?, ?, ?, ?, ?, ?)");
                    psGamer.setInt(1, idGenerado);
                    psGamer.setString(2, gamerNuevo.getNickname());
                    psGamer.setDate(3, gamerNuevo.getFechaNacimiento());
                    psGamer.setString(4, gamerNuevo.getTelefono());
                    psGamer.setString(5, gamerNuevo.getPais());
                    psGamer.setFloat(6, (float) 0.00);
                    psGamer.executeUpdate();
                    psGamer.close();
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
        finally 
        {
            connMySQL.desconectar(conn);
        }
        return gamerNuevo;
    }
}
