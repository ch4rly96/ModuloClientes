package com.example.demo.service;
import java.math.BigDecimal;

import com.example.demo.model.Cliente;
import com.example.demo.model.Fidelizacion;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.FidelizacionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FidelizacionServicelmpl implements FidelizacionService {

    @Autowired
    private FidelizacionRepository fidelizacionRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    // === VALIDACIONES ===
    private void validarClienteExistente(Long idCliente) {
        if (!clienteRepository.existsById(idCliente)) {
            throw new IllegalArgumentException("Cliente no encontrado con ID: " + idCliente);
        }
    }

    private void validarPuntosPositivos(Integer puntos) {
        if (puntos == null || puntos <= 0) {
            throw new IllegalArgumentException("Los puntos deben ser un valor positivo.");
        }
    }

    private Fidelizacion obtenerFidelizacionPorCliente(Long idCliente) {
        return fidelizacionRepository.findByClienteIdCliente(idCliente)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró fidelización para el cliente ID: " + idCliente));
    }

    // === CRUD BÁSICO ===
    @Override
    public Fidelizacion crearFidelizacion(Long idCliente) {
        validarClienteExistente(idCliente);

        if (fidelizacionRepository.existsByClienteIdCliente(idCliente)) {
            throw new IllegalArgumentException("El cliente ya tiene un registro de fidelización");
        }

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + idCliente));

        Fidelizacion fidelizacion = new Fidelizacion(cliente);
        return fidelizacionRepository.save(fidelizacion);
    }

    @Override
    public List<Fidelizacion> listarFidelizaciones() {
        return fidelizacionRepository.findAll();
    }

    @Override
    public Optional<Fidelizacion> obtenerPorId(Long idFidelizacion) {
        return fidelizacionRepository.findById(idFidelizacion);
    }

    @Override
    public Fidelizacion obtenerPorCliente(Long idCliente) {
        return obtenerFidelizacionPorCliente(idCliente);
    }

    @Override
    public void eliminarFidelizacion(Long idFidelizacion) {
        if (!fidelizacionRepository.existsById(idFidelizacion)) {
            throw new IllegalArgumentException("No se encontró fidelización con ID: " + idFidelizacion);
        }
        fidelizacionRepository.deleteById(idFidelizacion);
    }

    @Override
    public void eliminarPorCliente(Long idCliente) {
        Fidelizacion fidelizacion = obtenerFidelizacionPorCliente(idCliente);
        fidelizacionRepository.delete(fidelizacion);
    }

    // === GESTIÓN DE PUNTOS ===
    @Override
    public Fidelizacion agregarPuntos(Long idCliente, Integer puntos) {
        return agregarPuntos(idCliente, puntos, "Puntos agregados");
    }

    @Override
    public Fidelizacion agregarPuntos(Long idCliente, Integer puntos, String concepto) {
        validarPuntosPositivos(puntos);

        Fidelizacion fidelizacion = obtenerFidelizacionPorCliente(idCliente);
        fidelizacion.agregarPuntos(puntos);

        return fidelizacionRepository.save(fidelizacion);
    }

    @Override
    public Fidelizacion canjearPuntos(Long idCliente, Integer puntosACanjear, String concepto) {
        validarPuntosPositivos(puntosACanjear);

        Fidelizacion fidelizacion = obtenerFidelizacionPorCliente(idCliente);

        if (!fidelizacion.puedeCanjear(puntosACanjear)) {
            throw new IllegalArgumentException(
                    "Puntos insuficientes. Puntos disponibles: " + fidelizacion.getPuntosAcumulados() +
                            ", puntos solicitados: " + puntosACanjear
            );
        }

        fidelizacion.canjearPuntos(puntosACanjear);
        // Aquí podrías guardar el concepto de canje en un historial
        return fidelizacionRepository.save(fidelizacion);
    }

    @Override
    public Fidelizacion reiniciarPuntos(Long idCliente) {
        Fidelizacion fidelizacion = obtenerFidelizacionPorCliente(idCliente);
        fidelizacion.setPuntosAcumulados(0);
        fidelizacion.actualizarNivel();
        return fidelizacionRepository.save(fidelizacion);
    }

    // === ACTUALIZACIONES ===
    @Override
    public Fidelizacion actualizarNivel(Long idCliente) {
        Fidelizacion fidelizacion = obtenerFidelizacionPorCliente(idCliente);
        fidelizacion.actualizarNivel();
        fidelizacion.setUltimaActualizacion(LocalDateTime.now());
        return fidelizacionRepository.save(fidelizacion);
    }

    // === FILTROS Y CONSULTAS ===
    @Override
    public List<Fidelizacion> listarPorNivel(Fidelizacion.NivelFidelizacion nivel) {
        return fidelizacionRepository.findByNivel(nivel);
    }

    @Override
    public List<Fidelizacion> listarTopClientes(Integer limite) {
        Integer limiteReal = (limite == null || limite <= 0) ? 10 : limite;
        return fidelizacionRepository.findTopClientes(limiteReal);
    }

    @Override
    public List<Fidelizacion> listarClientesParaCanje(Integer puntosMinimos) {
        Integer puntosMin = (puntosMinimos == null || puntosMinimos < 0) ? 1 : puntosMinimos;
        return fidelizacionRepository.findClientesParaCanje(puntosMin);
    }

    // === VERIFICACIONES ===
    @Override
    public boolean existeFidelizacion(Long idCliente) {
        return fidelizacionRepository.existsByClienteIdCliente(idCliente);
    }

    @Override
    public boolean puedeCanjearPuntos(Long idCliente, Integer puntos) {
        if (!existeFidelizacion(idCliente)) {
            return false;
        }
        Fidelizacion fidelizacion = obtenerFidelizacionPorCliente(idCliente);
        return fidelizacion.puedeCanjear(puntos);
    }

    // === ESTADÍSTICAS ===
    @Override
    public Object[] obtenerEstadisticasGenerales() {
        return fidelizacionRepository.obtenerEstadisticasGenerales();
    }

    @Override
    public List<Object[]> obtenerEstadisticasPorNivel() {
        return fidelizacionRepository.obtenerEstadisticasPorNivel();
    }

    // === OBTENER INFORMACIÓN ESPECÍFICA ===
    @Override
    public Integer obtenerPuntosCliente(Long idCliente) {
        Fidelizacion fidelizacion = obtenerFidelizacionPorCliente(idCliente);
        return fidelizacion.getPuntosAcumulados();
    }

    @Override
    public Fidelizacion.NivelFidelizacion obtenerNivelCliente(Long idCliente) {
        Fidelizacion fidelizacion = obtenerFidelizacionPorCliente(idCliente);
        return fidelizacion.getNivel();
    }

    @Override
    public Double obtenerDescuentoCliente(Long idCliente) {
        Fidelizacion fidelizacion = obtenerFidelizacionPorCliente(idCliente);
        return fidelizacion.calcularDescuento();
    }

    // === MÉTODOS ADICIONALES PARA INTEGRACIÓN CON CLIENTE ===
    public Fidelizacion crearFidelizacionSiNoExiste(Long idCliente) {
        if (!existeFidelizacion(idCliente)) {
            return crearFidelizacion(idCliente);
        }
        return obtenerPorCliente(idCliente);
    }

    public void agregarPuntosPorCompra(Long idCliente, BigDecimal montoCompra) {
        if (montoCompra == null || montoCompra.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto de compra debe ser positivo");
        }

        // Ejemplo: 1 punto por cada 10 soles de compra
        int puntos = montoCompra.divide(BigDecimal.TEN, 0, java.math.RoundingMode.DOWN).intValue();

        if (puntos > 0) {
            agregarPuntos(idCliente, puntos, "Puntos por compra de S/ " + montoCompra);
        }
    }
}