package com.mycompany.tiendavideojuegos.models;

import com.mycompany.tiendavideojuegos.DTO.BannerDTO;
import com.mycompany.tiendavideojuegos.configuracion.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;

public class Banner 
{
    public boolean agregar(BannerDTO banner) 
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        boolean exito = false;
        try 
        {
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO Banner_Principal (id_juego, titulo, imagen, lugar, estado) VALUES (?, ?, ?, ?, ?)")) 
            {
                ps.setInt(1, banner.getIdJuego());
                ps.setString(2, banner.getTitulo());
                if (banner.getImagenUrl() != null && !banner.getImagenUrl().isEmpty())
                {
                    byte[] imagenBytes = Base64.getDecoder().decode(banner.getImagenUrl());
                    ps.setBytes(3, imagenBytes);
                }
                else
                {
                    ps.setNull(3, java.sql.Types.BLOB);
                }
                ps.setInt(4, banner.getLugar());
                ps.setString(5, "ACTIVO");
                int filas = ps.executeUpdate();
                if (filas > 0) exito = true;
            }
        }
        catch (Exception e) 
        {
            System.err.println("Error agregando banner: " + e.getMessage());
        }
        return exito;
    }
    
    public boolean eliminar(int idBanner) 
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        boolean exito = false;
        try 
        {
            try (java.sql.PreparedStatement ps = conn.prepareStatement("DELETE FROM Banner_Principal WHERE id_banner = ?")) 
            {
                ps.setInt(1, idBanner);
                if (ps.executeUpdate() > 0)
                {
                    exito = true;
                }
            }
        }
        catch (Exception e) 
        {
            System.err.println("Error eliminando banner: " + e.getMessage());
        }
        return exito;
    }
    
    public List<BannerDTO> listarBannersActivos()
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        List<BannerDTO> lista = new ArrayList<>();
        try 
        {
            try (PreparedStatement ps = conn.prepareStatement("SELECT bp.id_banner, bp.id_juego, bp.titulo, bp.imagen, bp.lugar, bp.estado, v.titulo as nombre_juego FROM Banner_Principal bp JOIN Videojuego v ON bp.id_juego = v.id_juego WHERE bp.estado = 'ACTIVO' ORDER BY bp.lugar ASC"); ResultSet rs = ps.executeQuery()) 
            {
                while (rs.next())
                {
                    var banner = new BannerDTO();
                    banner.setIdBanner(rs.getInt("id_banner"));
                    banner.setIdJuego(rs.getInt("id_juego"));
                    banner.setTitulo(rs.getString("titulo"));
                    byte[] imgBytes = rs.getBytes("imagen");
                    if (imgBytes != null && imgBytes.length > 0) 
                    {
                        String base64 = Base64.getEncoder().encodeToString(imgBytes);
                        banner.setImagenUrl(base64);
                    }
                    banner.setLugar(rs.getInt("lugar"));
                    banner.setEstado(rs.getString("estado"));
                    banner.setNombreJuego(rs.getString("nombre_juego"));
                    lista.add(banner);
                }
            }
        }
        catch (Exception e) 
        {
            System.err.println("Error listando banners: " + e.getMessage());
        }
        return lista; 
    }
    
    public boolean existeLugar(int lugar, int idBannerExcluir) 
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM Banner_Principal WHERE lugar = ? AND id_banner != ? AND estado = 'ACTIVO'")) 
        {
            ps.setInt(1, lugar);
            ps.setInt(2, idBannerExcluir);
            try (ResultSet rs = ps.executeQuery()) 
            {
                if (rs.next()) 
                {
                    return rs.getInt(1) > 0;
                }
            }
        } 
        catch (Exception e) 
        {
            System.err.println("Error verificando lugar: " + e.getMessage());
        }
        return false;
    }

    public boolean actualizar(BannerDTO banner) 
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        boolean exito = false;
        try 
        {
            String sql = "UPDATE Banner_Principal SET id_juego=?, titulo=?, lugar=? WHERE id_banner=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) 
            {
                ps.setInt(1, banner.getIdJuego());
                ps.setString(2, banner.getTitulo());
                ps.setInt(3, banner.getLugar());
                ps.setInt(4, banner.getIdBanner());
                ps.executeUpdate();
            }
            if (banner.getImagenUrl() != null && !banner.getImagenUrl().isEmpty()) 
            {
                try (PreparedStatement psImg = conn.prepareStatement("UPDATE Banner_Principal SET imagen=? WHERE id_banner=?")) 
                {
                    byte[] imagenBytes = Base64.getDecoder().decode(banner.getImagenUrl());
                    psImg.setBytes(1, imagenBytes);
                    psImg.setInt(2, banner.getIdBanner());
                    psImg.executeUpdate();
                }
            }
            exito = true;
        } 
        catch (Exception e) 
        {
            System.err.println("Error actualizando banner: " + e.getMessage());
        }
        return exito;
    }
}
