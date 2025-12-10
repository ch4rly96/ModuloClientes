package com.example.demo.service;

import com.example.demo.model.Cliente;
import com.example.demo.model.Fidelizacion;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.FidelizacionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class FidelizacionServicelmpl implements FidelizacionService {

    @Override
    public Long contarTotalRegistros() {
        return fidelizacionRepository.count();
    }

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

    @Override
    public List<Fidelizacion> buscarClientesPorNombre(String q) {
        if (q != null && !q.trim().isEmpty()) {
            // Usar la nueva búsqueda flexible que busca en cualquier parte del nombre
            return fidelizacionRepository.buscarPorCualquierParteDelNombre(q.trim());
        }
        return fidelizacionRepository.findAll();
    }

    @Override
    public List<Fidelizacion> buscarClientesPorNombreYTipo(String q, String nivel) {
        // Si ambos parámetros están vacíos, devolver todos
        if ((q == null || q.trim().isEmpty()) && (nivel == null || nivel.trim().isEmpty())) {
            return fidelizacionRepository.findAll();
        }

        // Si solo hay término de búsqueda, usar búsqueda flexible
        if (q != null && !q.trim().isEmpty() && (nivel == null || nivel.trim().isEmpty())) {
            return fidelizacionRepository.buscarPorCualquierParteDelNombre(q.trim());
        }

        // Usar la búsqueda combinada
        return fidelizacionRepository.buscarClientes(q, nivel);
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
        try {
            System.out.println("=== CALCULANDO ESTADÍSTICAS GENERALES ===");

            // Usar findAll() y calcular manualmente
            List<Fidelizacion> todas = fidelizacionRepository.findAll();
            System.out.println("Total registros encontrados: " + todas.size());

            if (todas.isEmpty()) {
                System.out.println("No hay registros, devolviendo ceros");
                return new Object[]{0L, 0L, 0.0};
            }

            // Calcular totales
            long totalClientes = todas.size();
            long totalPuntos = 0;

            for (Fidelizacion f : todas) {
                totalPuntos += f.getPuntosAcumulados();
                System.out.println("Cliente ID: " + f.getCliente().getIdCliente() +
                        ", Puntos: " + f.getPuntosAcumulados() +
                        ", Nivel: " + f.getNivel());
            }

            double promedio = totalClientes > 0 ? (double) totalPuntos / totalClientes : 0.0;

            System.out.println("Resultados -> Clientes: " + totalClientes +
                    ", Puntos: " + totalPuntos +
                    ", Promedio: " + promedio);

            return new Object[]{totalClientes, totalPuntos, promedio};

        } catch (Exception e) {
            System.out.println("ERROR en obtenerEstadisticasGenerales: " + e.getMessage());
            e.printStackTrace();
            return new Object[]{0L, 0L, 0.0};
        }
    }

    @Override
    public List<Object[]> obtenerEstadisticasPorNivel() {
        try {
            System.out.println("=== CALCULANDO ESTADÍSTICAS POR NIVEL ===");

            List<Fidelizacion> todas = fidelizacionRepository.findAll();
            System.out.println("Total registros para agrupar: " + todas.size());

            if (todas.isEmpty()) {
                System.out.println("No hay registros para agrupar");
                return new ArrayList<>();
            }

            // Agrupar por nivel usando Map
            Map<Fidelizacion.NivelFidelizacion, List<Fidelizacion>> porNivel = new HashMap<>();

            for (Fidelizacion f : todas) {
                Fidelizacion.NivelFidelizacion nivel = f.getNivel();
                if (!porNivel.containsKey(nivel)) {
                    porNivel.put(nivel, new ArrayList<>());
                }
                porNivel.get(nivel).add(f);
            }

            System.out.println("Niveles encontrados: " + porNivel.size());

            List<Object[]> resultados = new ArrayList<>();

            // Calcular para cada nivel
            for (Map.Entry<Fidelizacion.NivelFidelizacion, List<Fidelizacion>> entry : porNivel.entrySet()) {
                Fidelizacion.NivelFidelizacion nivel = entry.getKey();
                List<Fidelizacion> clientesNivel = entry.getValue();

                long cantidad = clientesNivel.size();
                long totalPuntosNivel = 0;

                for (Fidelizacion f : clientesNivel) {
                    totalPuntosNivel += f.getPuntosAcumulados();
                }

                double promedioNivel = cantidad > 0 ? (double) totalPuntosNivel / cantidad : 0.0;

                System.out.println("Nivel " + nivel + ": " + cantidad + " clientes, " +
                        totalPuntosNivel + " puntos, promedio " + promedioNivel);

                resultados.add(new Object[]{nivel, cantidad, promedioNivel});
            }

            // Ordenar resultados: Platinum, Gold, Silver, Bronze
            resultados.sort((a, b) -> {
                String nivelA = ((Fidelizacion.NivelFidelizacion) a[0]).toString();
                String nivelB = ((Fidelizacion.NivelFidelizacion) b[0]).toString();

                Map<String, Integer> orden = new HashMap<>();
                orden.put("PLATINUM", 1);
                orden.put("GOLD", 2);
                orden.put("SILVER", 3);
                orden.put("BRONZE", 4);

                return orden.getOrDefault(nivelA, 5) - orden.getOrDefault(nivelB, 5);
            });

            return resultados;

        } catch (Exception e) {
            System.out.println("ERROR en obtenerEstadisticasPorNivel: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
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

    @Override
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