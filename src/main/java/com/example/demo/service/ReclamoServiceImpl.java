package com.example.demo.service;

import com.example.demo.model.Reclamo;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.ReclamoRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReclamoServiceImpl implements ReclamoService  {
    @Autowired
    private ReclamoRepository reclamoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public Reclamo crearReclamo(Reclamo reclamo, String usuarioNombre) {
        validarReclamo(reclamo);
        reclamo.setCreadoPorNombre(usuarioNombre);
        reclamo.setNumeroReclamo(generarNumeroReclamo());
        return reclamoRepository.save(reclamo);
    }

    @Override
    public Reclamo actualizarEstado(Long id, String nuevoEstado, String solucion, String usuarioNombre) {
        Reclamo reclamo = obtenerPorId(id);
        reclamo.setEstado(nuevoEstado);
        reclamo.setSolucion(solucion);
        reclamo.setActualizadoPorNombre(usuarioNombre);

        if ("RESUELTO".equals(nuevoEstado) || "CERRADO".equals(nuevoEstado)) {
            reclamo.setFechaResolucion(LocalDateTime.now());
        }
        return reclamoRepository.save(reclamo);
    }

    private String generarNumeroReclamo() {
        Reclamo ultimo = reclamoRepository.findTopByOrderByIdReclamoDesc();
        int siguiente = (ultimo != null && ultimo.getIdReclamo() != null) ? ultimo.getIdReclamo().intValue() + 1 : 1;
        return "REC-" + LocalDateTime.now().getYear() + "-" + String.format("%04d", siguiente);
    }

    private void validarReclamo(Reclamo reclamo) {
        if (reclamo.getCliente() == null || reclamo.getCliente().getIdCliente() == null) {
            throw new IllegalArgumentException("Cliente requerido.");
        }
        if (!clienteRepository.existsById(reclamo.getCliente().getIdCliente())) {
            throw new IllegalArgumentException("Cliente no existe.");
        }
        if (StringUtils.isBlank(reclamo.getMotivo())) {
            throw new IllegalArgumentException("Motivo requerido.");
        }
        if (StringUtils.isBlank(reclamo.getDescripcion())) {
            throw new IllegalArgumentException("Descripción requerida.");
        }
    }

    @Override public Reclamo obtenerPorId(Long id) {
        return reclamoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reclamo no encontrado con id: " + id));
    }

    public static String quitarTildes(String texto) {
        if (texto == null) return null;
        return java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    @Override
    public List<Reclamo> buscar(String q, String estado) {
        if (q != null) {
            q = quitarTildes(q).toLowerCase();
        }

        if ((q == null || q.isEmpty()) && (estado == null || estado.equals("todos"))) {
            return reclamoRepository.findAllByOrderByFechaReclamoDesc();
        }

        if (estado == null || estado.equals("todos")) {
            return reclamoRepository.buscarPorCliente(q);
        }

        if (q == null || q.isEmpty()) {
            return reclamoRepository.findByEstadoOrderByFechaReclamoDesc(estado);
        }

        return reclamoRepository.buscarPorClienteYEstado(q, estado);
    }

    @Override public List<Reclamo> listarTodos() {
        return reclamoRepository.findAll();
    }

    @Override public List<Reclamo> listarPorCliente(Long idCliente) {
        return reclamoRepository.findByClienteIdClienteOrderByFechaReclamoDesc(idCliente);
    }

    @Override public List<Reclamo> listarPorEstado(String estado) {
        return reclamoRepository.findByEstadoOrderByFechaReclamoDesc(estado);
    }

    @Override public List<Reclamo> buscarPorNumero(String numero) {
        return reclamoRepository.findByNumeroReclamoContainingIgnoreCase(numero);
    }

    @Override public void eliminarReclamo(Long id) {
        if (!reclamoRepository.existsById(id)) {
            throw new IllegalArgumentException("Reclamo no existe.");
        }
        reclamoRepository.deleteById(id);
    }
    @Override
    public Reclamo actualizarReclamo(Reclamo reclamo) {
        return reclamoRepository.save(reclamo);
    }
}