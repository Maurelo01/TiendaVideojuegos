package com.mycompany.tiendavideojuegos.services;

import com.mycompany.tiendavideojuegos.DTO.GrupoFamiliarDTO;
import com.mycompany.tiendavideojuegos.DTO.SolicitudGrupoDTO;
import com.mycompany.tiendavideojuegos.DTO.UsuarioComunGamerDTO;
import com.mycompany.tiendavideojuegos.configuracion.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GruposService 
{
    public boolean crearGrupo(SolicitudGrupoDTO solicitud) throws Exception 
    {
        if (solicitud.getNombreGrupo() == null || solicitud.getNombreGrupo().isEmpty()) 
        {
            throw new Exception("El nombre del grupo es obligatorio.");
        }

        Connection conn = ConexionDB.getInstance().getConnection();
        if (perteneceAGrupo(conn, solicitud.getIdLider())) 
        {
            throw new Exception("El usuario ya pertenece a un grupo familiar y no puede crear otro.");
        }

        boolean exito = false;
        int idGrupoGenerado = -1;
        try 
        {
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO Grupo_Familiar (id_lider, nombre_grupo, fecha_creacion) VALUES (?, ?, CURRENT_DATE)", Statement.RETURN_GENERATED_KEYS)) 
            {
                ps.setInt(1, solicitud.getIdLider());
                ps.setString(2, solicitud.getNombreGrupo());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) 
                {
                    if (rs.next()) 
                    {
                        idGrupoGenerado = rs.getInt(1);
                    }
                }
            }

            if (idGrupoGenerado != -1) 
            {
                try 
                {
                    try (PreparedStatement psMiembro = conn.prepareStatement("INSERT INTO Miembro_Grupo (id_usuario, id_grupo) VALUES (?, ?)")) 
                    {
                        psMiembro.setInt(1, solicitud.getIdLider());
                        psMiembro.setInt(2, idGrupoGenerado);
                        psMiembro.executeUpdate();
                    }

                    exito = true;
                }
                catch (Exception e) 
                {
                    System.err.println("Error vinculando líder, eliminando grupo id: " + idGrupoGenerado);
                    try (PreparedStatement psDelete = conn.prepareStatement("DELETE FROM Grupo_Familiar WHERE id_grupo = ?")) 
                    {
                        psDelete.setInt(1, idGrupoGenerado);
                        psDelete.executeUpdate();
                    }
                    catch (Exception exDelete) 
                    {
                        System.err.println("Error: No se pudo borrar el grupo fallido: " + exDelete.getMessage());
                    }

                    throw new Exception("Error al vincular el líder con el grupo. Se revirtió la creación.");
                }
            } 
        } 
        catch (Exception ex) 
        {
            ex.printStackTrace();
            throw new Exception(ex.getMessage());
        }
        return exito;
    }
    
    public GrupoFamiliarDTO obtenerGrupoDelUsuario(int idUsuario) 
    {
        GrupoFamiliarDTO grupo = null;
        Connection conn = ConexionDB.getInstance().getConnection();
        String sqlBuscar = "SELECT g.*, u.nickname as nick_lider FROM Miembro_Grupo mg " +
                           "JOIN Grupo_Familiar g ON mg.id_grupo = g.id_grupo " +
                           "JOIN Usuario_Comun_Gamer u ON g.id_lider = u.id_usuario " +
                           "WHERE mg.id_usuario = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sqlBuscar)) 
        {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) 
            {
                if (rs.next()) 
                {
                    grupo = new GrupoFamiliarDTO();
                    grupo.setIdGrupo(rs.getInt("id_grupo"));
                    grupo.setNombreGrupo(rs.getString("nombre_grupo"));
                    grupo.setIdLider(rs.getInt("id_lider"));
                    grupo.setNombreLider(rs.getString("nick_lider"));
                    grupo.setMiembros(listarMiembros(conn, grupo.getIdGrupo()));
                    grupo.setTotalMiembros(grupo.getMiembros().size());
                }
            }
        } 
        catch (Exception e) 
        {
            System.err.println("Error obteniendo grupo: " + e.getMessage());
        }
        return grupo;
    }

    private List<UsuarioComunGamerDTO> listarMiembros(Connection conn, int idGrupo) 
    {
        List<UsuarioComunGamerDTO> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT u.id_usuario, u.nickname, u.avatar FROM Miembro_Grupo mg JOIN Usuario_Comun_Gamer u ON mg.id_usuario = u.id_usuario WHERE mg.id_grupo = ?")) 
        {
            ps.setInt(1, idGrupo);
            try (ResultSet rs = ps.executeQuery()) 
            {
                while (rs.next()) 
                {
                    UsuarioComunGamerDTO usuario = new UsuarioComunGamerDTO();
                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setNickname(rs.getString("nickname"));
                    byte[] imgBytes = rs.getBytes("avatar");
                    if (imgBytes != null) 
                    {
                        usuario.setAvatar(java.util.Base64.getEncoder().encodeToString(imgBytes));
                    }
                    lista.add(usuario);
                }
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        return lista;
    }
    
    private boolean perteneceAGrupo(Connection conn, int idUsuario) 
    {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM Miembro_Grupo WHERE id_usuario = ?")) 
        {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) 
            {
                return rs.next();
            }
        } 
        catch (Exception e) 
        {
            return false;
        }
    }
    
    public boolean agregarMiembro(SolicitudGrupoDTO solicitud) throws Exception 
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        if (!esLiderDelGrupo(conn, solicitud.getIdLider(), solicitud.getIdGrupo())) 
        {
            throw new Exception("Solo el líder del grupo puede enviar invitaciones.");
        }
        int idInvitado = solicitud.getIdUsuarioMiembro();
        if (idInvitado == 0 && solicitud.getNicknameMiembro() != null && !solicitud.getNicknameMiembro().isEmpty()) 
        {
            idInvitado = buscarIdPorNickname(conn, solicitud.getNicknameMiembro());
        }
        if (idInvitado <= 0) 
        {
            throw new Exception("El usuario indicado no existe.");
        }
        if (perteneceAGrupo(conn, idInvitado)) 
        {
            throw new Exception("El usuario '" + solicitud.getNicknameMiembro() + "' ya pertenece a un grupo familiar.");
        }
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO Miembro_Grupo (id_usuario, id_grupo) VALUES (?, ?)")) 
        {
            ps.setInt(1, idInvitado);
            ps.setInt(2, solicitud.getIdGrupo());
            return ps.executeUpdate() > 0;
        }
    }
    
    private boolean esLiderDelGrupo(Connection conn, int idUsuario, int idGrupo) 
    {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM Grupo_Familiar WHERE id_grupo = ? AND id_lider = ?")) 
        {
            ps.setInt(1, idGrupo);
            ps.setInt(2, idUsuario);
            try (ResultSet rs = ps.executeQuery()) 
            {
                return rs.next();
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return false;
        }
    }

    private int buscarIdPorNickname(Connection conn, String nickname) 
    {
        try (PreparedStatement ps = conn.prepareStatement("SELECT id_usuario FROM Usuario_Comun_Gamer WHERE nickname = ?")) 
        {
            ps.setString(1, nickname);
            try (ResultSet rs = ps.executeQuery()) 
            {
                if (rs.next()) return rs.getInt("id_usuario");
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        return -1;
    }
    
    public boolean expulsarMiembro(SolicitudGrupoDTO solicitud) throws Exception 
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        if (!esLiderDelGrupo(conn, solicitud.getIdLider(), solicitud.getIdGrupo())) 
        {
            throw new Exception("No tienes permisos para expulsar miembros.");
        }
        if (solicitud.getIdLider() == solicitud.getIdUsuarioMiembro()) 
        {
            throw new Exception("El líder no puede ser expulsado. Debe eliminar el grupo completo.");
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Miembro_Grupo WHERE id_usuario = ? AND id_grupo = ?")) 
        {
            ps.setInt(1, solicitud.getIdUsuarioMiembro());
            ps.setInt(2, solicitud.getIdGrupo());
            int filas = ps.executeUpdate();
            if (filas == 0) 
            {
                throw new Exception("El usuario no pertenece a este grupo.");
            }
            return true;
        }
    }
    
    public boolean eliminarGrupo(int idLider, int idGrupo) throws Exception 
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        if (!esLiderDelGrupo(conn, idLider, idGrupo)) 
        {
            throw new Exception("Solo el líder del grupo tiene permisos para eliminar gurpo.");
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Grupo_Familiar WHERE id_grupo = ?")) 
        {
            ps.setInt(1, idGrupo);
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            throw new Exception("Error al eliminar el grupo: " + e.getMessage());
        }
    }
    
    public boolean salirGrupo(int idUsuario, int idGrupo) throws Exception 
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        if (!perteneceAGrupoEspecifico(conn, idUsuario, idGrupo)) 
        {
            throw new Exception("No perteneces a este grupo.");
        }
        if (esLiderDelGrupo(conn, idUsuario, idGrupo)) 
        {
            return eliminarGrupo(idUsuario, idGrupo);
        } 
        else 
        {
            try 
            {
                try (PreparedStatement psDevolver = conn.prepareStatement("UPDATE Prestamo_Biblioteca SET fecha_devolucion = NOW(), estado_instalacion = 'NO_INSTALADO' WHERE id_usuario_recibe = ? AND fecha_devolucion IS NULL")) 
                {
                    psDevolver.setInt(1, idUsuario);
                    psDevolver.executeUpdate();
                }
                try (PreparedStatement psSalir = conn.prepareStatement("DELETE FROM Miembro_Grupo WHERE id_usuario = ? AND id_grupo = ?")) 
                {
                    psSalir.setInt(1, idUsuario);
                    psSalir.setInt(2, idGrupo);
                    return psSalir.executeUpdate() > 0;
                }
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
                throw new Exception("Error al intentar salir del grupo: " + e.getMessage());
            }
        }
    }
    
    private boolean perteneceAGrupoEspecifico(Connection conn, int idUsuario, int idGrupo) 
    {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM Miembro_Grupo WHERE id_usuario = ? AND id_grupo = ?")) 
        {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idGrupo);
            try (ResultSet rs = ps.executeQuery()) 
            {
                return rs.next();
            }
        } 
        catch (Exception e) 
        {
            return false;
        }
    }
}