package com.mycompany.tiendavideojuegos.DTO;

import java.io.Serializable;
import java.util.List;

public class ReporteAnalisisDTO implements Serializable 
{
    private List<AnalisisCategoriaDTO> categoriasFavoritas;
    private List<ComparativaCalificacionDTO> comparativaCalificaciones;

    public List<AnalisisCategoriaDTO> getCategoriasFavoritas()
    {
        return categoriasFavoritas;
    }

    public void setCategoriasFavoritas(List<AnalisisCategoriaDTO> categoriasFavoritas)
    {
        this.categoriasFavoritas = categoriasFavoritas;
    }

    public List<ComparativaCalificacionDTO> getComparativaCalificaciones()
    {
        return comparativaCalificaciones;
    }

    public void setComparativaCalificaciones(List<ComparativaCalificacionDTO> comparativaCalificaciones)
    {
        this.comparativaCalificaciones = comparativaCalificaciones;
    }
}
