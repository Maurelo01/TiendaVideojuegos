package com.mycompany.tiendavideojuegos.DTO;
public class EmpresaDTO 
{
    private int idEmpresa;
    private String nombreEmpresa;
    private String descripcion;
    private float porcentajeComisionEspecifica;
    private String imagenBanner;
    
    public int getIdEmpresa() 
    { 
        return idEmpresa; 
    }
    
    public void setIdEmpresa(int idEmpresa) 
    { 
        this.idEmpresa = idEmpresa; 
    }
    
    public String getNombreEmpresa() 
    { 
        return nombreEmpresa; 
    }
    
    public void setNombreEmpresa(String nombreEmpresa) 
    { 
        this.nombreEmpresa = nombreEmpresa;
    }
    
    public String getDescripcion()
    {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion)
    {
        this.descripcion = descripcion;
    }
    
    public float getPorcentajeComisionEspecifica()
    {
        return porcentajeComisionEspecifica;
    }
    
    public void setPorcentajeComisionEspecifica(float porcentajeComisionEspecifica)
    {
        this.porcentajeComisionEspecifica = porcentajeComisionEspecifica;
    }
    
    public String getImagenBanner()
    {
        return imagenBanner;
    }
    
    public void setImagenBanner(String imagenBanner)
    {
        this.imagenBanner = imagenBanner;
    }
}
