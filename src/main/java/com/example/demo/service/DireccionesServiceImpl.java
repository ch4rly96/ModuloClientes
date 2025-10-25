package com.example.demo.service;

import com.example.demo.model.Direcciones;
import com.example.demo.model.Cliente;
import com.example.demo.repository.DireccionesRepository;
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
