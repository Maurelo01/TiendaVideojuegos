package com.mycompany.tiendavideojuegos.services;

import com.mycompany.tiendavideojuegos.DTO.EmpresaDTO;
import com.mycompany.tiendavideojuegos.DTO.UsuarioComunGamerDTO;
import com.mycompany.tiendavideojuegos.DTO.UsuarioDTO;
import com.mycompany.tiendavideojuegos.DTO.UsuarioEmpresaDTO;
import com.mycompany.tiendavideojuegos.models.Usuario;
import com.mycompany.tiendavideojuegos.models.UsuarioComunGamer;
import com.mycompany.tiendavideojuegos.models.UsuarioEmpresa;
import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.regex.Pattern;

public class UsuariosService 
{
    private final Usuario usuarioModel = new Usuario();
    private final UsuarioComunGamer gamerModel = new UsuarioComunGamer();
    private final UsuarioEmpresa empresaModel = new UsuarioEmpresa();
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern PATTERN = Pattern.compile(EMAIL_REGEX);
    
    public UsuarioDTO login(String correo, String contraseña) throws Exception 
    {
        if (correo == null || correo.trim().isEmpty() || contraseña == null || contraseña.trim().isEmpty()) 
        {
            throw new Exception("El correo y la contraseña son obligatorios.");
        }
        UsuarioDTO usuarioEncontrado = usuarioModel.login(correo, contraseña);
        if (usuarioEncontrado != null && "EMPRESA".equals(usuarioEncontrado.getRol())) 
        {
            if (empresaModel.esEmpresaSuspendida(usuarioEncontrado.getIdUsuario())) 
            {
                throw new Exception("Acceso denegado: Tu empresa ha sido suspendida por la administración.");
            }
            int idEmpresa = usuarioModel.obtenerIdEmpresaPorUsuario(usuarioEncontrado.getIdUsuario());
            usuarioEncontrado.setIdEmpresa(idEmpresa);
        }
        return usuarioEncontrado;
    }
    
    public UsuarioComunGamerDTO registrarGamer(UsuarioComunGamerDTO gamer) throws Exception
    {
        validarCamposGamer(gamer);
        if (!esCorreoValido(gamer.getCorreo())) 
        {
            throw new Exception("El formato del correo electrónico no es válido.");
        }
        if (gamer.getContraseña().length() < 8) 
        {
            throw new Exception("La contraseña debe tener al menos 8 caracteres.");
        }
        if (!esFechaValida(gamer.getFechaNacimiento())) 
        {
            throw new Exception("La fecha de nacimiento no es válida.");
        }
        return gamerModel.registrarGamer(gamer);
    }
    
    public boolean registrarEmpresa(EmpresaDTO empresa, UsuarioEmpresaDTO usuario) throws Exception
    {
        if (empresa.getNombreEmpresa() == null || empresa.getNombreEmpresa().trim().isEmpty()) 
        {
            throw new Exception("El nombre de la empresa es obligatorio.");
        }
        if (empresa.getDescripcion() == null || empresa.getDescripcion().trim().isEmpty()) 
        {
            throw new Exception("La descripción de la empresa es obligatoria.");
        }
        if (usuario.getCorreo() == null || usuario.getCorreo().trim().isEmpty()) 
        {
            throw new Exception("El correo del encargado es obligatorio.");
        }
        if (!esCorreoValido(usuario.getCorreo())) 
        {
            throw new Exception("El correo del encargado no es válido.");
        }
        if (usuario.getNombreEmpleado() == null || usuario.getNombreEmpleado().trim().isEmpty()) 
        {
            throw new Exception("El nombre del empleado es obligatorio.");
        }
        return empresaModel.registrarEmpresaYUsuario(empresa, usuario);
    }
    
    private void validarCamposGamer(UsuarioComunGamerDTO gamer) throws Exception 
    {
        if (gamer.getNickname() == null || gamer.getNickname().trim().isEmpty()) 
        {
            throw new Exception("El nickname es obligatorio.");
        }
        if (gamer.getCorreo() == null || gamer.getCorreo().trim().isEmpty()) 
        {
            throw new Exception("El correo es obligatorio.");
        }
        if (gamer.getContraseña() == null || gamer.getContraseña().trim().isEmpty()) 
        {
            throw new Exception("La contraseña es obligatoria.");
        }
        if (gamer.getFechaNacimiento() == null) 
        {
            throw new Exception("La fecha de nacimiento es obligatoria.");
        }
    }
    
    private boolean esCorreoValido(String correo) 
    {
        return PATTERN.matcher(correo).matches();
    }
    
