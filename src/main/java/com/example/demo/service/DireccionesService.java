package com.example.demo.service;

import com.example.demo.model.Direcciones;
import com.example.demo.model.Cliente;
import java.util.List;
import java.util.Optional;

public interface DireccionesService {
    Direcciones guardarDireccion(Direcciones direccion);
    Optional<Direcciones> obtenerPorId(Long id);
    List<Direcciones> listarPorCliente(Long idCliente);
    List<Direcciones> listarPorTipo(String tipo);  // ← String
    Optional<Direcciones> obtenerPrincipal(Long idCliente);
    void eliminarDireccion(Long idDireccion, Long idCliente);
}
