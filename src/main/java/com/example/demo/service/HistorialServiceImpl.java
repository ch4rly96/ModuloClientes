package com.example.demo.service;

import com.example.demo.model.Cliente;
import com.example.demo.model.Historial;
import com.example.demo.model.Usuario;
import com.example.demo.repository.HistorialRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HistorialServiceImpl implements HistorialService {

    @Autowired
    private HistorialRepository historialRepository;

    @Override
    public Historial registrar(Cliente cliente, Usuario usuario, String tipoInteraccion, String detalle) {
        validarEntrada(cliente, usuario, tipoInteraccion, detalle);

        Historial entrada = new Historial();
        entrada.setCliente(cliente);
        entrada.setCreadoPor(usuario);
        entrada.setTipoInteraccion(tipoInteraccion);
        entrada.setDetalle(detalle);

        return historialRepository.save(entrada);
    }

    private void validarEntrada(Cliente cliente, Usuario usuario, String tipo, String detalle) {
        if (cliente == null || cliente.getIdCliente() == null) {
            throw new IllegalArgumentException("Cliente inválido.");
        }
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario requerido para auditoría.");
        }
        if (StringUtils.isBlank(tipo)) {
            throw new IllegalArgumentException("Tipo de interacción requerido.");
        }
        if (StringUtils.isBlank(detalle)) {
            throw new IllegalArgumentException("Detalle requerido.");
        }
    }

    @Override
    public List<Historial> listarPorCliente(Long idCliente) {
        return historialRepository.findByClienteIdClienteOrderByCreadoEnDesc(idCliente);
    }

    @Override
    public List<Historial> listarPorTipo(Long idCliente, String tipo) {
        return historialRepository.findByClienteIdClienteAndTipoInteraccionOrderByCreadoEnDesc(idCliente, tipo);
    }

    @Override
    public List<Historial> listarPorRango(Long idCliente, LocalDateTime desde, LocalDateTime hasta) {
        return historialRepository.findByClienteIdClienteAndCreadoEnBetweenOrderByCreadoEnDesc(idCliente, desde, hasta);
    }
}
