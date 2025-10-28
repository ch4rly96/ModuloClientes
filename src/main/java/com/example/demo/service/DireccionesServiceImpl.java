package com.example.demo.service;

import com.example.demo.model.Direcciones;
import com.example.demo.model.Cliente;
import com.example.demo.repository.DireccionesRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Service
public class DireccionesServiceImpl implements DireccionesService{

    @Autowired
    private DireccionesRepository direccionesRepository;

    @Override
    public Direcciones guardarDireccion(Direcciones direccion) {
        // Validaciones con Apache Commons Lang
        if (direccion.getCliente() == null) {
            throw new IllegalArgumentException("La dirección debe estar asociada a un cliente.");
        }

        if (StringUtils.isBlank(direccion.getDireccionCompleta())) {
            throw new IllegalArgumentException("La dirección no puede estar vacía.");
        }

        if (StringUtils.isBlank(direccion.getTipoDireccion())) {
            throw new IllegalArgumentException("Debe especificar el tipo de dirección (principal, envío, etc).");
        }

        // Si pasa las validaciones, se guarda
        return direccionesRepository.save(direccion);
    }

    @Override
    public List<Direcciones> listarDirecciones() {
        return direccionesRepository.findAll();
    }

    @Override
    public Optional<Direcciones> obtenerPorId(Long id) {
        return direccionesRepository.findById(id);
    }

    @Override
    public List<Direcciones> listarPorCliente(Cliente cliente) {
        return direccionesRepository.findByCliente(cliente);
    }

    @Override
    public List<Direcciones> listarPorTipo(String tipoDireccion) {
        return direccionesRepository.findByTipoDireccion(tipoDireccion);
    }

    @Override
    public void eliminarDireccion(Long id) {
        direccionesRepository.deleteById(id);
    }
}
