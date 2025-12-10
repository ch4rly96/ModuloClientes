package com.example.demo.service;

import com.example.demo.model.Fidelizacion;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface FidelizacionService {

    // === CRUD BÁSICO ===
    Fidelizacion crearFidelizacion(Long idCliente);
    List<Fidelizacion> listarFidelizaciones();
    Optional<Fidelizacion> obtenerPorId(Long idFidelizacion);
    Fidelizacion obtenerPorCliente(Long idCliente);
    void eliminarFidelizacion(Long idFidelizacion);
    void eliminarPorCliente(Long idCliente);

    // === GESTIÓN DE PUNTOS ===
    Fidelizacion agregarPuntos(Long idCliente, Integer puntos);
    Fidelizacion agregarPuntos(Long idCliente, Integer puntos, String concepto);
    Fidelizacion canjearPuntos(Long idCliente, Integer puntosACanjear, String concepto);
    Fidelizacion reiniciarPuntos(Long idCliente);

    // === ACTUALIZACIONES ===
    Fidelizacion actualizarNivel(Long idCliente);

    // === FILTROS Y CONSULTAS ===
    List<Fidelizacion> listarPorNivel(Fidelizacion.NivelFidelizacion nivel);
    List<Fidelizacion> listarTopClientes(Integer limite);
    List<Fidelizacion> listarClientesParaCanje(Integer puntosMinimos);
    List<Fidelizacion> buscarClientesPorNombre(String q);
    List<Fidelizacion> buscarClientesPorNombreYTipo(String q, String nivel);

    // === VERIFICACIONES ===
    boolean existeFidelizacion(Long idCliente);
    boolean puedeCanjearPuntos(Long idCliente, Integer puntos);

    // === ESTADÍSTICAS ===
    Object[] obtenerEstadisticasGenerales();
    List<Object[]> obtenerEstadisticasPorNivel();

    // === NUEVO MÉTODO PARA CONTAR ===
    Long contarTotalRegistros();

    // === OBTENER INFORMACIÓN ESPECÍFICA ===
    Integer obtenerPuntosCliente(Long idCliente);
    Fidelizacion.NivelFidelizacion obtenerNivelCliente(Long idCliente);
    Double obtenerDescuentoCliente(Long idCliente);

    void agregarPuntosPorCompra(Long idCliente, BigDecimal montoCompra);
}