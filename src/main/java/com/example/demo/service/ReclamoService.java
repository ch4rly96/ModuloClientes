package com.example.demo.service;

import com.example.demo.model.Reclamo;
import java.util.List;

public interface ReclamoService {

    Reclamo crearReclamo(Reclamo reclamo, String usuarioNombre);

    Reclamo obtenerPorId(Long id);

    Reclamo actualizarReclamo(Reclamo reclamo);

    List<Reclamo> listarTodos();

    List<Reclamo> listarPorCliente(Long idCliente);

    List<Reclamo> listarPorEstado(String estado);

    List<Reclamo> buscarPorNumero(String numero);

    List<Reclamo> buscar(String q, String estado);

    Reclamo actualizarEstado(Long id, String nuevoEstado, String solucion, String usuarioNombre);

    void eliminarReclamo(Long id);
}
