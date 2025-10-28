package com.example.demo.service;

import com.example.demo.model.Cliente;
import com.example.demo.repository.ClienteRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public Cliente guardarCliente(Cliente cliente) {
        // Validaciones usando Apache Commons Lang
        if ("Persona".equalsIgnoreCase(cliente.getTipoCliente())) {
            if (StringUtils.isBlank(cliente.getNombres())) {
                throw new IllegalArgumentException("El nombre del cliente no puede estar vacío.");
            }
            if (StringUtils.isBlank(cliente.getApellidos())) {
                throw new IllegalArgumentException("El apellido del cliente no puede estar vacío.");
            }
        } else if ("Empresa".equalsIgnoreCase(cliente.getTipoCliente())) {
            if (StringUtils.isBlank(cliente.getRazonSocial())) {
                throw new IllegalArgumentException("La razón social no puede estar vacía.");
            }
        }

        // Si pasa las validaciones, se guarda
        return clienteRepository.save(cliente);
    }

    @Override
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    @Override
    public Optional<Cliente> obtenerPorId(Long id) {
        return clienteRepository.findById(id);
    }

    @Override
    public Cliente obtenerPorDocumento(String documentoIdentidad) {
        return clienteRepository.findByDocumentoIdentidad(documentoIdentidad);
    }

    @Override
    public void eliminarCliente(Long id) {
        clienteRepository.deleteById(id);
    }

    @Override
    public void cambiarEstado(Long id, boolean estado) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(id);
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            cliente.setEstado(estado);
            clienteRepository.save(cliente);
        }
    }
}
