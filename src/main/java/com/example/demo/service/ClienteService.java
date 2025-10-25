package com.example.demo.service;

import com.example.demo.model.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteService {
    // Crear o actualizar cliente
    Cliente guardarCliente(Cliente cliente);

    // Obtener todos los clientes
    List<Cliente> listarClientes();

    // Buscar cliente por ID
    Optional<Cliente> obtenerPorId(Long id);

    // Buscar cliente por documento
    Cliente obtenerPorDocumento(String documentoIdentidad);

    // Eliminar cliente por ID
    void eliminarCliente(Long id);

    // Cambiar estado (activar o desactivar)
    void cambiarEstado(Long id, boolean estado);
}
