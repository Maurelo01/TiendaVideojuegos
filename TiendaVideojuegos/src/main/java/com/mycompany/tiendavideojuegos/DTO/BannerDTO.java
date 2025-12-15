package com.mycompany.tiendavideojuegos.DTO;
public class BannerDTO 
{
    private int idBanner;
    private int idJuego;
    private String titulo;
    private String imagenUrl;
    private int lugar;
    private String estado;
    private String nombreJuego;
    
    public int getIdBanner()
    {
        return idBanner;
    }
    
    public void setIdBanner(int idBanner) 
    {
        this.idBanner = idBanner;
    }
    
    public int getIdJuego() 
    {
        return idJuego;
    }
    
    public void setIdJuego(int idJuego) 
    {
        this.idJuego = idJuego;
    }
    
    public String getTitulo() 
    {
        return titulo;
    }
    
    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
    }
    
    public String getImagenUrl() 
    {
        return imagenUrl;
    }
    
    public void setImagenUrl(String imagenUrl) 
    {
        this.imagenUrl = imagenUrl;
    }
    
    public int getLugar() 
    {
        return lugar;
    }
    
    public void setLugar(int lugar) 
    {
        this.lugar = lugar;
    }
    
    public String getEstado() 
    {
        return estado;
    }
    
    public void setEstado(String estado) 
    {
        this.estado = estado;
    }
    
    public String getNombreJuego() 
    {
        return nombreJuego;
    }
    
    public void setNombreJuego(String nombreJuego) 
    {
        this.nombreJuego = nombreJuego;
    }
}
