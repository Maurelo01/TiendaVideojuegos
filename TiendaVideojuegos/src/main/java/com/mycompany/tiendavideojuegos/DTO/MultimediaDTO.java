package com.mycompany.tiendavideojuegos.DTO;
public class MultimediaDTO 
{
    private int idMedia;
    private int idJuego;
    private String contenido;
    private String tipo;
    
    public int getIdMedia() 
    {
        return idMedia;
    }
    
    public void setIdMedia(int idMedia) 
    {
        this.idMedia = idMedia;
    }
    
    public int getIdJuego() 
    {
        return idJuego;
    }
    
    public void setIdJuego(int idJuego) 
    {
        this.idJuego = idJuego;
    }
    
    public String getTipo() 
    {
        return tipo;
    }
    
    public void setTipo(String tipo) 
    {
        this.tipo = tipo;
    }
    
    public String getContenido()
    {
        return contenido;
    }
    
    public void setContenido(String contenido)
    {
        this.contenido = contenido;
    }
}
