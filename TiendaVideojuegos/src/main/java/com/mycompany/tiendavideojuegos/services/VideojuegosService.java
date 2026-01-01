package com.mycompany.tiendavideojuegos.services;

import com.mycompany.tiendavideojuegos.DTO.CategoriaDTO;
import com.mycompany.tiendavideojuegos.DTO.MultimediaDTO;
import com.mycompany.tiendavideojuegos.DTO.VideojuegosDTO;
import com.mycompany.tiendavideojuegos.models.Videojuegos;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VideojuegosService 
{
    private final Videojuegos modelo = new Videojuegos();
    private static final List<String> CLASIFICACIONES_VALIDAS = Arrays.asList("E", "T", "M");
    
    public boolean publicarJuego(VideojuegosDTO juego) throws Exception
    {
        if (juego.getImagen() == null || juego.getImagen().isEmpty()) 
        {
            throw new Exception("Es obligatorio subir una imagen de portada.");
        }
        validarDatosComunes(juego);
        return modelo.publicar(juego);
    }
    
    public List<VideojuegosDTO> obtenerJuegosPorEmpresa(int idEmpresa) throws Exception
    {
        if (idEmpresa <= 0) 
        {
            throw new Exception("El id de la empresa no es válido.");
        }
        return modelo.listarPorEmpresa(idEmpresa);
    }

    public boolean editarJuego(VideojuegosDTO juego) throws Exception
    {
        if (juego.getIdJuego() <= 0) 
        {
            throw new Exception("El id del juego es inválido.");
        }
        validarDatosComunes(juego);
        return modelo.editar(juego);
    }
    
    private void validarDatosComunes(VideojuegosDTO juego) throws Exception 
    {
        if (juego.getTitulo() == null || juego.getTitulo().trim().isEmpty())
        {
            throw new Exception("El título del videojuego es obligatorio.");
        }
        
        if (juego.getTitulo().length() > 150) 
        {
            throw new Exception("El título no puede exceder los 150 caracteres.");
        }

        if (juego.getDescripcion() == null || juego.getDescripcion().trim().isEmpty()) 
        {
            throw new Exception("La descripción es obligatoria.");
        }
        
        if (juego.getDescripcion().length() > 250) 
        {
            throw new Exception("La descripción no puede exceder los 250 caracteres.");
        }

        if (juego.getPrecio() < 0) 
        {
            throw new Exception("El precio no puede ser negativo.");
        }

        if (juego.getRecursosMinimos() == null || juego.getRecursosMinimos().trim().isEmpty()) 
        {
            throw new Exception("Debe especificar los recursos mínimos del sistema.");
        }
        
        if (juego.getClasificacionEdad() == null || !CLASIFICACIONES_VALIDAS.contains(juego.getClasificacionEdad().toUpperCase())) 
        {
            throw new Exception("Clasificación de edad inválida. Valores permitidos: " + CLASIFICACIONES_VALIDAS);
        }
        
        if (juego.getIdEmpresa() <= 0) 
        {
            throw new Exception("El juego debe estar asociado a una empresa válida.");
        }
        
        if (juego.getEstado() == null || juego.getEstado().isEmpty()) 
        {
            juego.setEstado("ACTIVO");
        }
    }
    
    public List<VideojuegosDTO> obtenerJuegosPublicos() 
    {
        List<VideojuegosDTO> lista = modelo.listarActivos();
        if (lista == null) 
        {
            return new ArrayList<>(); 
        }   
        return modelo.listarActivos();
    }
    
    public boolean suspenderJuego(int idJuego) throws Exception
    {
        if (idJuego <= 0) 
        {
            throw new Exception("Id de juego inválido.");
        }
        return modelo.actualizarEstado(idJuego, "SUSPENDIDO");
    }
    
    public boolean activarJuego(int idJuego) throws Exception
    {
        if (idJuego <= 0) 
        {
            throw new Exception("Id de juego inválido.");
        }
        return modelo.actualizarEstado(idJuego, "ACTIVO");
    }
    
    public boolean agregarMultimedia(int idJuego, List<MultimediaDTO> multimediaList) throws Exception
    {
        if (idJuego <= 0) throw new Exception("Id de juego inválido.");
        if (multimediaList == null || multimediaList.isEmpty()) throw new Exception("La lista de multimedia está vacía.");
        return modelo.agregarMultimedia(idJuego, multimediaList);
    }
    
    public List<MultimediaDTO> listarMultimedia(int idJuego) throws Exception
    {
        if (idJuego <= 0)
        {
            throw new Exception("Id de juego inválido");
        }
        return modelo.obtenerMultimedia(idJuego);
    }
    
    public boolean eliminarMultimedia(int idMedia) throws Exception 
    {
        if (idMedia <= 0) 
        {
            throw new Exception("El Id del multimedia no es válido.");
        }
        return modelo.eliminarMultimedia(idMedia);
    }
    
    public List<VideojuegosDTO> buscar(String titulo, Integer idCategoria) 
    {
        return modelo.buscarJuegos(titulo, idCategoria);
    }
    
    public VideojuegosDTO obtenerJuegoPorId(int id) throws Exception
    {
        if (id <= 0) throw new Exception("Id inválido");
        VideojuegosDTO juego = modelo.obtenerPorId(id);
        if (juego == null) throw new Exception("Juego no encontrado");
        return juego;
    }
    
    public List<CategoriaDTO> obtenerCategorias() throws Exception
    {
        return modelo.obtenerTodasLasCategorias(); 
    }
}
