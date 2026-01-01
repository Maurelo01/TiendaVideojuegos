package com.mycompany.tiendavideojuegos.models;

import com.mycompany.tiendavideojuegos.DTO.ComentarioDTO;
import com.mycompany.tiendavideojuegos.DTO.JuegoCalificacionDTO;
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
        try (PreparedStatement ps = conn.prepareStatement("SELECT c.*, u.nickname, c.oculto FROM Comentario c JOIN Usuario_Comun_Gamer u ON c.id_gamer = u.id_usuario WHERE c.id_juego = ? ORDER BY c.fecha ASC"))
        {
            ps.setInt(1, idJuego);
            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    ComentarioDTO comentario = new ComentarioDTO();
                    comentario.setIdComentario(rs.getInt("id_comentario"));
                    comentario.setIdGamer(rs.getInt("id_gamer"));
                    comentario.setIdJuego(rs.getInt("id_juego"));
                    comentario.setNicknameGamer(rs.getString("nickname"));
                    comentario.setTexto(rs.getString("texto"));
                    comentario.setCalificacion(rs.getInt("calificacion"));
                    comentario.setFecha(rs.getTimestamp("fecha"));
                    comentario.setIdComentarioPrincipal(rs.getInt("id_comentario_principal"));
                    comentario.setOculto(rs.getBoolean("oculto"));
                    lista.add(comentario);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return lista;
    }
    
    public boolean cambiarVisibilidad(int idComentario, boolean ocultar)
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("UPDATE Comentario SET oculto = ? WHERE id_comentario = ?"))
        {
            ps.setBoolean(1, ocultar);
            ps.setInt(2, idComentario);
            return ps.executeUpdate() > 0;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean esModeradorDelComentario(int idComentario, int idUsuarioSolicitante)
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        String verificarUsuarioSql = 
            "SELECT 1 FROM Comentario c " +
            "JOIN Videojuego v ON c.id_juego = v.id_juego " +
            "LEFT JOIN Usuario_Empresa ue ON v.id_empresa = ue.id_empresa " +
            "LEFT JOIN Usuario u ON u.id_usuario = ? " +
            "WHERE c.id_comentario = ? " +
            "AND (ue.id_usuario = ? OR u.rol = 'ADMIN') " +
            "LIMIT 1";
            
        try (PreparedStatement ps = conn.prepareStatement(verificarUsuarioSql))
        {
            ps.setInt(1, idUsuarioSolicitante); // Para verificar admin
            ps.setInt(2, idComentario);
            ps.setInt(3, idUsuarioSolicitante); // Para verificar empresa
            try (ResultSet rs = ps.executeQuery())
            {
                return rs.next();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
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
    
    public List<JuegoCalificacionDTO> obtenerPromediosPorEmpresa(int idEmpresa) 
    {
        List<JuegoCalificacionDTO> lista = new ArrayList<>();
        Connection conn = ConexionDB.getInstance().getConnection();
        String promedioSql = "SELECT v.titulo, COALESCE(AVG(c.calificacion), 0) as promedio, COUNT(c.id_comentario) as total " +
                     "FROM Videojuego v " +
                     "LEFT JOIN Comentario c ON v.id_juego = c.id_juego AND c.id_comentario_principal IS NULL " +
                     "WHERE v.id_empresa = ? AND v.estado = 'ACTIVO' " +
                     "GROUP BY v.id_juego, v.titulo " +
                     "ORDER BY promedio DESC";
        try (PreparedStatement ps = conn.prepareStatement(promedioSql)) 
        {
            ps.setInt(1, idEmpresa);
            try (ResultSet rs = ps.executeQuery()) 
            {
                while (rs.next()) 
                {
                    lista.add(new JuegoCalificacionDTO(rs.getString("titulo"), rs.getDouble("promedio"), rs.getInt("total")));
                }
            }
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return lista;
    }
    
    public List<ComentarioDTO> obtenerTopComentados(int idEmpresa) 
    {
        List<ComentarioDTO> lista = new ArrayList<>();
        Connection conn = ConexionDB.getInstance().getConnection();
        String masRespuestasSql = "SELECT cPadre.id_comentario, cPadre.texto, u.nickname, v.titulo, COUNT(cHijo.id_comentario) as respuestas " +
                     "FROM Comentario cPadre " +
                     "JOIN Videojuego v ON cPadre.id_juego = v.id_juego " +
                     "JOIN Usuario_Comun_Gamer u ON cPadre.id_gamer = u.id_usuario " +
                     "JOIN Comentario cHijo ON cPadre.id_comentario = cHijo.id_comentario_principal " +
                     "WHERE v.id_empresa = ? " +
                     "GROUP BY cPadre.id_comentario, cPadre.texto, u.nickname, v.titulo " +
                     "ORDER BY respuestas DESC LIMIT 5";

        try (PreparedStatement ps = conn.prepareStatement(masRespuestasSql)) 
        {
            ps.setInt(1, idEmpresa);
            try (ResultSet rs = ps.executeQuery()) 
            {
                while (rs.next()) 
                {
                    ComentarioDTO dto = new ComentarioDTO();
                    dto.setIdComentario(rs.getInt("id_comentario"));
                    dto.setTexto(rs.getString("texto"));
                    dto.setNicknameGamer(rs.getString("nickname") + " (en " + rs.getString("titulo") + ")");
                    dto.setCalificacion(rs.getInt("respuestas")); // Temporal para guardar el conteo de respuestas
                    lista.add(dto);
                }
            }
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return lista;
    }
    
    public List<ComentarioDTO> obtenerPeoresComentarios(int idEmpresa) 
    {
        List<ComentarioDTO> lista = new ArrayList<>();
        Connection conn = ConexionDB.getInstance().getConnection();
        String peoresSql = "SELECT c.id_comentario, c.texto, c.calificacion, v.titulo, u.nickname " +
                     "FROM Comentario c " +
                     "JOIN Videojuego v ON c.id_juego = v.id_juego " +
                     "JOIN Usuario_Comun_Gamer u ON c.id_gamer = u.id_usuario " +
                     "WHERE v.id_empresa = ? " +
                     "AND c.calificacion <= 2 " +
                     "AND c.id_comentario_principal IS NULL " +
                     "ORDER BY c.fecha DESC LIMIT 10";

        try (PreparedStatement ps = conn.prepareStatement(peoresSql)) 
        {
            ps.setInt(1, idEmpresa);
            try (ResultSet rs = ps.executeQuery()) 
            {
                while (rs.next()) 
                {
                    ComentarioDTO dto = new ComentarioDTO();
                    dto.setIdComentario(rs.getInt("id_comentario"));
                    dto.setTexto(rs.getString("texto"));
                    dto.setCalificacion(rs.getInt("calificacion"));
                    dto.setNicknameGamer(rs.getString("nickname") + " - " + rs.getString("titulo"));
                    lista.add(dto);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return lista;
    }
}