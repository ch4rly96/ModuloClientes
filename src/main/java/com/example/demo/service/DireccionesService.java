package com.example.demo.service;

import com.example.demo.model.Direcciones;
import com.example.demo.model.Cliente;
import java.util.List;
import java.util.Optional;

public interface DireccionesService {
    Direcciones guardarDireccion(Direcciones direccion);

    List<Direcciones> listarDirecciones();

    Optional<Direcciones> obtenerPorId(Long id);

    List<Direcciones> listarPorCliente(Cliente cliente);

    List<Direcciones> listarPorTipo(String tipoDireccion);

    void eliminarDireccion(Long id);
}
