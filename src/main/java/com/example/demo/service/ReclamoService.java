package com.example.demo.service;

import com.example.demo.model.Reclamo;
import java.util.List;

public interface ReclamoService {

    Reclamo crearReclamo(Reclamo reclamo, String usuarioNombre);

    Reclamo obtenerPorId(Long id);

    List<Reclamo> listarTodos();

    List<Reclamo> listarPorCliente(Long idCliente);

    List<Reclamo> listarPorEstado(String estado);

    List<Reclamo> buscarPorNumero(String numero);

    Reclamo actualizarEstado(Long id, String nuevoEstado, String solucion, String usuarioNombre);

    void eliminarReclamo(Long id);
}
