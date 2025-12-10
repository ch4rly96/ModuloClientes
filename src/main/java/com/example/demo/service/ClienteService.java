package com.example.demo.service;

import com.example.demo.model.Cliente;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ClienteService {
    // === CRUD BÁSICO ===
    Cliente guardarCliente(Cliente cliente);
    List<Cliente> listarClientes();
    Optional<Cliente> obtenerPorId(Long id);
    Cliente obtenerPorDocumento(String documentoIdentidad);
    void eliminar(Long id);
    void cambiarEstado(Long id, boolean estado);

    // === FILTROS ERP ===
    List<Cliente> listarActivos();
    List<Cliente> listarMorosos();
    List<Cliente> listarPorTipo(String tipoCliente, String subtipoCliente);

    long contarClientesPorTipo(String tipoCliente);
    long contarClientesPorSubtipo(String tipoCliente, String subtipoCliente);

    // === BÚSQUEDA AVANZADA ===
    List<Cliente> buscarPorNombre(String texto);
    List<Cliente> buscarPorRazonSocial(String texto);

    // === GESTIÓN DE DEUDA ===
    void registrarVenta(Long idCliente, BigDecimal monto);
    void registrarPago(Long idCliente, BigDecimal monto);
    void actualizarDeuda(Long idCliente, BigDecimal monto);
}
