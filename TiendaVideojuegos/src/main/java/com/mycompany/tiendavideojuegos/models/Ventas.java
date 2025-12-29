package com.mycompany.tiendavideojuegos.models;

import com.mycompany.tiendavideojuegos.DTO.HistorialComprasDTO;
import com.mycompany.tiendavideojuegos.DTO.ReporteAdminDTO;
import com.mycompany.tiendavideojuegos.DTO.ReporteVentasEmpresaDTO;
import com.mycompany.tiendavideojuegos.DTO.SolicitudCompra;
import com.mycompany.tiendavideojuegos.configuracion.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class Ventas 
{    
    public boolean procesarCompra(SolicitudCompra solicitud) throws Exception 
    {
        Connection conn = ConexionDB.getInstance().getConnection();
        
        // Validar Juego
        float precioJuego = 0;
        String clasificacion = "";
        float comisionEmpresa = -1; 
        
        try (PreparedStatement ps = conn.prepareStatement("SELECT v.precio, v.clasificacion_edad, v.estado, e.porcentaje_comision_especifica FROM Videojuego v JOIN Empresa e ON v.id_empresa = e.id_empresa WHERE v.id_juego = ?"))
        {
            ps.setInt(1, solicitud.getIdJuego());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    if (!"ACTIVO".equals(rs.getString("estado"))) {
                        throw new Exception("El juego no está disponible para la venta.");
                    }
                    precioJuego = rs.getFloat("precio");
                    clasificacion = rs.getString("clasificacion_edad");
                    float comEsp = rs.getFloat("porcentaje_comision_especifica");
                    if (!rs.wasNull()) comisionEmpresa = comEsp;
                } else {
                    throw new Exception("El videojuego no existe.");
                }
            }
        }

        // Validar Saldo y Edad
        float saldoUsuario = 0;
        java.sql.Date fechaNacimiento = null;
        
        try (PreparedStatement ps = conn.prepareStatement("SELECT fecha_nacimiento, saldo_cartera FROM Usuario_Comun_Gamer WHERE id_usuario = ?"))
        {
            ps.setInt(1, solicitud.getIdUsuario());
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    saldoUsuario = rs.getFloat("saldo_cartera");
                    fechaNacimiento = rs.getDate("fecha_nacimiento");
                }
                else
                {
                    throw new Exception("El usuario no existe.");
                }
            }
        }

        // Validar Edad
        LocalDate fechaCompra = (solicitud.getFechaSimulada() != null) ? solicitud.getFechaSimulada().toLocalDate() : LocalDate.now();
        int edad = Period.between(fechaNacimiento.toLocalDate(), fechaCompra).getYears();
        if (!esEdadValida(edad, clasificacion)) 
        {
            throw new Exception("Bloqueo por Edad: Tienes " + edad + " años y el juego es para clasificación " + clasificacion);
        }

        // Validar Saldo
        if (saldoUsuario < precioJuego)
        {
            throw new Exception("Saldo insuficiente. Tienes $" + saldoUsuario + " y requieres $" + precioJuego);
        }

        // Validar Duplicados
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM Biblioteca_Personal WHERE id_gamer = ? AND id_juego = ?"))
        {
            ps.setInt(1, solicitud.getIdUsuario());
            ps.setInt(2, solicitud.getIdJuego());
            if (ps.executeQuery().next()) throw new Exception("Ya posees este juego en tu biblioteca.");
        }

        // Cálculos
        float porcentajeComision = (comisionEmpresa != -1) ? comisionEmpresa : obtenerComisionGlobal(conn);
        float gananciaPlataforma = (precioJuego * porcentajeComision) / 100;
        float gananciaEmpresaFinal = precioJuego - gananciaPlataforma;

        // COMPENSACION

        // Descontar saldo del usuario
        try (PreparedStatement ps = conn.prepareStatement("UPDATE Usuario_Comun_Gamer SET saldo_cartera = saldo_cartera - ? WHERE id_usuario = ?")) 
        {
            ps.setFloat(1, precioJuego);
            ps.setInt(2, solicitud.getIdUsuario());
            if (ps.executeUpdate() == 0)
            {
                throw new Exception("Error al descontar saldo.");
            }
        }

        // Registrar la venta
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO Venta (id_gamer, id_juego, fecha_compra, precio_de_compra, comision_aplicada, ganancia_plataforma, ganancia_empresa) VALUES (?, ?, ?, ?, ?, ?, ?)"))
        {
            ps.setInt(1, solicitud.getIdUsuario());
            ps.setInt(2, solicitud.getIdJuego());
            ps.setDate(3, java.sql.Date.valueOf(fechaCompra));
            ps.setFloat(4, precioJuego);
            ps.setFloat(5, porcentajeComision);
            ps.setFloat(6, gananciaPlataforma);
            ps.setFloat(7, gananciaEmpresaFinal);
            ps.executeUpdate();
        }
        catch (Exception e)
        {
            devolverSaldo(conn, solicitud.getIdUsuario(), precioJuego);
            throw new Exception("Error registrando venta. Saldo devuelto: " + e.getMessage());
        }

        // Agregar juego a la biblioteca
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO Biblioteca_Personal (id_gamer, id_juego, fecha_adquisicion, estado_instalacion) VALUES (?, ?, ?, 'NO_INSTALADO')"))
        {
            ps.setInt(1, solicitud.getIdUsuario());
            ps.setInt(2, solicitud.getIdJuego());
            ps.setDate(3, java.sql.Date.valueOf(fechaCompra));
            ps.executeUpdate();
        }
        catch (Exception e)
        {
            eliminarVenta(conn, solicitud.getIdUsuario(), solicitud.getIdJuego());
            devolverSaldo(conn, solicitud.getIdUsuario(), precioJuego);
            throw new Exception("Error agregando juego a biblioteca. Compra cancelada y saldo devuelto: " + e.getMessage());
        }

        return true;
    }

    private void devolverSaldo(Connection conn, int idUsuario, float monto)
    {
        try (PreparedStatement ps = conn.prepareStatement("UPDATE Usuario_Comun_Gamer SET saldo_cartera = saldo_cartera + ? WHERE id_usuario = ?"))
        {
            ps.setFloat(1, monto);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            System.err.println("Error: No se pudo devolver el saldo: " + e.getMessage());
        }
    }

    private void eliminarVenta(Connection conn, int idUsuario, int idJuego) 
    {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Venta WHERE id_gamer = ? AND id_juego = ? ORDER BY id_venta DESC LIMIT 1"))
        {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idJuego);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            System.err.println("Error: No se pudo eliminar la venta: " + e.getMessage());
        }
    }


    private float obtenerComisionGlobal(Connection conn) 
    {
        try (PreparedStatement ps = conn.prepareStatement("SELECT comision_global_actual FROM Configuracion_Sistema LIMIT 1");ResultSet rs = ps.executeQuery()) 
        {
            if (rs.next()) return rs.getFloat("comision_global_actual");
        }
        catch (Exception e) {}
        return 15.0f;
    }

    private boolean esEdadValida(int edadUsuario, String clasificacionJuego) 
    {
        switch (clasificacionJuego) 
        {
            case "E":
                return true;
            case "T":
                return edadUsuario >= 13;
            case "M":
                return edadUsuario >= 17;
            default:
                return true;
        }
    }
    
    public List<ReporteVentasEmpresaDTO> generarReporteEmpresa(int idEmpresa, String fechaInicio, String fechaFin)
    {
        List<ReporteVentasEmpresaDTO> lista = new ArrayList<>();
        Connection conn = ConexionDB.getInstance().getConnection();
        if (fechaInicio == null || fechaInicio.isEmpty()) fechaInicio = "2020-01-01";
        if (fechaFin == null || fechaFin.isEmpty()) fechaFin = "2030-12-31";

        String operacionSql = "SELECT v.titulo, COUNT(t.id_venta) as cantidad, " +
                            "SUM(t.precio_de_compra) as bruto, " +
                            "SUM(t.ganancia_plataforma) as comision, " +
                            "SUM(t.ganancia_empresa) as neto " +
                            "FROM Venta t " +
                            "JOIN Videojuego v ON t.id_juego = v.id_juego " +
                            "WHERE v.id_empresa = ? AND t.fecha_compra BETWEEN ? AND ? " +
                            "GROUP BY v.id_juego, v.titulo " +
                            "ORDER BY neto DESC";

        try (PreparedStatement ps = conn.prepareStatement(operacionSql))
        {
            ps.setInt(1, idEmpresa);
            ps.setString(2, fechaInicio + " 00:00:00");
            ps.setString(3, fechaFin + " 23:59:59");
            
            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next()) 
                {
                    var item = new ReporteVentasEmpresaDTO();
                    item.setTituloJuego(rs.getString("titulo"));
                    item.setCopiasVendidas(rs.getInt("cantidad"));
                    item.setIngresosBrutos(rs.getFloat("bruto"));
                    item.setComisionPlataforma(rs.getFloat("comision"));
                    item.setGananciaNeta(rs.getFloat("neto"));
                    lista.add(item);
                }
            }
        }
        catch (Exception e)
        {
            System.err.println("Error generando reporte empresa: " + e.getMessage());
        }
        return lista;
    }
    
    public List<ReporteAdminDTO> generarReporteAdmin(String fechaInicio, String fechaFin)
    {
        List<ReporteAdminDTO> lista = new ArrayList<>();
        Connection conn = ConexionDB.getInstance().getConnection();
        if (fechaInicio == null || fechaInicio.isEmpty()) fechaInicio = "2020-01-01";
        if (fechaFin == null || fechaFin.isEmpty()) fechaFin = "2030-12-31";

        String operacionSql = "SELECT e.nombre_empresa, COUNT(t.id_venta) as cantidad, " +
                            "SUM(t.precio_de_compra) as total_ingresos, " +
                            "SUM(t.ganancia_plataforma) as ganancia_plataforma, " +
                            "SUM(t.ganancia_empresa) as ganancia_empresa " +
                            "FROM Venta t " +
                            "JOIN Videojuego v ON t.id_juego = v.id_juego " +
                            "JOIN Empresa e ON v.id_empresa = e.id_empresa " +
                            "WHERE t.fecha_compra BETWEEN ? AND ? " +
                            "GROUP BY e.id_empresa, e.nombre_empresa " +
                            "ORDER BY ganancia_plataforma DESC";

        try (PreparedStatement ps = conn.prepareStatement(operacionSql))
        {
            ps.setString(1, fechaInicio + " 00:00:00");
            ps.setString(2, fechaFin + " 23:59:59");
            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    var item = new ReporteAdminDTO();
                    item.setNombreEmpresa(rs.getString("nombre_empresa"));
                    item.setTotalVentas(rs.getInt("cantidad"));
                    item.setTotalIngresos(rs.getFloat("total_ingresos"));
                    item.setGananciaPlataforma(rs.getFloat("ganancia_plataforma"));
                    item.setGananciaEmpresa(rs.getFloat("ganancia_empresa"));
                    lista.add(item);
                }
            }
        }
        catch (Exception e)
        {
        }
        return lista;
    }
    
    public List<HistorialComprasDTO> obtenerHistorialUsuario(int idUsuario) 
    {
        List<com.mycompany.tiendavideojuegos.DTO.HistorialComprasDTO> lista = new ArrayList<>();
        Connection conn = ConexionDB.getInstance().getConnection();

        String calculoSql = "SELECT v.fecha_compra, j.titulo, v.precio_de_compra " +
                            "FROM Venta v " +
                            "JOIN Videojuego j ON v.id_juego = j.id_juego " +
                            "WHERE v.id_gamer = ? " +
                            "ORDER BY v.fecha_compra DESC";

        try (PreparedStatement ps = conn.prepareStatement(calculoSql)) 
        {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) 
            {
                while (rs.next()) 
                {
                    var item = new com.mycompany.tiendavideojuegos.DTO.HistorialComprasDTO();
                    item.setFechaCompra(rs.getDate("fecha_compra"));
                    item.setTituloJuego(rs.getString("titulo"));
                    item.setPrecioPagado(rs.getFloat("precio_de_compra"));
                    lista.add(item);
                }
            }
        } 
        catch (Exception e) 
        {
            System.err.println("Error obteniendo historial de compras: " + e.getMessage());
        }
        return lista;
    }
}
