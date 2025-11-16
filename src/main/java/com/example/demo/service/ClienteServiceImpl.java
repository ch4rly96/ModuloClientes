package com.example.demo.service;

import com.example.demo.model.Cliente;
import com.example.demo.repository.ClienteRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    // === GUARDAR CLIENTE CON VALIDACIÓN ===
    @Override
    public Cliente guardarCliente(Cliente cliente) {
        validarCliente(cliente);
        return clienteRepository.save(cliente);
    }

    private void validarCliente(Cliente cliente) {
        if (StringUtils.isBlank(cliente.getCliente())) {
            throw new IllegalArgumentException("Debe seleccionar si es persona o empresa.");
        }

        if ("persona".equalsIgnoreCase(cliente.getCliente())) {
            if (!"natural".equals(cliente.getTipoCliente())) {
                throw new IllegalArgumentException("Una persona debe ser de tipo 'natural'.");
            }
            if (StringUtils.isBlank(cliente.getNombres()) || StringUtils.isBlank(cliente.getApellidos())) {
                throw new IllegalArgumentException("Nombre y apellido son obligatorios para personas.");
            }
            cliente.setRazonSocial(null);
        }
        else if ("empresa".equalsIgnoreCase(cliente.getCliente())) {
            if (!List.of("juridico", "constructor", "corporativo").contains(cliente.getTipoCliente())) {
                throw new IllegalArgumentException("Tipo de empresa inválido. Use: jurídico, constructor o corporativo.");
            }
            if (StringUtils.isBlank(cliente.getRazonSocial())) {
                throw new IllegalArgumentException("La razón social es obligatoria para empresas.");
            }
            cliente.setNombres(null);
            cliente.setApellidos(null);
        }

        // Validar tipo de documento
        if ("persona".equalsIgnoreCase(cliente.getCliente()) &&
                !List.of("DNI", "CE").contains(cliente.getTipoDocumento())) {
            throw new IllegalArgumentException("Personas solo pueden tener DNI o CE.");
        }
        if ("empresa".equalsIgnoreCase(cliente.getCliente()) && !"RUC".equals(cliente.getTipoDocumento())) {
            throw new IllegalArgumentException("Empresas deben tener RUC.");
        }
    }

    // === LISTADOS ===
    @Override
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    @Override
    public List<Cliente> listarActivos() {
        return clienteRepository.findByEstadoTrue();
    }

    @Override
    public List<Cliente> listarMorosos() {
        return clienteRepository.findByEsMorosoTrueOrderByDeudaActualDesc();
    }

    @Override
    public List<Cliente> listarPorTipo(String cliente, String tipoCliente) {
        return clienteRepository.findByClienteAndTipoCliente(cliente, tipoCliente);
    }

    // === BÚSQUEDA ===
    @Override
    public Optional<Cliente> obtenerPorId(Long id) {
        return clienteRepository.findById(id);
    }

    @Override
    public Cliente obtenerPorDocumento(String documentoIdentidad) {
        return clienteRepository.findByDocumentoIdentidad(documentoIdentidad);
    }

    @Override
    public List<Cliente> buscarPorNombre(String texto) {
        return clienteRepository.findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(texto, texto);
    }

    @Override
    public List<Cliente> buscarPorRazonSocial(String texto) {
        return clienteRepository.findByRazonSocialContainingIgnoreCase(texto);
    }

    // === ESTADO Y ELIMINACIÓN ===
    @Override
    public void eliminarCliente(Long id) {
        clienteRepository.deleteById(id);
    }

    @Override
    public void cambiarEstado(Long id, boolean estado) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + id));
        cliente.setEstado(estado);
        clienteRepository.save(cliente);
    }

    // === GESTIÓN DE DEUDA (ERP) ===
    @Override
    public void actualizarDeuda(Long idCliente, BigDecimal monto) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + idCliente));

        BigDecimal nuevaDeuda = cliente.getDeudaActual().add(monto);
        if (nuevaDeuda.compareTo(BigDecimal.ZERO) < 0) nuevaDeuda = BigDecimal.ZERO;

        cliente.setDeudaActual(nuevaDeuda);
        cliente.setEsMoroso(nuevaDeuda.compareTo(BigDecimal.ZERO) > 0);
        clienteRepository.save(cliente);
    }

    @Override
    public void registrarVenta(Long idCliente, BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto de venta debe ser positivo.");
        }
        actualizarDeuda(idCliente, monto);
    }

    @Override
    public void registrarPago(Long idCliente, BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto de pago debe ser positivo.");
        }
        actualizarDeuda(idCliente, monto.negate());
    }
}
