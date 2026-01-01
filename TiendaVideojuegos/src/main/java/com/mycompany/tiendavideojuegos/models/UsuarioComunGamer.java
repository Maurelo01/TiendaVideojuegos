package com.mycompany.tiendavideojuegos.models;

import com.mycompany.tiendavideojuegos.DTO.AnalisisCategoriaDTO;
import com.mycompany.tiendavideojuegos.DTO.BibliotecaDTO;
import com.mycompany.tiendavideojuegos.DTO.ComparativaCalificacionDTO;
import com.mycompany.tiendavideojuegos.DTO.UsuarioComunGamerDTO;
import com.mycompany.tiendavideojuegos.configuracion.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class UsuarioComunGamer
{
    public UsuarioComunGamerDTO registrarGamer(UsuarioComunGamerDTO gamerNuevo)
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        int idGenerado = -1;
        try
        {
            try (PreparedStatement psUsuario = conn.prepareStatement("INSERT INTO Usuario (correo, contraseña, rol) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) 
            {
                psUsuario.setString(1, gamerNuevo.getCorreo());
                psUsuario.setString(2, gamerNuevo.getContraseña());
                psUsuario.setString(3, "GAMER");
                psUsuario.executeUpdate();
                try (ResultSet rsIds = psUsuario.getGeneratedKeys()) 
                {
                    if (rsIds.next())
                    {
                        idGenerado = rsIds.getInt(1);
                        gamerNuevo.setIdUsuario(idGenerado);
                    }
                }
            }
            if (idGenerado != -1) 
            {
                try
                {
                    try (PreparedStatement psGamer = conn.prepareStatement("INSERT INTO Usuario_Comun_Gamer (id_usuario, nickname, fecha_nacimiento, telefono, pais, saldo_cartera) VALUES (?, ?, ?, ?, ?, ?)")) 
                    {
                        psGamer.setInt(1, idGenerado);
                        psGamer.setString(2, gamerNuevo.getNickname());
                        psGamer.setDate(3, gamerNuevo.getFechaNacimiento());
                        psGamer.setString(4, gamerNuevo.getTelefono());
                        psGamer.setString(5, gamerNuevo.getPais());
                        psGamer.setFloat(6, (float) 0.00);
                        psGamer.executeUpdate();
                    }
                }
                catch (Exception e) 
                {
                    System.err.println("Error creando usuario gamer, Borrando usuario sin tabla padre: " + idGenerado);
                    try (var psDel = conn.prepareStatement("DELETE FROM Usuario WHERE id_usuario = ?")) 
                    {
                        psDel.setInt(1, idGenerado);
                        psDel.executeUpdate();
                    }
                    return null;
                }
            } 
            else 
            {
                return null;
            }
        } 
        catch (Exception e) 
        {
            System.err.println("Error en registro: " + e.getMessage());
            return null;
        }
        return gamerNuevo;
    }
    
    public UsuarioComunGamerDTO obtenerPorId(int idUsuario) 
    {
        UsuarioComunGamerDTO gamer = null;
        Connection conn = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT u.id_usuario, u.correo, u.rol, u.fecha_registro, g.nickname, g.fecha_nacimiento, g.telefono, g.pais, g.saldo_cartera, g.avatar, g.perfil_publico FROM Usuario u " + 
                "JOIN Usuario_Comun_Gamer g ON u.id_usuario = g.id_usuario WHERE u.id_usuario = ?")) 
        {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) 
            {
                if (rs.next()) 
                {
                    gamer = new UsuarioComunGamerDTO();
                    gamer.setIdUsuario(rs.getInt("id_usuario"));
                    gamer.setCorreo(rs.getString("correo"));
                    gamer.setRol(rs.getString("rol"));
                    gamer.setFechaRegistro(rs.getTimestamp("fecha_registro"));
                    gamer.setNickname(rs.getString("nickname"));
                    gamer.setFechaNacimiento(rs.getDate("fecha_nacimiento"));
                    gamer.setTelefono(rs.getString("telefono"));
                    gamer.setPais(rs.getString("pais"));
                    gamer.setSaldoCartera(rs.getFloat("saldo_cartera"));
                    gamer.setPerfilPublico(rs.getBoolean("perfil_publico"));
                    byte[] imgBytes = rs.getBytes("avatar");
                    if (imgBytes != null && imgBytes.length > 0) 
                    {
                        String base64 = Base64.getEncoder().encodeToString(imgBytes);
                        gamer.setAvatar(base64);
                    }
                }
            }
        } 
        catch (Exception e) 
        {
            System.err.println("Error obteniendo gamer: " + e.getMessage());
        }
        return gamer;
    }
    
    public boolean actualizarPerfil(UsuarioComunGamerDTO gamer) 
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        boolean exito = false;
        try 
        {
            try (PreparedStatement psGamer = conn.prepareStatement("UPDATE Usuario_Comun_Gamer SET nickname = ?, telefono = ?, pais = ? WHERE id_usuario = ?")) 
            {
                psGamer.setString(1, gamer.getNickname());
                psGamer.setString(2, gamer.getTelefono());
                psGamer.setString(3, gamer.getPais());
                psGamer.setInt(4, gamer.getIdUsuario());
                psGamer.executeUpdate();
            }
            if (gamer.getAvatar() != null && !gamer.getAvatar().isEmpty()) 
            {
                try (PreparedStatement psAvatar = conn.prepareStatement("UPDATE Usuario_Comun_Gamer SET avatar = ? WHERE id_usuario = ?")) 
                {
                    byte[] imagenBytes = Base64.getDecoder().decode(gamer.getAvatar());
                    psAvatar.setBytes(1, imagenBytes); 
                    psAvatar.setInt(2, gamer.getIdUsuario());
                    psAvatar.executeUpdate();
                }
            }
            if (gamer.getContraseña() != null && !gamer.getContraseña().isEmpty()) 
            {
                try (PreparedStatement psUser = conn.prepareStatement("UPDATE Usuario SET contraseña = ? WHERE id_usuario = ?")) 
                {
                    psUser.setString(1, gamer.getContraseña());
                    psUser.setInt(2, gamer.getIdUsuario());
                    psUser.executeUpdate();
                }
            }
            exito = true;
        } 
        catch (Exception e) 
        {
            System.err.println("Error actualizando perfil gamer: " + e.getMessage());
            exito = false;
        } 
        return exito;
    }
    
    public boolean sumarSaldo(int idUsuario, float monto) 
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("UPDATE Usuario_Comun_Gamer SET saldo_cartera = saldo_cartera + ? WHERE id_usuario = ?")) 
        {
            ps.setFloat(1, monto);
            ps.setInt(2, idUsuario);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } 
        catch (SQLException e) 
        {
            System.err.println("Error recargando saldo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public float obtenerSaldo(int idUsuario) 
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT saldo_cartera FROM Usuario_Comun_Gamer WHERE id_usuario = ?")) 
        {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) 
            {
                if (rs.next()) 
                {
                    return rs.getFloat("saldo_cartera");
                }
            }
        } 
        catch (SQLException e) 
        {
            System.err.println("Error obteniendo saldo: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0f;
    }
    
    public List<BibliotecaDTO> obtenerBiblioteca(int idGamer) 
    {
        List<BibliotecaDTO> biblioteca = new ArrayList<>();
        Connection conn = ConexionDB.getInstance().getConnection();
        // Juegos comprados por el usuario
        String misJuegosSql = 
            "SELECT v.id_juego, v.titulo, v.imagen_portada, b.fecha_adquisicion, b.estado_instalacion, ? AS id_propietario, 'Tú' AS nickname_propietario, 0 AS es_prestamo_activo " +
            "FROM Biblioteca_Personal b " +
            "JOIN Videojuego v ON b.id_juego = v.id_juego " +
            "WHERE b.id_gamer = ? ";
        // Juegos del grupo familiar sin tomar los comprados por el usuario
        String juegosFamiliaSql = 
            "UNION " +
            "SELECT v.id_juego, v.titulo, v.imagen_portada, bp_amigo.fecha_adquisicion, " +
            "CASE " +
            "WHEN p.id_usuario_recibe = ? THEN 'INSTALADO' " + 
            "WHEN p.id_prestamo IS NOT NULL THEN 'OCUPADO' " +
            "WHEN bp_amigo.estado_instalacion = 'INSTALADO' THEN 'OCUPADO' " +
            "ELSE 'DISPONIBLE' " +
            "END as estado_calculado, " +
            "u_amigo.id_usuario as id_propietario, u_amigo.nickname as nickname_propietario, " +
            "CASE WHEN p.id_usuario_recibe = ? THEN 1 ELSE 0 END AS es_prestamo_activo " +
            "FROM Miembro_Grupo mg_yo " +
            "JOIN Miembro_Grupo mg_amigo ON mg_yo.id_grupo = mg_amigo.id_grupo AND mg_amigo.id_usuario != ? " +
            "JOIN Biblioteca_Personal bp_amigo ON mg_amigo.id_usuario = bp_amigo.id_gamer " +
            "JOIN Usuario_Comun_Gamer u_amigo ON bp_amigo.id_gamer = u_amigo.id_usuario " +
            "JOIN Videojuego v ON bp_amigo.id_juego = v.id_juego " +
            "LEFT JOIN Prestamo_Biblioteca p ON p.id_juego = v.id_juego " +
            "AND p.id_usuario_propietario = u_amigo.id_usuario " +
            "AND p.fecha_devolucion IS NULL " +
            "WHERE mg_yo.id_usuario = ? " +
            "AND v.id_juego NOT IN (SELECT id_juego FROM Biblioteca_Personal WHERE id_gamer = ?) " +
            "ORDER BY fecha_adquisicion DESC";
        try (PreparedStatement ps = conn.prepareStatement(misJuegosSql + juegosFamiliaSql)) 
        {
            ps.setInt(1, idGamer); // id_propietario = id del usuario actual
            ps.setInt(2, idGamer); // WHERE b.id_gamer = usuario actual
            ps.setInt(3, idGamer); // CASE: id_recibe = usuario actual
            ps.setInt(4, idGamer); // CASE: es_prestamo_activo
            ps.setInt(5, idGamer); // JOIN: amigo != usuario actual
            ps.setInt(6, idGamer); // WHERE mg_yo = usuario actual
            ps.setInt(7, idGamer); // NOT IN (juegos del usuario actual)
            try (ResultSet rs = ps.executeQuery()) 
            {
                while (rs.next()) 
                {
                    var item = new com.mycompany.tiendavideojuegos.DTO.BibliotecaDTO();
                    item.setIdJuego(rs.getInt("id_juego"));
                    item.setTitulo(rs.getString("titulo"));
                    item.setFechaAdquisicion(rs.getDate("fecha_adquisicion"));
                    item.setEstadoInstalacion(rs.getString("estado_instalacion"));
                    item.setIdPropietario(rs.getInt("id_propietario"));
                    item.setNombrePropietario(rs.getString("nickname_propietario"));
                    byte[] imgBytes = rs.getBytes("imagen_portada");
                    if (imgBytes != null && imgBytes.length > 0) 
                    {
                        item.setImagen(Base64.getEncoder().encodeToString(imgBytes));
                    }
                    biblioteca.add(item);
                }
            }
        } 
        catch (Exception e) 
        {
            System.err.println("Error obteniendo biblioteca personal y familiar: " + e.getMessage());
            e.printStackTrace();
        }
        return biblioteca;
    }
    
    public boolean instalarJuegoEnBiblioteca(int idGamer, int idJuego)
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("UPDATE Biblioteca_Personal SET estado_instalacion = 'INSTALADO' WHERE id_gamer = ? AND id_juego = ?"))
        {
            ps.setInt(1, idGamer);
            ps.setInt(2, idJuego);
            return ps.executeUpdate() > 0;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean desinstalarJuegoEnBiblioteca(int idGamer, int idJuego)
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("UPDATE Biblioteca_Personal SET estado_instalacion = 'NO_INSTALADO' WHERE id_gamer = ? AND id_juego = ?"))
        {
            ps.setInt(1, idGamer);
            ps.setInt(2, idJuego);
            return ps.executeUpdate() > 0;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean actualizarPrivacidad(int idUsuario, boolean esPublico) throws Exception
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("UPDATE Usuario_Comun_Gamer SET perfil_publico = ? WHERE id_usuario = ?"))
        {
            ps.setBoolean(1, esPublico);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        }
    }
    
    public List<UsuarioComunGamerDTO> buscarPorNickname(String textoBusqueda) 
    {
        List<UsuarioComunGamerDTO> resultados = new ArrayList<>();
        Connection conn = ConexionDB.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT id_usuario, nickname, avatar FROM Usuario_Comun_Gamer WHERE nickname LIKE ? AND perfil_publico = TRUE LIMIT 10")) 
        {
            ps.setString(1, "%" + textoBusqueda + "%");
            try (ResultSet rs = ps.executeQuery()) 
            {
                while (rs.next()) 
                {
                    UsuarioComunGamerDTO usuario = new UsuarioComunGamerDTO();
                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setNickname(rs.getString("nickname"));
                    byte[] imgBytes = rs.getBytes("avatar");
                    if (imgBytes != null && imgBytes.length > 0)
                    {
                        usuario.setAvatar(Base64.getEncoder().encodeToString(imgBytes));
                    }
                    resultados.add(usuario);
                }
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        return resultados;
    }
    
    public List<AnalisisCategoriaDTO> obtenerCategoriasFavoritas(int idGamer)
    {
        List<AnalisisCategoriaDTO> lista = new ArrayList<>();
        Connection conn = ConexionDB.getInstance().getConnection();
        String catFavSql = "SELECT c.nombre_categoria, COUNT(jc.id_juego) as total " +
                    "FROM Biblioteca_Personal bp " +
                    "JOIN Juego_Categoria jc ON bp.id_juego = jc.id_juego " +
                    "JOIN Categoria c ON jc.id_categoria = c.id_categoria " +
                    "WHERE bp.id_gamer = ? " +
                    "GROUP BY c.id_categoria, c.nombre_categoria " +
                    "ORDER BY total DESC " +
                    "LIMIT 5";
        try (PreparedStatement ps = conn.prepareStatement(catFavSql))
        {
            ps.setInt(1, idGamer);
            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    lista.add(new AnalisisCategoriaDTO(rs.getString("nombre_categoria"), rs.getInt("total")));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return lista;
    }
    
    public List<ComparativaCalificacionDTO> obtenerComparativa(int idGamer)
    {
        List<ComparativaCalificacionDTO> lista = new ArrayList<>();
        Connection conn = ConexionDB.getInstance().getConnection();
        String comparativaSql = "SELECT v.titulo, " +
                    "c_propio.calificacion as mi_nota, " +
                    "(SELECT AVG(c_all.calificacion) " +
                    "FROM Comentario c_all WHERE c_all.id_juego = v.id_juego AND c_all.id_comentario_principal IS NULL) as promedio_global " +
                    "FROM Comentario c_propio " +
                    "JOIN Videojuego v ON c_propio.id_juego = v.id_juego " +
                    "WHERE c_propio.id_gamer = ? " +
                    "AND c_propio.id_comentario_principal IS NULL"; 

        try (PreparedStatement ps = conn.prepareStatement(comparativaSql))
        {
            ps.setInt(1, idGamer);
            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    ComparativaCalificacionDTO dto = new ComparativaCalificacionDTO();
                    dto.setTituloJuego(rs.getString("titulo"));
                    dto.setMiCalificacion(rs.getInt("mi_nota"));
                    dto.setPromedioComunidad(rs.getFloat("promedio_global"));
                    lista.add(dto);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return lista;
    }
}
