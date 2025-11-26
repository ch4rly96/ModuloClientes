package com.example.demo.service;

import com.example.demo.model.Direcciones;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.DireccionesRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Service
public class DireccionesServiceImpl implements DireccionesService {

    @Autowired
    private DireccionesRepository direccionesRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public Optional<Direcciones> obtenerPorId(Long id) {
        return direccionesRepository.findById(id);
    }

    @Override
    public Direcciones guardarDireccion(Direcciones direccion) {
        validarDireccion(direccion);
        return direccionesRepository.save(direccion);
    }

    private void validarDireccion(Direcciones direccion) {
        if (direccion.getCliente() == null || direccion.getCliente().getIdCliente() == null) {
            throw new IllegalArgumentException("La dirección debe estar asociada a un cliente válido.");
        }

        if (direccion.getTipoDireccion() == null ||
                StringUtils.isBlank(direccion.getTipoDireccion().toString())) {
            throw new IllegalArgumentException("Debe seleccionar el tipo de dirección.");
        }

        if (StringUtils.isBlank(direccion.getDireccion())) {
            throw new IllegalArgumentException("La dirección no puede estar vacía.");
        }

        Long idCliente = direccion.getCliente().getIdCliente();

        // Validar solo 1 PRINCIPAL
        if ("PRINCIPAL".equals(direccion.getTipoDireccion())) {
            long count = direccionesRepository.countByClienteIdClienteAndTipoDireccion(idCliente, "PRINCIPAL");
            if (direccion.getIdDireccion() == null && count > 0) {
                throw new IllegalArgumentException("Ya existe una dirección principal.");
            }
            if (direccion.getIdDireccion() != null && count > 1) {
                throw new IllegalArgumentException("No se puede tener más de una dirección principal.");
            }
        }

        if (!clienteRepository.existsById(idCliente)) {
            throw new IllegalArgumentException("El cliente no existe.");
        }
    }

    @Override
    public List<Direcciones> listarPorCliente(Long idCliente) {
        return direccionesRepository.findByClienteIdCliente(idCliente);
    }

    @Override
    public Optional<Direcciones> obtenerPrincipal(Long idCliente) {
        return direccionesRepository.findByClienteIdClienteAndTipoDireccion(idCliente, "PRINCIPAL");
    }

    @Override
    public List<Direcciones> listarPorTipo(String tipo) {
        return direccionesRepository.findByTipoDireccion(tipo);
    }

    @Override
    public void eliminarDireccion(Long idDireccion, Long idCliente) {
        Direcciones dir = direccionesRepository.findById(idDireccion)
                .orElseThrow(() -> new IllegalArgumentException("Dirección no encontrada."));

        if (!dir.getCliente().getIdCliente().equals(idCliente)) {
            throw new IllegalArgumentException("No puedes eliminar direcciones de otro cliente.");
        }

        if ("PRINCIPAL".equals(dir.getTipoDireccion())) {
            throw new IllegalArgumentException("No se puede eliminar la dirección principal.");
        }

        direccionesRepository.delete(dir);
    }
}
