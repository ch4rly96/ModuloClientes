package com.example.demo.service;

import com.example.demo.model.Historial;
import com.example.demo.model.Cliente;
import com.example.demo.repository.HistorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HistorialServiceImpl implements HistorialService{

    @Autowired
    private HistorialRepository historialRepository;

    @Override
    public Historial guardarHistorial(Historial historial) {
        return historialRepository.save(historial);
    }

    @Override
    public List<Historial> listarHistoriales() {
        return historialRepository.findAll();
    }

    @Override
    public Optional<Historial> obtenerPorId(Long id) {
        return historialRepository.findById(id);
    }

    @Override
    public List<Historial> listarPorCliente(Cliente cliente) {
        return historialRepository.findByCliente(cliente);
    }

    @Override
    public List<Historial> listarPorTipo(String tipoInteraccion) {
        return historialRepository.findByTipoInteraccion(tipoInteraccion);
    }

    @Override
    public void eliminarHistorial(Long id) {
        historialRepository.deleteById(id);
    }
}
