package com.mycompany.tiendavideojuegos.models;

import com.mycompany.tiendavideojuegos.DTO.UsuarioDTO;
import com.mycompany.tiendavideojuegos.configuracion.ConexionDB;

public class Usuario 
{
    protected int idUsuario;
    protected String correo;
    protected String contraseña;
    protected String rol;
    
    public Usuario() 
    {
    }

    public Usuario(String correo, String contraseña, String rol) 
    {
        this.correo = correo;
        this.contraseña = contraseña;
        this.rol = rol;
    }
    
    public UsuarioDTO login(String correo, String contraseña)
    {
        ConexionDB connMySQL = new ConexionDB();
        var conn = connMySQL.conectar();
        UsuarioDTO usuarioDTO = null;
        
        try
        {
            var ps = conn.prepareStatement("SELECT * FROM Usuario WHERE correo = ? AND contraseña = ?");
            ps.setString(1, correo);
            ps.setString(2, contraseña);
            var rs = ps.executeQuery();
            try (rs)
            {
                if (rs.next())
                {
                    usuarioDTO = new UsuarioDTO();
                    usuarioDTO.setIdUsuario(rs.getInt("id_usuario"));
                    usuarioDTO.setCorreo(rs.getString("correo"));
                    usuarioDTO.setRol(rs.getString("rol"));
                }
            }
        }
        catch (Exception e)
        {
            System.err.println("Error en login: " + e.getMessage());
        }
        finally 
        {
            connMySQL.desconectar(conn);
        }
        return usuarioDTO;
    }
}
