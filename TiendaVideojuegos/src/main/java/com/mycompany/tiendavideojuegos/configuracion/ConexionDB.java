package com.mycompany.tiendavideojuegos.configuracion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB 
{
    private Connection conn;
    private String url = "jdbc:mysql://localhost:3306/TiendaDB";
    private String usuario = "root";
    private String contraseña = "12345";
    
    public Connection conectar()
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, usuario, contraseña);
            System.out.println("Conexión exitosa a TiendaDB");
            return conn;
        }
        catch (SQLException e)
        {
            System.err.println("Error al conectar: " + e.getMessage());
            return null;
        } 
        catch (ClassNotFoundException ex) 
        {
            System.err.println("Error al cargar: " + ex.getMessage());
            return null;
        }
    }
    
    public void desconectar(Connection c) 
    {
        if (c != null) 
        {
            try 
            {
                c.close();
                System.out.println("Conexión cerrada");
            } 
            catch (SQLException e) 
            {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
}
