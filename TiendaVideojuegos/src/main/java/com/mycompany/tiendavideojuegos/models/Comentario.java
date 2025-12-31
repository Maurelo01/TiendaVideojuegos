package com.mycompany.tiendavideojuegos.models;

import com.mycompany.tiendavideojuegos.DTO.ComentarioDTO;
import com.mycompany.tiendavideojuegos.configuracion.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Comentario
{
    public boolean agregar(ComentarioDTO comentario)
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO Comentario (id_gamer, id_juego, texto, calificacion, fecha, id_comentario_principal) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, ?)"))
        {
            ps.setInt(1, comentario.getIdGamer());
            ps.setInt(2, comentario.getIdJuego());
            ps.setString(3, comentario.getTexto());
            ps.setInt(4, comentario.getCalificacion());
            if (comentario.getIdComentarioPrincipal() > 0)
            {
                ps.setInt(5, comentario.getIdComentarioPrincipal());
            }
            else
            {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            return ps.executeUpdate() > 0;
        }
        catch (Exception e)
        {
            System.err.println("Error agregando comentario: " + e.getMessage());
            return false;
        }
    }

    public List<ComentarioDTO> listarPorJuego(int idJuego)
    {
        List<ComentarioDTO> lista = new ArrayList<>();
        Connection conn = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT c.*, u.nickname FROM Comentario c JOIN Usuario_Comun_Gamer u ON c.id_gamer = u.id_usuario WHERE c.id_juego = ? ORDER BY c.fecha ASC"))
        {
            ps.setInt(1, idJuego);
            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    ComentarioDTO c = new ComentarioDTO();
                    c.setIdComentario(rs.getInt("id_comentario"));
                    c.setIdGamer(rs.getInt("id_gamer"));
                    c.setIdJuego(rs.getInt("id_juego"));
                    c.setNicknameGamer(rs.getString("nickname"));
                    c.setTexto(rs.getString("texto"));
                    c.setCalificacion(rs.getInt("calificacion"));
                    c.setFecha(rs.getTimestamp("fecha"));
                    c.setIdComentarioPrincipal(rs.getInt("id_comentario_principal"));
                    lista.add(c);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return lista;
    }
    
    // ... métodos agregar y listarPorJuego que ya tenías ...

    public boolean actualizar(ComentarioDTO comentario)
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("UPDATE Comentario SET texto = ?, calificacion = ? WHERE id_comentario = ? AND id_gamer = ?"))
        {
            ps.setString(1, comentario.getTexto());
            ps.setInt(2, comentario.getCalificacion());
            ps.setInt(3, comentario.getIdComentario());
            ps.setInt(4, comentario.getIdGamer()); 
            return ps.executeUpdate() > 0;
        }
        catch (Exception e)
        {
            System.err.println("Error actualizando comentario: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int idComentario, int idGamer)
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Comentario WHERE id_comentario = ? AND id_gamer = ?"))
        {
            ps.setInt(1, idComentario);
            ps.setInt(2, idGamer);
            return ps.executeUpdate() > 0;
        }
        catch (Exception e)
        {
            System.err.println("Error eliminando comentario: " + e.getMessage());
            return false;
        }
    }
}