package com.mycompany.tiendavideojuegos.services;

import com.mycompany.tiendavideojuegos.DTO.SolicitudCompra;
import com.mycompany.tiendavideojuegos.configuracion.ConexionDB;
import com.mycompany.tiendavideojuegos.models.Ventas;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class VentasService
{
    private final Ventas modelo = new Ventas();

    public boolean procesarCompra(SolicitudCompra solicitud) throws Exception
    {
        if (solicitud == null)
        {
            throw new Exception("Solicitud inválida.");
        }
        if (solicitud.getIdUsuario() <= 0 || solicitud.getIdJuego() <= 0)
        {
            throw new Exception("Datos de usuario o juego incorrectos.");
        }
        return modelo.procesarCompra(solicitud);
    }
    
    public boolean verificarJuegoEnBiblioteca(int idUsuario, int idJuego) throws Exception
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM Biblioteca_Personal WHERE id_gamer = ? AND id_juego = ?"))
        {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idJuego);
            try (ResultSet rs = ps.executeQuery())
            {
                return rs.next();
            }
        }
    }
}
