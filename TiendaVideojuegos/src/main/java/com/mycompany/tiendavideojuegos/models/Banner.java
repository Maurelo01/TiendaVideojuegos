package com.mycompany.tiendavideojuegos.models;

import com.mycompany.tiendavideojuegos.DTO.BannerDTO;
import com.mycompany.tiendavideojuegos.configuracion.ConexionDB;
import java.util.ArrayList;
import java.util.List;

public class Banner 
{
    public boolean agregar(BannerDTO banner) 
    {
        ConexionDB connMySQL = new ConexionDB();
        var conn = connMySQL.conectar();
        boolean exito = false;
        try 
        {
            var ps = conn.prepareStatement("INSERT INTO Banner_Principal (id_juego, titulo, imagen_url, lugar, estado) VALUES (?, ?, ?, ?, ?)");
            ps.setInt(1, banner.getIdJuego());
            ps.setString(2, banner.getTitulo());
            ps.setString(3, banner.getImagenUrl());
            ps.setInt(4, banner.getLugar());
            ps.setString(5, "ACTIVO");
            int filas = ps.executeUpdate();
            if (filas > 0) exito = true;
            ps.close();
        }
        catch (Exception e) 
        {
            System.err.println("Error agregando banner: " + e.getMessage());
        }
        finally 
        {
            connMySQL.desconectar(conn);
        }
        return exito;
    }
    
    public boolean eliminar(int idBanner) 
    {
        ConexionDB connMySQL = new ConexionDB();
        var conn = connMySQL.conectar();
        boolean exito = false;
        try 
        {
            var ps = conn.prepareStatement("DELETE FROM Banner_Principal WHERE id_banner = ?");
            ps.setInt(1, idBanner);
            if (ps.executeUpdate() > 0)
            {
                exito = true;
            }
            ps.close();
        }
        catch (Exception e) 
        {
            System.err.println("Error eliminando banner: " + e.getMessage());
        }
        finally 
        {
            connMySQL.desconectar(conn);
        }
        return exito;
    }
    
    public List<BannerDTO> listarBannersActivos()
    {
       ConexionDB connMySQL = new ConexionDB();
        var conn = connMySQL.conectar();
        List<BannerDTO> lista = new ArrayList<>();
        try 
        {
            var ps = conn.prepareStatement("SELECT bp.*, v.titulo as nombre_juego FROM Banner_Principal bp JOIN Videojuego v ON bp.id_juego = v.id_juego WHERE bp.estado = 'ACTIVO' ORDER BY bp.lugar ASC");
            var rs = ps.executeQuery();
            while (rs.next()) 
            {
                var banner = new BannerDTO();
                banner.setIdBanner(rs.getInt("id_banner"));
                banner.setIdJuego(rs.getInt("id_juego"));
                banner.setTitulo(rs.getString("titulo"));
                banner.setImagenUrl(rs.getString("imagen_url"));
                banner.setLugar(rs.getInt("lugar"));
                banner.setEstado(rs.getString("estado"));
                banner.setNombreJuego(rs.getString("nombre_juego"));
                lista.add(banner);
            }
            rs.close();
            ps.close();
        }
        catch (Exception e) 
        {
            System.err.println("Error listando banners: " + e.getMessage());
        } 
        finally 
        {
            connMySQL.desconectar(conn);
        }
        return lista; 
    }
}
