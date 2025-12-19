package com.mycompany.tiendavideojuegos.services;

import com.mycompany.tiendavideojuegos.DTO.BannerDTO;
import com.mycompany.tiendavideojuegos.DTO.CategoriaDTO;
import com.mycompany.tiendavideojuegos.DTO.ConfiguracionDTO;
import com.mycompany.tiendavideojuegos.models.Banner;
import com.mycompany.tiendavideojuegos.models.Categoria;
import com.mycompany.tiendavideojuegos.models.Configuracion;
import java.util.List;

public class AdminService 
{
    private final Categoria modeloCat = new Categoria();
    private final Configuracion modeloConfig = new Configuracion();
    private final Banner modeloBanner = new Banner();
    
    public List<CategoriaDTO> listarCategorias() 
    {
        return modeloCat.listar();
    }
    
    public boolean crearCategoria(CategoriaDTO cat) throws Exception
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
}
