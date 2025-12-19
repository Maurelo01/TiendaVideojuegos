package com.mycompany.tiendavideojuegos.configuracion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB 
{
    private static ConexionDB instance;
    private Connection connection;
    private static final String URL = "jdbc:mysql://localhost:3306/TiendaDB";
    private static final String USUARIO = "root";
    private static final String CONTRASEÑA = "12345";

    private ConexionDB() 
    {
        try 
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USUARIO, CONTRASEÑA);
            System.out.println("Conexión establecida con TiendaDB");
        } 
        catch (ClassNotFoundException | SQLException ex) 
        {
            System.err.println("Error al conectar con la DB: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static ConexionDB getInstance() 
    {
        try 
        {
            if (instance == null || instance.getConnection().isClosed()) 
            {
                instance = new ConexionDB();
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        return instance;
    }

    public Connection getConnection() 
    {
        return connection;
    }
}