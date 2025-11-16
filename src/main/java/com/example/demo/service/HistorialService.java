package com.example.demo.service;

import com.example.demo.model.Cliente;
import com.example.demo.model.Historial;
import com.example.demo.model.Usuario;

import java.time.LocalDateTime;
import java.util.List;

public interface HistorialService {

    // Crear entrada (automático o manual)
    Historial registrar(Cliente cliente, Usuario usuario, String tipoInteraccion, String detalle);

    // Listar por cliente
    List<Historial> listarPorCliente(Long idCliente);

    // Opcionales
    List<Historial> listarPorTipo(Long idCliente, String tipo);
    List<Historial> listarPorRango(Long idCliente, LocalDateTime desde, LocalDateTime hasta);
}
