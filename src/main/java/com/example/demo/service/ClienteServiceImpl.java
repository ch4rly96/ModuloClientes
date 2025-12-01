package com.example.demo.service;

import com.example.demo.model.Cliente;
import com.example.demo.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public Cliente guardarCliente(Cliente cliente) {
        validarCliente(cliente);
        return clienteRepository.save(cliente);
    }

    private void validarCliente(Cliente cliente) {

        if (StringUtils.isBlank(cliente.getTipoCliente())) {
            throw new IllegalArgumentException("Debe seleccionar si es persona o empresa.");
        }

        // === VALIDAR PERSONA ===
        if ("persona".equalsIgnoreCase(cliente.getTipoCliente())) {

            if (!"natural".equalsIgnoreCase(cliente.getSubtipoCliente())) {
                throw new IllegalArgumentException("Una persona solo puede ser subtipo NATURAL.");
            }

            if (StringUtils.isBlank(cliente.getNombres()) ||
                    StringUtils.isBlank(cliente.getApellidos())) {
                throw new IllegalArgumentException("Nombre y apellido son obligatorios para personas.");
            }

            if (!List.of("DNI", "CE").contains(cliente.getTipoDocumento())) {
                throw new IllegalArgumentException("Personas solo pueden tener DNI o CE.");
            }
        }

        // === VALIDAR EMPRESA ===
        if ("empresa".equalsIgnoreCase(cliente.getTipoCliente())) {

            if (!List.of("juridico", "constructor", "corporativo")
                    .contains(cliente.getSubtipoCliente())) {
                throw new IllegalArgumentException("Subtipo inválido para empresa.");
            }

            if (StringUtils.isBlank(cliente.getRazonSocial())) {
                throw new IllegalArgumentException("La razón social es obligatoria para empresas.");
            }

            if (!"RUC".equals(cliente.getTipoDocumento())) {
                throw new IllegalArgumentException("Empresas deben tener RUC.");
            }
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
    public List<Cliente> listarPorTipo(String tipoCliente, String subtipoCliente) {
        if (subtipoCliente == null || subtipoCliente.isBlank()) {
            return clienteRepository.findByTipoCliente(tipoCliente);
        }
        return clienteRepository.findByTipoClienteAndSubtipoCliente(tipoCliente, subtipoCliente);
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
        return texto == null || texto.isBlank()
                ? clienteRepository.findAll()
                : clienteRepository.buscarPorNombre("%" + texto.trim() + "%");
    }

    @Override
    public List<Cliente> buscarPorRazonSocial(String texto) {
        return texto == null || texto.isBlank()
                ? clienteRepository.findAll()
                : clienteRepository.buscarPorRazonSocial("%" + texto.trim() + "%");
    }

    // === ESTADO Y ELIMINACIÓN ===
    @Override
    public void eliminar(Long id) {
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
