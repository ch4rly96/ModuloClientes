package com.example.demo.service;

import com.example.demo.model.Historial;
import com.example.demo.model.Cliente;
import java.util.List;
import java.util.Optional;

public interface HistorialService {
    Historial guardarHistorial(Historial historial);

    List<Historial> listarHistoriales();

    Optional<Historial> obtenerPorId(Long id);

    List<Historial> listarPorCliente(Cliente cliente);

    List<Historial> listarPorTipo(String tipoInteraccion);

    void eliminarHistorial(Long id);
}
