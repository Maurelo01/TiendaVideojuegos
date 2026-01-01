package com.mycompany.tiendavideojuegos.models;

import com.mycompany.tiendavideojuegos.DTO.ReporteUsoFamiliarDTO;
import com.mycompany.tiendavideojuegos.configuracion.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Prestamo 
{
    private int idPrestamo;
    private int idUsuarioRecibe;
    private int idJuego;
    private int idUsuarioPropietario;
    private String estadoInstalacion;
    private Timestamp fechaPrestamo;
    private Timestamp fechaDevolucion;

    public int getIdPrestamo()
    {
        return idPrestamo;
    }

    public void setIdPrestamo(int idPrestamo)
    {
        this.idPrestamo = idPrestamo;
    }

    public int getIdUsuarioRecibe()
    {
        return idUsuarioRecibe;
    }

    public void setIdUsuarioRecibe(int idUsuarioRecibe)
    {
        this.idUsuarioRecibe = idUsuarioRecibe;
    }

    public int getIdJuego()
    {
        return idJuego;
    }

    public void setIdJuego(int idJuego)
    {
        this.idJuego = idJuego;
    }

    public int getIdUsuarioPropietario()
    {
        return idUsuarioPropietario;
    }

    public void setIdUsuarioPropietario(int idUsuarioPropietario) {
        this.idUsuarioPropietario = idUsuarioPropietario;
    }

    public String getEstadoInstalacion()
    {
        return estadoInstalacion;
    }

    public void setEstadoInstalacion(String estadoInstalacion)
    {
        this.estadoInstalacion = estadoInstalacion;
    }

    public Timestamp getFechaPrestamo()
    {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(Timestamp fechaPrestamo)
    {
        this.fechaPrestamo = fechaPrestamo;
    }

    public Timestamp getFechaDevolucion()
    {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(Timestamp fechaDevolucion)
    {
        this.fechaDevolucion = fechaDevolucion;
    }
    
    public List<ReporteUsoFamiliarDTO> obtenerReporteUso(int idUsuario) 
    {
        List<ReporteUsoFamiliarDTO> lista = new ArrayList<>();
        Connection conn = ConexionDB.getInstance().getConnection();
        String historialSql = "SELECT v.titulo, u_dueno.nickname, " +
                     "COUNT(p.id_prestamo) as veces_prestado, " +
                     "(SELECT COALESCE(AVG(c.calificacion), 0) FROM Comentario c WHERE c.id_juego = v.id_juego AND c.id_comentario_principal IS NULL) as promedio_global " +
                     "FROM Prestamo_Biblioteca p " +
                     "JOIN Videojuego v ON p.id_juego = v.id_juego " +
                     "JOIN Usuario_Comun_Gamer u_dueno ON p.id_usuario_propietario = u_dueno.id_usuario " +
                     "WHERE p.id_usuario_recibe = ? " +
                     "GROUP BY v.id_juego, v.titulo, u_dueno.nickname " +
                     "ORDER BY veces_prestado DESC";
        try (PreparedStatement ps = conn.prepareStatement(historialSql)) 
        {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) 
            {
                while (rs.next()) 
                {
                    ReporteUsoFamiliarDTO dto = new ReporteUsoFamiliarDTO();
                    dto.setTituloJuego(rs.getString("titulo"));
                    dto.setNombrePropietario(rs.getString("nickname"));
                    dto.setVecesInstalado(rs.getInt("veces_prestado"));
                    dto.setPromedioComunidad(rs.getFloat("promedio_global"));
                    lista.add(dto);
                }
            }
        } 
        catch (Exception e) 
        {
            System.err.println("Error reporte uso familiar: " + e.getMessage());
        }
        return lista;
    }
}
