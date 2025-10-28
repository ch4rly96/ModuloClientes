package com.example.demo.service;

import com.example.demo.model.Historial;
import com.example.demo.model.Cliente;
import com.example.demo.repository.HistorialRepository;
import org.apache.commons.lang3.StringUtils;
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
        // Validaciones con Apache Commons Lang
        if (historial.getCliente() == null) {
            throw new IllegalArgumentException("El historial debe estar asociado a un cliente.");
        }

        if (StringUtils.isBlank(historial.getTipoInteraccion())) {
            throw new IllegalArgumentException("Debe especificar el tipo de interacción (consulta, compra, reclamo, devolución, etc).");
        }

        if (StringUtils.isBlank(historial.getDetalle())) {
            throw new IllegalArgumentException("El detalle del historial no puede estar vacío.");
        }

        // Si pasa las validaciones, se guarda
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
