package com.mycompany.tiendavideojuegos.models;

import com.mycompany.tiendavideojuegos.DTO.MultimediaDTO;
import com.mycompany.tiendavideojuegos.DTO.VideojuegosDTO;
import com.mycompany.tiendavideojuegos.configuracion.ConexionDB;
import java.util.ArrayList;
import java.util.List;

public class Videojuegos
{
    public boolean publicar(VideojuegosDTO juego) 
    {
        ConexionDB connMySQL = new ConexionDB();
        var conn = connMySQL.conectar();
        boolean exito = false;
        int idJuegoGenerado = -1; // guardar id 
        try 
        {
            // Insertar Juego
            var ps = conn.prepareStatement("INSERT INTO Videojuego (id_empresa, titulo, descripcion, precio, recursos_minimos, clasificacion_edad, estado, fecha_publicacion, comentarios_estado) VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_DATE, ?)");
            ps.setInt(1, juego.getIdEmpresa());
            ps.setString(2, juego.getTitulo());
            ps.setString(3, juego.getDescripcion());
            ps.setFloat(4, juego.getPrecio());
            ps.setString(5, juego.getRecursosMinimos());    
            ps.setString(6, juego.getClasificacionEdad());
            ps.setString(7, "ACTIVO");
            ps.setBoolean(8, true);
            var rsKeys = ps.getGeneratedKeys();
            if(rsKeys.next()) // Recupera la id
            {
                idJuegoGenerado = rsKeys.getInt(1);
            }
            rsKeys.close();
            ps.close();
            if (ps.executeUpdate() > 0 && idJuegoGenerado != -1) 
            {
                // Insertar multimedia
                if (juego.getMultimedia() != null && !juego.getMultimedia().isEmpty()) 
                {
                    var psMedia = conn.prepareStatement("INSERT INTO Multimedia (id_juego, url, tipo) VALUES (?, ?, ?)");
                    for (MultimediaDTO media : juego.getMultimedia()) 
                    {
                        psMedia.setInt(1, idJuegoGenerado);
                        psMedia.setString(2, media.getUrl());
                        psMedia.setString(3, media.getTipo());
                        psMedia.executeUpdate();
                    }
                    psMedia.close();
                }
                // Insertar categorias
                if (juego.getIdsCategorias() != null && !juego.getIdsCategorias().isEmpty()) 
                {
                    var psCat = conn.prepareStatement("INSERT INTO Juego_Categoria (id_juego, id_categoria) VALUES (?, ?)");
                    for (Integer idCat : juego.getIdsCategorias())
                    {
                        psCat.setInt(1, idJuegoGenerado);
                        psCat.setInt(2, idCat);
                        psCat.executeUpdate();
                    }
                    psCat.close();
                }
                exito = true;
            }
            ps.close();
        }
        catch (Exception e) 
        {
            System.err.println("Error publicando videojuego: " + e.getMessage());
            if (idJuegoGenerado != -1) 
            {
                try 
                {
                    var psDel = conn.prepareStatement("DELETE FROM Videojuego WHERE id_juego = ?");
                    psDel.setInt(1, idJuegoGenerado);
                    psDel.executeUpdate();
                    System.out.println("Error: Se eliminó el registro del videojuego por error en añadir sus recursos.");
                } 
                catch (Exception ex) 
                {
                    System.err.println("Error al intentar borrar registro incompleto: " + ex.getMessage());
                }
            }
        }
        finally 
        {
            connMySQL.desconectar(conn);
        }
        return exito;
    }
    
    public List<VideojuegosDTO> listarPorEmpresa(int idEmpresa) 
    {
        ConexionDB connMySQL = new ConexionDB();
        var conn = connMySQL.conectar();
        List<VideojuegosDTO> lista = new ArrayList<>();
        try 
        {
            var ps = conn.prepareStatement("SELECT * FROM Videojuego WHERE id_empresa = ?");
            ps.setInt(1, idEmpresa);
            var rs = ps.executeQuery();
            while (rs.next()) 
            {
                var videojuego = new VideojuegosDTO();
                videojuego.setIdJuego(rs.getInt("id_juego"));
                videojuego.setIdEmpresa(rs.getInt("id_empresa"));
                videojuego.setTitulo(rs.getString("titulo"));
                videojuego.setDescripcion(rs.getString("descripcion"));
                videojuego.setPrecio(rs.getFloat("precio"));
                videojuego.setRecursosMinimos(rs.getString("recursos_minimos"));
                videojuego.setClasificacionEdad(rs.getString("clasificacion_edad"));
                videojuego.setEstado(rs.getString("estado"));
                videojuego.setFechaPublicacion(rs.getDate("fecha_publicacion"));
                videojuego.setComentariosEstado(rs.getBoolean("comentarios_estado"));
                lista.add(videojuego);
            }
            rs.close();
            ps.close();
        }
        catch (Exception e) 
        {
            System.err.println("Error listando juegos: " + e.getMessage());
        }
        finally
        {
            connMySQL.desconectar(conn);
        }
        return lista;
    }
}
