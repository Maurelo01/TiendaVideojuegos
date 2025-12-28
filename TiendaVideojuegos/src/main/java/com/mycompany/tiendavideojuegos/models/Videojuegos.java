package com.mycompany.tiendavideojuegos.models;

import com.mycompany.tiendavideojuegos.DTO.MultimediaDTO;
import com.mycompany.tiendavideojuegos.DTO.VideojuegosDTO;
import com.mycompany.tiendavideojuegos.configuracion.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Videojuegos
{
    public boolean publicar(VideojuegosDTO juego) 
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        boolean exito = false;
        int idJuegoGenerado = -1; // guardar id 
        try 
        {
            // Insertar Juego
            var ps = conn.prepareStatement("INSERT INTO Videojuego (id_empresa, titulo, descripcion, precio, recursos_minimos, clasificacion_edad, estado, fecha_publicacion, comentarios_estado, imagen_portada) VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_DATE, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, juego.getIdEmpresa());
            ps.setString(2, juego.getTitulo());
            ps.setString(3, juego.getDescripcion());
            ps.setFloat(4, juego.getPrecio());
            ps.setString(5, juego.getRecursosMinimos());    
            ps.setString(6, juego.getClasificacionEdad());
            ps.setString(7, "ACTIVO");
            ps.setBoolean(8, true);
            if (juego.getImagen() != null && !juego.getImagen().isEmpty()) 
            {
                byte[] imagenBytes = Base64.getDecoder().decode(juego.getImagen());
                ps.setBytes(9, imagenBytes);
            } 
            else 
            {
                ps.setNull(9, Types.BLOB);
            }
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
                    agregarMultimedia(idJuegoGenerado, juego.getMultimedia());
                }
                // Insertar categorias
                if (juego.getIdsCategorias() != null && !juego.getIdsCategorias().isEmpty()) 
                {
                    try (PreparedStatement psCat = conn.prepareStatement("INSERT INTO Juego_Categoria (id_juego, id_categoria) VALUES (?, ?)")) 
                    {
                        for (Integer idCat : juego.getIdsCategorias()) 
                        {
                            psCat.setInt(1, idJuegoGenerado);
                            psCat.setInt(2, idCat);
                            if (psCat.executeUpdate() == 0)
                            {
                                throw new Exception("Error insertando categoría");
                            }
                        }
                    }
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
                    try (PreparedStatement psDel = conn.prepareStatement("DELETE FROM Videojuego WHERE id_juego = ?")) 
                    {
                        psDel.setInt(1, idJuegoGenerado);
                        psDel.executeUpdate();
                    }
                    System.out.println("Se eliminó el registro del videojuego por error en añadir sus recursos.");
                } 
                catch (Exception ex) 
                {
                    System.err.println("Error al intentar borrar registro incompleto: " + ex.getMessage());
                }
            }
        }
        return exito;
    }
    
    public List<VideojuegosDTO> listarPorEmpresa(int idEmpresa) 
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        List<VideojuegosDTO> lista = new ArrayList<>();
        try 
        {
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Videojuego WHERE id_empresa = ?")) {
                ps.setInt(1, idEmpresa);
                try (ResultSet rs = ps.executeQuery()) 
                {
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
                }
            }
        }
        catch (Exception e) 
        {
            System.err.println("Error listando juegos: " + e.getMessage());
        }
        return lista;
    }
    
    public List<VideojuegosDTO> listarActivos() 
    {
        List<VideojuegosDTO> lista = new ArrayList<>();
        Connection conn = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT v.* FROM Videojuego v JOIN Empresa e ON v.id_empresa = e.id_empresa WHERE v.estado = 'ACTIVO' AND e.estado = 'ACTIVO'"); ResultSet rs = ps.executeQuery()) 
        {
            while (rs.next()) 
            {
                VideojuegosDTO juego = new VideojuegosDTO();
                juego.setIdJuego(rs.getInt("id_juego"));
                juego.setIdEmpresa(rs.getInt("id_empresa"));
                juego.setTitulo(rs.getString("titulo"));
                juego.setDescripcion(rs.getString("descripcion"));
                juego.setPrecio(rs.getFloat("precio"));
                juego.setRecursosMinimos(rs.getString("recursos_minimos"));
                juego.setClasificacionEdad(rs.getString("clasificacion_edad"));
                juego.setEstado(rs.getString("estado"));
                byte[] imgBytes = rs.getBytes("imagen_portada");
                if (imgBytes != null && imgBytes.length > 0) 
                {
                    String base64 = Base64.getEncoder().encodeToString(imgBytes);
                    juego.setImagen(base64);
                } 
                else 
                {
                    juego.setImagen(null);
                }
                lista.add(juego);
            }
        } 
        catch (SQLException e) 
        {
            System.err.println("Error listando juegos activos: " + e.getMessage());
        }
        return lista;
    }
    
    public boolean editar(VideojuegosDTO juego)
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        boolean exito = false;
        try
        {
            int filas;
            try (PreparedStatement ps = conn.prepareStatement("UPDATE Videojuego SET titulo=?, descripcion=?, precio=?, recursos_minimos=?, clasificacion_edad=?, comentarios_estado=? WHERE id_juego=?")) 
            {
                ps.setString(1, juego.getTitulo());
                ps.setString(2, juego.getDescripcion());
                ps.setFloat(3, juego.getPrecio());
                ps.setString(4, juego.getRecursosMinimos());
                ps.setString(5, juego.getClasificacionEdad());
                ps.setBoolean(6, juego.isComentariosEstado());
                ps.setInt(7, juego.getIdJuego());
                filas = ps.executeUpdate();
            }
            if(filas > 0)
            {
                if (juego.getImagen() != null && !juego.getImagen().isEmpty()) 
                {
                    try (PreparedStatement psImg = conn.prepareStatement("UPDATE Videojuego SET imagen_portada = ? WHERE id_juego = ?")) 
                    {
                        byte[] imagenBytes = Base64.getDecoder().decode(juego.getImagen());
                        psImg.setBytes(1, imagenBytes);
                        psImg.setInt(2, juego.getIdJuego());
                        psImg.executeUpdate();
                    }
                }
                if (juego.getIdsCategorias() != null) 
                {
                    // Actualizar categorias
                    try (PreparedStatement psDelCat = conn.prepareStatement("DELETE FROM Juego_Categoria WHERE id_juego = ?")) 
                    {
                        psDelCat.setInt(1, juego.getIdJuego());
                        psDelCat.executeUpdate();
                    }
                    // Insertar nuevas categorias 
                    if (!juego.getIdsCategorias().isEmpty()) 
                    {
                        try (PreparedStatement psInsCat = conn.prepareStatement("INSERT INTO Juego_Categoria (id_juego, id_categoria) VALUES (?, ?)")) 
                        {
                            for (Integer idCat : juego.getIdsCategorias())
                            {
                                psInsCat.setInt(1, juego.getIdJuego());
                                psInsCat.setInt(2, idCat);
                                psInsCat.executeUpdate();
                            }
                        }
                    }
                }
                if (juego.getMultimedia() != null) 
                {
                    // Borrar multimedia antigua
                    try (PreparedStatement psDelMedia = conn.prepareStatement("DELETE FROM Multimedia WHERE id_juego = ?")) 
                    {
                        psDelMedia.setInt(1, juego.getIdJuego());
                        psDelMedia.executeUpdate();
                    }
                    // Insertar nueva
                    if (!juego.getMultimedia().isEmpty()) 
                    {
                        agregarMultimedia(juego.getIdJuego(), juego.getMultimedia());
                    }
                }
                exito = true;
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al editar videojuego: " + e.getMessage());
        }
        return exito;
    }
    
    public boolean actualizarEstado(int idJuego, String nuevoEstado) 
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        try 
        {
            try (PreparedStatement ps = conn.prepareStatement("UPDATE Videojuego SET estado = ? WHERE id_juego = ?")) 
            {
                ps.setString(1, nuevoEstado);
                ps.setInt(2, idJuego);
                return ps.executeUpdate() > 0;
            }
        } 
        catch (SQLException e) 
        {
            System.err.println("Error actualizando estado del juego: " + e.getMessage());
            return false;
        }
    }
    
    public boolean agregarMultimedia(int idJuego, List<MultimediaDTO> lista)
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO Multimedia (id_juego, contenido, tipo) VALUES (?, ?, ?)"))
        {
            for (MultimediaDTO media : lista)
            {
                ps.setInt(1, idJuego);
                ps.setBytes(2, decodeBase64(media.getContenido()));
                ps.setString(3, media.getTipo());
                ps.addBatch();
            }
            ps.executeBatch();
            return true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
    private byte[] decodeBase64(String base64)
    {
        if (base64 == null || base64.trim().isEmpty()) return null;
        if (base64.contains(",")) 
        {
            base64 = base64.split(",")[1];
        }
        try 
        {
            base64 = base64.replaceAll("\\s", ""); 
            return Base64.getDecoder().decode(base64);
        }
        catch (IllegalArgumentException e)
        {
            System.err.println("Error decodificando Base64: " + e.getMessage());
            return null;
        }
    }
    
    public List<MultimediaDTO> obtenerMultimedia(int idJuego)
    {
        List<MultimediaDTO> lista = new ArrayList<>();
        Connection conn = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Multimedia WHERE id_juego = ?"))
        {
            ps.setInt(1, idJuego);
            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    MultimediaDTO media = new MultimediaDTO();
                    media.setIdMedia(rs.getInt("id_media"));
                    media.setIdJuego(rs.getInt("id_juego"));
                    media.setTipo(rs.getString("tipo"));
                    byte[] contenidoBytes = rs.getBytes("contenido");
                    if (contenidoBytes != null && contenidoBytes.length > 0)
                    {
                        String base64 = Base64.getEncoder().encodeToString(contenidoBytes);
                        media.setContenido(base64); 
                    }
                    lista.add(media);
                }
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error obteniendo multimedia: " + e.getMessage());
        }
        return lista;
    }

    public boolean eliminarMultimedia(int idMedia) 
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Multimedia WHERE id_media = ?")) 
        {
            ps.setInt(1, idMedia);
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } 
        catch (SQLException e) 
        {
            System.err.println("Error eliminando multimedia: " + e.getMessage());
            return false;
        }
    }
    
    public List<VideojuegosDTO> buscarJuegos(String titulo, Integer idCategoria) 
    {
        List<VideojuegosDTO> lista = new ArrayList<>();
        Connection conn = ConexionDB.getInstance().getConnection();
        StringBuilder juegoSql = new StringBuilder("SELECT DISTINCT v.* FROM Videojuego v ");
        if (idCategoria != null && idCategoria > 0) 
        {
            juegoSql.append("JOIN Juego_Categoria jc ON v.id_juego = jc.id_juego ");
        }
        juegoSql.append("JOIN Empresa e ON v.id_empresa = e.id_empresa ");
        juegoSql.append("WHERE v.estado = 'ACTIVO' AND e.estado = 'ACTIVO' ");
        if (titulo != null && !titulo.trim().isEmpty())
        {
            juegoSql.append("AND v.titulo LIKE ? ");
        }
        if (idCategoria != null && idCategoria > 0)
        {
            juegoSql.append("AND jc.id_categoria = ? ");
        }
        try (PreparedStatement ps = conn.prepareStatement(juegoSql.toString())) 
        {
            int indice = 1;
            if (titulo != null && !titulo.trim().isEmpty())
            {
                ps.setString(indice++, "%" + titulo + "%");
            }
            if (idCategoria != null && idCategoria > 0)
            {
                ps.setInt(indice++, idCategoria);
            }
            try (ResultSet rs = ps.executeQuery()) 
            {
                while (rs.next()) 
                {
                    VideojuegosDTO juego = new VideojuegosDTO();
                    juego.setIdJuego(rs.getInt("id_juego"));
                    juego.setIdEmpresa(rs.getInt("id_empresa"));
                    juego.setTitulo(rs.getString("titulo"));
                    juego.setDescripcion(rs.getString("descripcion"));
                    juego.setPrecio(rs.getFloat("precio"));
                    juego.setRecursosMinimos(rs.getString("recursos_minimos"));
                    juego.setClasificacionEdad(rs.getString("clasificacion_edad"));
                    juego.setEstado(rs.getString("estado"));
                    byte[] imgBytes = rs.getBytes("imagen_portada");
                    if (imgBytes != null && imgBytes.length > 0)
                    {
                        juego.setImagen(java.util.Base64.getEncoder().encodeToString(imgBytes));
                    }
                    lista.add(juego);
                }
            }
        } 
        catch (SQLException e) 
        {
            System.err.println("Error en búsqueda: " + e.getMessage());
        }
        return lista;
    }
}
