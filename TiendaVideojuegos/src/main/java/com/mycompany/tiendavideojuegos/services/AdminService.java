package com.mycompany.tiendavideojuegos.services;

import com.mycompany.tiendavideojuegos.DTO.BannerDTO;
import com.mycompany.tiendavideojuegos.DTO.CategoriaDTO;
import com.mycompany.tiendavideojuegos.DTO.ConfiguracionDTO;
import com.mycompany.tiendavideojuegos.DTO.EmpresaDTO;
import com.mycompany.tiendavideojuegos.models.Banner;
import com.mycompany.tiendavideojuegos.models.Categoria;
import com.mycompany.tiendavideojuegos.models.Configuracion;
import com.mycompany.tiendavideojuegos.models.UsuarioEmpresa;
import java.util.List;

public class AdminService 
{
    private final Categoria modeloCat = new Categoria();
    private final Configuracion modeloConfig = new Configuracion();
    private final Banner modeloBanner = new Banner();
    private final UsuarioEmpresa modeloEmpresa = new UsuarioEmpresa();
    
    public List<CategoriaDTO> listarCategorias() 
    {
        return modeloCat.listar();
    }
    
    public boolean crearCategoria(CategoriaDTO cat) throws Exception
    {
        validarDatosComunes(cat);
        return modeloCat.crear(cat);
    }
    
    public ConfiguracionDTO obtenerConfiguracion() 
    {
        return modeloConfig.obtenerConfiguracion();
    }
    
    public boolean actualizarComisionGlobal(float nuevaComision) 
    {
        if (nuevaComision < 0 || nuevaComision > 100) 
        {
            return false;
        }
        return modeloConfig.actualizarComisionGlobal(nuevaComision);
    }
    
    public List<BannerDTO> listarBannersActivos() 
    {
        return modeloBanner.listarBannersActivos();
    }
    
    public boolean agregarBanner(BannerDTO banner) 
    {
        return modeloBanner.agregar(banner);
    }
    
    public boolean editarCategoria(CategoriaDTO cat, boolean forzarEdicion) throws Exception
    {
        if (cat.getIdCategoria() <= 0) 
        {
            throw new Exception("El id de la categoría no es válido.");
        }
        validarDatosComunes(cat);
        int juegosAfectados = modeloCat.contarJuegosPorCategoria(cat.getIdCategoria());
        if (juegosAfectados > 0 && !forzarEdicion) 
        {
            throw new Exception("Advertencia: Estás editando una categoría usada por " + juegosAfectados + " juego(s). El cambio se mostrara en todos ellos, ¿Deseas continuar?");
        }
        return modeloCat.actualizar(cat);
    }
    
    public boolean eliminarCategoria(int idCategoria, boolean forzarBorrado) throws Exception
    {
        if (idCategoria <= 0) 
        {
            throw new Exception("Id inválido.");
        }
        int juegosAfectados = modeloCat.contarJuegosPorCategoria(idCategoria);
        if (juegosAfectados > 0 && !forzarBorrado) 
        {
            throw new Exception("Advertencia: Esta categoría está asignada a " + juegosAfectados +" juego(s). Si se elimina se desvinculará de ellos, ¿Desea continuar?");
        }
        return modeloCat.eliminar(idCategoria);
    }
    
    private void validarDatosComunes(CategoriaDTO cat) throws Exception 
    {
        if (cat.getNombreCategoria() == null || cat.getNombreCategoria().trim().isEmpty()) 
        {
            throw new Exception("El nombre de la categoría es obligatorio.");
        }
        if (cat.getNombreCategoria().length() > 150) 
        {
            throw new Exception("El nombre de la categoría no puede exceder los 150 caracteres.");
        }
        if (modeloCat.existe(cat.getNombreCategoria())) 
        {
            throw new Exception("Ya existe una categoría con el nombre: " + cat.getNombreCategoria());
        }
    }
    
    public List<EmpresaDTO> listarEmpresas() 
    {
        return modeloEmpresa.listarTodas();
    }
    
    public boolean editarEmpresa(EmpresaDTO empresa) throws Exception
    {
        if (empresa.getIdEmpresa() <= 0) throw new Exception("Id inválido");
        if (empresa.getNombreEmpresa() == null || empresa.getNombreEmpresa().trim().isEmpty())
        {
            throw new Exception("El nombre es obligatorio");
        }
        return modeloEmpresa.actualizarEmpresa(empresa);
    }
    
    public boolean suspenderEmpresa(int idEmpresa) throws Exception
    {
        if (idEmpresa <= 0) throw new Exception("Id inválido");
        return modeloEmpresa.cambiarEstado(idEmpresa, "SUSPENDIDO");
    }
    
    public boolean activarEmpresa(int idEmpresa) throws Exception
    {
        if (idEmpresa <= 0) throw new Exception("Id inválido");
        return modeloEmpresa.cambiarEstado(idEmpresa, "ACTIVO");
    }
}
