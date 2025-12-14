package com.mycompany.tiendavideojuegos.models;

import java.sql.Timestamp;

public class Usuario 
{
    int idUsuario;
    String correo;
    String contraseña;
    String rol;
    Timestamp fechaRegistro;
    
    public Usuario()
    {
        // Vacio por si acaso
    }
    
    public Usuario(String correo, String contraseña, String rol) // Para nuevos usuarios
    {
        this.correo = correo;
        this.contraseña = contraseña;
        this.rol = rol;
    }
    
    public Usuario(int idUsuario, String correo, String contraseña, String rol, Timestamp fechaRegistro) // Para leer de la base de datos
    {
        this.idUsuario = idUsuario;
        this.correo = correo;
        this.contraseña = contraseña;
        this.rol = rol;
        this.fechaRegistro = fechaRegistro;
    }
}
