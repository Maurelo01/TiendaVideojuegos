package com.mycompany.tiendavideojuegos.services;

import com.mycompany.tiendavideojuegos.DTO.ReporteUsoFamiliarDTO;
import com.mycompany.tiendavideojuegos.configuracion.ConexionDB;
import com.mycompany.tiendavideojuegos.models.Prestamo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class PrestamosService 
{
    private final Prestamo prestamoModel = new Prestamo();
    
    public boolean solicitarPrestamo(int idUsuarioSolicitante, int idJuego, int idPropietario) throws Exception 
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        if (!compartenGrupo(conn, idUsuarioSolicitante, idPropietario)) 
        {
            throw new Exception("Error: No perteneces al mismo grupo familiar que el dueño.");
        }
        if (!estaJuegoDisponible(conn, idJuego, idPropietario)) 
        {
            throw new Exception("El juego no está disponible en este momento (alguien más lo está jugando).");
        }
        if (yaTieneJuegoPrestado(conn, idUsuarioSolicitante)) 
        {
            throw new Exception("Ya tienes un juego prestado instalado, debes devolverlo antes de solicitar otro.");
        }
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO Prestamo_Biblioteca (id_usuario_recibe, id_juego, id_usuario_propietario, estado_instalacion, fecha_prestamo) VALUES (?, ?, ?, 'INSTALADO', NOW())")) 
        {
            ps.setInt(1, idUsuarioSolicitante);
            ps.setInt(2, idJuego);
            ps.setInt(3, idPropietario);
            return ps.executeUpdate() > 0;
        }
    }

    private boolean compartenGrupo(Connection conn, int usuario1, int usuario2) 
    {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM Miembro_Grupo m1 JOIN Miembro_Grupo m2 ON m1.id_grupo = m2.id_grupo WHERE m1.id_usuario = ? AND m2.id_usuario = ?")) 
        {
            ps.setInt(1, usuario1);
            ps.setInt(2, usuario2);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } 
        catch (Exception e)
        {
            return false;
        }
    }

    private boolean estaJuegoDisponible(Connection conn, int idJuego, int idPropietario) 
    {
        try 
        {
            try (PreparedStatement ps = conn.prepareStatement("SELECT estado_instalacion FROM Biblioteca_Personal WHERE id_gamer = ? AND id_juego = ?")) 
            {
                ps.setInt(1, idPropietario);
                ps.setInt(2, idJuego);
                ResultSet rs = ps.executeQuery();
                if (rs.next() && "INSTALADO".equals(rs.getString("estado_instalacion"))) 
                {
                    return false;
                }
            }
            try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM Prestamo_Biblioteca WHERE id_juego = ? AND id_usuario_propietario = ? AND fecha_devolucion IS NULL")) 
            {
                ps.setInt(1, idJuego);
                ps.setInt(2, idPropietario);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) 
                {
                    return false;
                }
            }
            return true;
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean devolverPrestamo(int idUsuarioRecibe, int idJuego) throws Exception 
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("UPDATE Prestamo_Biblioteca SET fecha_devolucion = NOW(), estado_instalacion = 'NO_INSTALADO' WHERE id_usuario_recibe = ? AND id_juego = ? AND fecha_devolucion IS NULL")) 
        {
            ps.setInt(1, idUsuarioRecibe);
            ps.setInt(2, idJuego);
            return ps.executeUpdate() > 0;
        }
    }

    private boolean yaTieneJuegoPrestado(Connection conn, int idUsuarioSolicitante) 
    {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM Prestamo_Biblioteca WHERE id_usuario_recibe = ? AND fecha_devolucion IS NULL")) 
        {
            ps.setInt(1, idUsuarioSolicitante);
            try (ResultSet rs = ps.executeQuery()) 
            {
                return rs.next();
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return true;
        }
    }
    
    public List<ReporteUsoFamiliarDTO> generarReporteUso(int idUsuario) throws Exception
    {
        if (idUsuario <= 0) throw new Exception("Id usuario inválido");
        return prestamoModel.obtenerReporteUso(idUsuario);
    }
}