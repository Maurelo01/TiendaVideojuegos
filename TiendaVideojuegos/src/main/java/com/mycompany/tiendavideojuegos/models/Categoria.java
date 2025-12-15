package com.mycompany.tiendavideojuegos.models;

import com.mycompany.tiendavideojuegos.DTO.CategoriaDTO;
import com.mycompany.tiendavideojuegos.configuracion.ConexionDB;
import java.util.ArrayList;
import java.util.List;

public class Categoria 
{
    public boolean crear(CategoriaDTO categoria) 
    {
        ConexionDB connMySQL = new ConexionDB();
        var conn = connMySQL.conectar();
        boolean exito = false;
        try 
        {
            var ps = conn.prepareStatement("INSERT INTO Categoria (nombre_categoria) VALUES (?)");
            ps.setString(1, categoria.getNombreCategoria());
            int filas = ps.executeUpdate();
            if (filas > 0) 
            {
                exito = true;
            }
            ps.close();
        }
        catch (Exception e)
        {
            System.err.println("Error creando categoría: " + e.getMessage());
        }
        finally
        {
            connMySQL.desconectar(conn);
        }
        return exito;
    }
    
    public List<CategoriaDTO> listar() 
    {
        ConexionDB connMySQL = new ConexionDB();
        var conn = connMySQL.conectar();
        List<CategoriaDTO> lista = new ArrayList<>();
        try
        {
            var ps = conn.prepareStatement("SELECT * FROM Categoria");
            var rs = ps.executeQuery();
            while (rs.next())
            {
                var categoria = new CategoriaDTO();
                categoria.setIdCategoria(rs.getInt("id_categoria"));
                categoria.setNombreCategoria(rs.getString("nombre_categoria"));
                lista.add(categoria);
            }
            rs.close();
            ps.close();
        }
        catch (Exception e)
        {
            System.err.println("Error listando categorías: " + e.getMessage());
        }
        finally
        {
            connMySQL.desconectar(conn);
        }
        return lista;
    }
    
    public boolean actualizar(CategoriaDTO categoria)
    {
        ConexionDB connMySQL = new ConexionDB();
        var conn = connMySQL.conectar();
        boolean exito = false;
        try
        {
            var ps = conn.prepareStatement("UPDATE Categoria SET nombre_categoria = ? WHERE id_categoria = ?");
            ps.setString(1, categoria.getNombreCategoria());
            ps.setInt(2, categoria.getIdCategoria());
            int filas = ps.executeUpdate();
            if (filas > 0)
            {
                exito = true;
            }
            ps.close();
        }
        catch (Exception e)
        {
            System.err.println("Error actualizando categoría: " + e.getMessage());
        }
        finally 
        {
            connMySQL.desconectar(conn);
        }
        return exito;
    }
    
    public boolean eliminar(int idCategoria) 
    {
        ConexionDB connMySQL = new ConexionDB();
        var conn = connMySQL.conectar();
        boolean exito = false;
        try 
        {
            var ps = conn.prepareStatement("DELETE FROM Categoria WHERE id_categoria = ?");
            ps.setInt(1, idCategoria);
            int filas = ps.executeUpdate();
            if (filas > 0)
            {
                exito = true;
            }
            ps.close();
        }
        catch (Exception e) 
        {
            System.err.println("Error eliminando categoría: " + e.getMessage());
        }
        finally
        {
            connMySQL.desconectar(conn);
        }
        return exito;
    }
}