    private boolean esFechaValida(Date fechaNacimiento) 
    {
        if (fechaNacimiento == null) return false;
        LocalDate fechaNac;
        if (fechaNacimiento instanceof java.sql.Date) 
        {
            fechaNac = ((java.sql.Date) fechaNacimiento).toLocalDate();
        } 
        else 
        {
            fechaNac = fechaNacimiento.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        LocalDate ahora = LocalDate.now();
        if (fechaNac.isAfter(ahora)) return false;
        return true;
    }
    
    public boolean agregarUsuarioAEmpresa(int idEmpresa, UsuarioEmpresaDTO usuario) throws Exception
    {
        if (idEmpresa <= 0) 
        {
            throw new Exception("El ID de la empresa no es válido.");
        }
        
        if (usuario.getCorreo() == null || !esCorreoValido(usuario.getCorreo())) 
        {
            throw new Exception("El correo electrónico no es válido.");
        }
        
        if (usuario.getContraseña() == null || usuario.getContraseña().length() < 8)
        {
            throw new Exception("La contraseña debe tener al menos 8 caracteres.");
        }
        
        if (usuario.getNombreEmpleado() == null || usuario.getNombreEmpleado().trim().isEmpty())
        {
            throw new Exception("El nombre del empleado es obligatorio.");
        }
        
        if (usuario.getFechaNacimiento() == null)
        {
            throw new Exception("La fecha de nacimiento es obligatoria.");
        }
        
        if (!esFechaValida(usuario.getFechaNacimiento())) 
        {
            throw new Exception("La fecha de nacimiento no es válida ya que no puede ser futura.");
        }
        return empresaModel.agregarEmpleado(idEmpresa, usuario);
    }

    public List<UsuarioEmpresaDTO> listarEmpleados(int idEmpresa) throws Exception
    {
        if (idEmpresa <= 0) 
        {
            throw new Exception("El Id de la empresa no es válido.");
        }
        return empresaModel.listarEmpleados(idEmpresa);
    }
    
    public boolean eliminarEmpleado(int idEmpresa, int idUsuarioEmpleado) throws Exception
    {
        if (idEmpresa <= 0 || idUsuarioEmpleado <= 0) 
        {
            throw new Exception("Ids inválidos.");
        }

        if (!empresaModel.esEmpleadoDeEmpresa(idUsuarioEmpleado, idEmpresa)) 
        {
            throw new Exception("El usuario no pertenece a esta empresa o no existe.");
        }

        List<UsuarioEmpresaDTO> empleados = empresaModel.listarEmpleados(idEmpresa);
        if (empleados.size() <= 1) 
        {
            throw new Exception("No se puede eliminar al último empleado ya que la empresa debe mantener al menos un usuario activo.");
        }
        return empresaModel.eliminarUsuario(idUsuarioEmpleado);
    }
    
    public UsuarioComunGamerDTO obtenerPerfilGamer(int idUsuario) throws Exception
    {
        if (idUsuario <= 0) throw new Exception("Id inválido");
        UsuarioComunGamerDTO gamer = gamerModel.obtenerPorId(idUsuario);
        if (gamer == null) throw new Exception("Usuario no encontrado");
        gamer.setContraseña(null); 
        return gamer;
    }
    
    public boolean actualizarPerfilGamer(int idUsuario, UsuarioComunGamerDTO datosNuevos) throws Exception
    {
        if (idUsuario <= 0) throw new Exception("Id inválido");
        if (datosNuevos.getNickname() == null || datosNuevos.getNickname().trim().isEmpty()) 
        {
            throw new Exception("El nickname es obligatorio");
        }
        if (datosNuevos.getContraseña() != null && !datosNuevos.getContraseña().isEmpty()) 
        {
            if (datosNuevos.getContraseña().length() < 8)
            {
                throw new Exception("La nueva contraseña debe tener al menos 8 caracteres");
            }
        }
        datosNuevos.setIdUsuario(idUsuario);
        return gamerModel.actualizarPerfil(datosNuevos);
    }
    
    public EmpresaDTO obtenerPerfilPublicoEmpresa(int idEmpresa) throws Exception
    {
        if (idEmpresa <= 0) throw new Exception("Id de empresa inválido");
        EmpresaDTO empresa = empresaModel.obtenerDatosPublicosEmpresa(idEmpresa);
        if (empresa == null) throw new Exception("Empresa no encontrada");
        return empresa;
    }
    
    public float recargarSaldo(int idUsuario, float monto) throws Exception
    {
        if (idUsuario <= 0) 
        {
            throw new IllegalArgumentException("Id de usuario inválido.");
        }
        if (monto <= 0) 
        {
            throw new IllegalArgumentException("El monto a recargar debe ser mayor a 0.");
        }
        if (monto > 10000) 
        {
            throw new IllegalArgumentException("El monto máximo de recarga es de $10,000, no cabe tanto pisto.");
        }
        boolean exitoso = gamerModel.sumarSaldo(idUsuario, monto);
        if (exitoso) 
        {
            float nuevoSaldo = gamerModel.obtenerSaldo(idUsuario);
            if (nuevoSaldo == 0.0f) 
            {
                throw new Exception("Error al obtener el saldo actualizado.");
            }
            return nuevoSaldo;
        }
        else
        {
            throw new Exception("No se pudo realizar la recarga. Verifica que el usuario exista.");
        }
    }
}
