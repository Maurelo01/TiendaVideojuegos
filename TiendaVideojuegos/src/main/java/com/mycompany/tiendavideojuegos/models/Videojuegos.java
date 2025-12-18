package com.mycompany.tiendavideojuegos.models;

import com.mycompany.tiendavideojuegos.DTO.MultimediaDTO;
import com.mycompany.tiendavideojuegos.DTO.VideojuegosDTO;
import com.mycompany.tiendavideojuegos.configuracion.ConexionDB;
import java.sql.Statement;
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
            var ps = conn.prepareStatement("INSERT INTO Videojuego (id_empresa, titulo, descripcion, precio, recursos_minimos, clasificacion_edad, estado, fecha_publicacion, comentarios_estado) VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_DATE, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, juego.getIdEmpresa());
            ps.setString(2, juego.getTitulo());
            ps.setString(3, juego.getDescripcion());
            ps.setFloat(4, juego.getPrecio());
            ps.setString(5, juego.getRecursosMinimos());    
            ps.setString(6, juego.getClasificacionEdad());
            ps.setString(7, "ACTIVO");
            ps.setBoolean(8, true);
            int filas = ps.executeUpdate();
            if (filas == 0) 
            {
                throw new Exception("No se insertó el videojuego");
            }
            var rsKeys = ps.getGeneratedKeys();
            if (!rsKeys.next()) 
            {
                throw new Exception("No se generó id del videojuego");
            }
            idJuegoGenerado = rsKeys.getInt(1);
            ps.close();
            if (filas > 0 && idJuegoGenerado != -1) 
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
                        if (psMedia.executeUpdate() == 0) 
                        {
                            throw new Exception("Error insertando multimedia");
                        }
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
                        if (psCat.executeUpdate() == 0) 
                        {
                            throw new Exception("Error insertando categoría");
                        }
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
                    psDel.close();
                    System.out.println("Se eliminó el registro del videojuego por error en añadir sus recursos.");
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
    
    public boolean editar(VideojuegosDTO juego)
    {
        ConexionDB connMySQL = new ConexionDB();
        var conn = connMySQL.conectar();
        boolean exito = false;
        try
        {
            var ps = conn.prepareStatement("UPDATE Videojuego SET titulo=?, descripcion=?, precio=?, recursos_minimos=?, clasificacion_edad=?, comentarios_estado=? WHERE id_juego=?");
            ps.setString(1, juego.getTitulo());
            ps.setString(2, juego.getDescripcion());
            ps.setFloat(3, juego.getPrecio());
            ps.setString(4, juego.getRecursosMinimos());
            ps.setString(5, juego.getClasificacionEdad());
            ps.setBoolean(6, juego.isComentariosEstado());
            ps.setInt(7, juego.getIdJuego());
            int filas = ps.executeUpdate();
            ps.close();
            if(filas > 0)
            {
                if (juego.getIdsCategorias() != null) 
                {
                    // Actualizar categorias
                    var psDelCat = conn.prepareStatement("DELETE FROM Juego_Categoria WHERE id_juego = ?");
                    psDelCat.setInt(1, juego.getIdJuego());
                    psDelCat.executeUpdate();
                    psDelCat.close();
                    // Insertar nuevas categorias 
                    if (!juego.getIdsCategorias().isEmpty()) 
                    {
                        var psInsCat = conn.prepareStatement("INSERT INTO Juego_Categoria (id_juego, id_categoria) VALUES (?, ?)");
                        for (Integer idCat : juego.getIdsCategorias()) 
                        {
                            psInsCat.setInt(1, juego.getIdJuego());
                            psInsCat.setInt(2, idCat);
                            psInsCat.executeUpdate();
                        }
                        psInsCat.close();
                    }
                }
                if (juego.getMultimedia() != null) 
                {
                    // Borrar multimedia antigua
                    var psDelMedia = conn.prepareStatement("DELETE FROM Multimedia WHERE id_juego = ?");
                    psDelMedia.setInt(1, juego.getIdJuego());
                    psDelMedia.executeUpdate();
                    psDelMedia.close();
                    // Insertar nueva
                    if (!juego.getMultimedia().isEmpty()) 
                    {
                        var psInsMed = conn.prepareStatement("INSERT INTO Multimedia (id_juego, url, tipo) VALUES (?, ?, ?)");
                        for (MultimediaDTO media : juego.getMultimedia()) 
                        {
                            psInsMed.setInt(1, juego.getIdJuego());
                            psInsMed.setString(2, media.getUrl());
                            psInsMed.setString(3, media.getTipo());
                            psInsMed.executeUpdate();
                        }
                        psInsMed.close();
                    }
                }
                exito = true;
            }
        }
        catch (Exception e)
        {
            System.err.println("Error al editar videojuego: " + e.getMessage());
        }
        finally 
        {
            connMySQL.desconectar(conn);
        }
        return exito;
    }
}
