package com.mycompany.tiendavideojuegos.models;

import com.mycompany.tiendavideojuegos.DTO.CategoriaDTO;
import com.mycompany.tiendavideojuegos.configuracion.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Categoria 
{
    public boolean crear(CategoriaDTO categoria) 
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        boolean exito = false;
        try 
        {
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO Categoria (nombre_categoria) VALUES (?)")) 
            {
                ps.setString(1, categoria.getNombreCategoria());
                int filas = ps.executeUpdate();
                if (filas > 0)
                {
                    exito = true;
                }
            }
        }
        catch (Exception e)
        {
            System.err.println("Error creando categoría: " + e.getMessage());
        }
        return exito;
    }
    
    public List<CategoriaDTO> listar() 
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        List<CategoriaDTO> lista = new ArrayList<>();
        try
        {
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Categoria"); ResultSet rs = ps.executeQuery()) 
            {
                while (rs.next())
                {
                    var categoria = new CategoriaDTO();
                    categoria.setIdCategoria(rs.getInt("id_categoria"));
                    categoria.setNombreCategoria(rs.getString("nombre_categoria"));
                    lista.add(categoria);
                }
            }
        }
        catch (Exception e)
        {
            System.err.println("Error listando categorías: " + e.getMessage());
        }
        return lista;
    }
    
    public boolean actualizar(CategoriaDTO categoria)
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        boolean exito = false;
        try
        {
            try (PreparedStatement ps = conn.prepareStatement("UPDATE Categoria SET nombre_categoria = ? WHERE id_categoria = ?")) 
            {
                ps.setString(1, categoria.getNombreCategoria());
                ps.setInt(2, categoria.getIdCategoria());
                int filas = ps.executeUpdate();
                if (filas > 0)
                {
                    exito = true;
                }
            }
        }
        catch (Exception e)
        {
            System.err.println("Error actualizando categoría: " + e.getMessage());
        }
        return exito;
    }
    
    public boolean eliminar(int idCategoria) 
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        boolean exito = false;
        try 
        {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Categoria WHERE id_categoria = ?")) 
            {
                ps.setInt(1, idCategoria);
                int filas = ps.executeUpdate();
                if (filas > 0)
                {
                    exito = true;
                }
            }
        }
        catch (Exception e) 
        {
            System.err.println("Error eliminando categoría: " + e.getMessage());
        }
        return exito;
    }
}