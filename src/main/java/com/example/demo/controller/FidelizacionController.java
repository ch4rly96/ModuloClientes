package com.example.demo.controller;

import com.example.demo.model.Fidelizacion;
import com.example.demo.repository.FidelizacionRepository;
import com.example.demo.service.ClienteService;
import com.example.demo.service.FidelizacionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/fidelizacion")
public class FidelizacionController {

    private final FidelizacionService fidelizacionService;
    private final ClienteService clienteService;
    private FidelizacionRepository fidelizacionRepository;

    public FidelizacionController(FidelizacionService fidelizacionService, ClienteService clienteService) {
        this.fidelizacionService = fidelizacionService;
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listarClientesFidelizados(@RequestParam(value = "q", required = false) String q,
                                            @RequestParam(value = "filtro", required = false, defaultValue = "todos") String filtro,
                                            Model model) {

        List<Fidelizacion> fidelizaciones;

        // Si hay búsqueda por nombre
        if (q != null && !q.trim().isEmpty()) {
            // Si el filtro no es "todos", usar búsqueda combinada
            if (!"todos".equals(filtro)) {
                fidelizaciones = fidelizacionService.buscarClientesPorNombreYTipo(q, filtro);
            } else {
                fidelizaciones = fidelizacionService.buscarClientesPorNombre(q);
            }
        }
        // Si no hay búsqueda pero hay filtro
        else if (!"todos".equals(filtro)) {
            fidelizaciones = fidelizacionService.buscarClientesPorNombreYTipo(null, filtro);
        }
        // Si no hay ni búsqueda ni filtro, listar todos
        else {
            fidelizaciones = fidelizacionService.listarFidelizaciones();
        }

        model.addAttribute("fidelizaciones", fidelizaciones);
        model.addAttribute("q", q);
        model.addAttribute("filtro", filtro);

        return "fidelizacion/lista";
    }

    @GetMapping("/nivel/{nivel}")
    public String obtenerPorNivel(@PathVariable String nivel, Model model) {
        try {
            Fidelizacion.NivelFidelizacion nivelEnum = Fidelizacion.NivelFidelizacion.valueOf(nivel.toUpperCase());
            model.addAttribute("fidelizaciones", fidelizacionService.listarPorNivel(nivelEnum));
            return "fidelizacion/lista";
        } catch (IllegalArgumentException e) {
            return "error";
        }
    }

    @GetMapping("/top-clientes")
    public String obtenerTopClientes(@RequestParam(defaultValue = "10") Integer limite, Model model) {
        model.addAttribute("fidelizaciones", fidelizacionService.listarTopClientes(limite));
        return "fidelizacion/topClientes";
    }

    @GetMapping("/clientes-canje")
    public String obtenerClientesParaCanje(@RequestParam(defaultValue = "1") Integer puntosMinimos, Model model) {
        model.addAttribute("fidelizaciones", fidelizacionService.listarClientesParaCanje(puntosMinimos));
        return "fidelizacion/clienteCanje";
    }

    // DETALLE
    @GetMapping("/detalle/{idCliente}")
    public String obtenerPorCliente(@PathVariable Long idCliente, Model model) {
        try {
            Fidelizacion fidelizacion = fidelizacionService.obtenerPorCliente(idCliente);
            model.addAttribute("fidelizacion", fidelizacion);
            return "fidelizacion/detalle";
        } catch (IllegalArgumentException e) {
            return "error";
        }
    }

    // CREAR
    @PostMapping("/cliente/{idCliente}")
    public String crearFidelizacion(@PathVariable Long idCliente, Model model) {
        try {
            Fidelizacion creado = fidelizacionService.crearFidelizacion(idCliente);
            model.addAttribute("fidelizacion", creado);
            return "fidelizacion/detalle";
        } catch (IllegalArgumentException e) {
            return "error";
        }
    }

    // GESTIÓN DE PUNTOS
    @GetMapping("/cliente/{idCliente}/agregar-puntos")
    public String agregarPuntos(@PathVariable Long idCliente, Model model) {
        model.addAttribute("idCliente", idCliente);
        return "fidelizacion/agregarPuntos";
    }

    @PostMapping("/cliente/{idCliente}/agregar-puntos")
    public String agregarPuntos(@PathVariable Long idCliente,
                                @RequestParam Integer puntos,
                                @RequestParam(required = false) String concepto,
                                Model model) {
        try {
            Fidelizacion actualizado;
            if (concepto != null && !concepto.trim().isEmpty()) {
                actualizado = fidelizacionService.agregarPuntos(idCliente, puntos, concepto);
            } else {
                actualizado = fidelizacionService.agregarPuntos(idCliente, puntos);
            }
            model.addAttribute("fidelizacion", actualizado);
            model.addAttribute("success", "Puntos agregados correctamente");
            return "redirect:/fidelizacion/detalle/" + idCliente;
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/cliente/{idCliente}/canjear-puntos")
    public String canjearPuntos(@PathVariable Long idCliente, Model model) {
        model.addAttribute("idCliente", idCliente);
        return "fidelizacion/canjearPuntos";
    }

    @PostMapping("/cliente/{idCliente}/canjear-puntos")
    public String canjearPuntos(@PathVariable Long idCliente,
                                @RequestParam Integer puntos,
                                @RequestParam(required = false) String concepto,
                                Model model) {
        try {
            Fidelizacion actualizado = fidelizacionService.canjearPuntos(idCliente, puntos, concepto);
            model.addAttribute("fidelizacion", actualizado);
            model.addAttribute("success", "Puntos canjeados correctamente");
            return "redirect:/fidelizacion/detalle/" + idCliente;
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    // ACTUALIZACIONES
    @PutMapping("/cliente/{idCliente}/actualizar-nivel")
    public String actualizarNivel(@PathVariable Long idCliente, Model model) {
        try {
            Fidelizacion actualizado = fidelizacionService.actualizarNivel(idCliente);
            model.addAttribute("fidelizacion", actualizado);
            return "fidelizacion/detalle";
        } catch (IllegalArgumentException e) {
            return "error";
        }
    }

    // ELIMINAR
    @DeleteMapping("/cliente/{idCliente}")
    public String eliminarPorCliente(@PathVariable Long idCliente, Model model) {
        try {
            fidelizacionService.eliminarPorCliente(idCliente);
            return "redirect:/fidelizacion";
        } catch (IllegalArgumentException e) {
            return "error";
        }
    }

    @GetMapping("/estadisticas")
    public String obtenerEstadisticas(Model model) {
        try {
            System.out.println("=== INICIANDO OBTENCIÓN DE ESTADÍSTICAS ===");

            // 1. Obtener estadísticas generales
            Object[] statsGenerales = fidelizacionService.obtenerEstadisticasGenerales();

            // 2. Obtener estadísticas por nivel
            List<Object[]> statsPorNivel = fidelizacionService.obtenerEstadisticasPorNivel();

            // 3. Procesar estadísticas generales
            long totalClientes = 0;
            long totalPuntos = 0;
            double promedioPuntos = 0.0;

            if (statsGenerales != null && statsGenerales.length >= 3) {
                try {
                    // Los valores vienen como Object, necesitamos convertirlos
                    totalClientes = ((Number) statsGenerales[0]).longValue();
                    totalPuntos = ((Number) statsGenerales[1]).longValue();
                    promedioPuntos = ((Number) statsGenerales[2]).doubleValue();

                    System.out.println("Estadísticas procesadas -> Clientes: " + totalClientes +
                            ", Puntos: " + totalPuntos +
                            ", Promedio: " + promedioPuntos);
                } catch (Exception e) {
                    System.out.println("Error al convertir estadísticas generales: " + e.getMessage());
                }
            } else {
                System.out.println("Estadísticas generales nulas o incompletas");
            }

            model.addAttribute("totalClientes", totalClientes);
            model.addAttribute("totalPuntos", totalPuntos);
            model.addAttribute("promedioPuntos", String.format("%.2f", promedioPuntos));

            // 4. Procesar estadísticas por nivel
            if (statsPorNivel != null && !statsPorNivel.isEmpty()) {
                List<Map<String, Object>> nivelesProcesados = new ArrayList<>();

                System.out.println("Procesando " + statsPorNivel.size() + " niveles...");

                for (Object[] nivelData : statsPorNivel) {
                    try {
                        if (nivelData.length >= 3) {
                            Map<String, Object> nivelMap = new HashMap<>();

                            // Nivel
                            Object nivelObj = nivelData[0];
                            String nivelStr;
                            if (nivelObj instanceof Fidelizacion.NivelFidelizacion) {
                                nivelStr = ((Fidelizacion.NivelFidelizacion) nivelObj).toString();
                            } else {
                                nivelStr = nivelObj != null ? nivelObj.toString() : "DESCONOCIDO";
                            }
                            nivelMap.put("nivel", nivelStr);

                            // Cantidad de clientes
                            Long cantidad = 0L;
                            if (nivelData[1] != null) {
                                cantidad = ((Number) nivelData[1]).longValue();
                            }
                            nivelMap.put("cantidadClientes", cantidad);

                            // Promedio de puntos
                            Double promedio = 0.0;
                            if (nivelData[2] != null) {
                                promedio = ((Number) nivelData[2]).doubleValue();
                            }
                            nivelMap.put("promedioPuntos", String.format("%.2f", promedio));
                            nivelMap.put("promedioPuntosNum", promedio);

                            // Calcular porcentaje
                            double porcentaje = 0.0;
                            if (totalClientes > 0 && cantidad > 0) {
                                porcentaje = (cantidad.doubleValue() / totalClientes) * 100;
                            }
                            nivelMap.put("porcentaje", String.format("%.1f", porcentaje));
                            nivelMap.put("porcentajeNumero", porcentaje);

                            nivelesProcesados.add(nivelMap);

                            System.out.println("Nivel " + nivelStr + ": " + cantidad +
                                    " clientes, " + promedio + " promedio, " +
                                    porcentaje + "%");
                        }
                    } catch (Exception e) {
                        System.out.println("Error procesando nivel: " + Arrays.toString(nivelData));
                        e.printStackTrace();
                    }
                }

                model.addAttribute("estadisticasPorNivel", nivelesProcesados);
            } else {
                System.out.println("No hay estadísticas por nivel");
                model.addAttribute("estadisticasPorNivel", new ArrayList<>());
            }

            // 5. Contar registros para debug
            Long totalRegistros = fidelizacionService.contarTotalRegistros();
            model.addAttribute("debugTotalRegistros", totalRegistros);

            System.out.println("=== FINALIZADO: Total clientes = " + totalClientes + " ===");

            return "fidelizacion/estadisticas";

        } catch (Exception e) {
            System.out.println("ERROR CRÍTICO en estadísticas: " + e.getMessage());
            e.printStackTrace();

            model.addAttribute("totalClientes", 0);
            model.addAttribute("totalPuntos", 0);
            model.addAttribute("promedioPuntos", "0.00");
            model.addAttribute("estadisticasPorNivel", new ArrayList<>());
            model.addAttribute("error", "Error: " + e.getMessage());

            return "fidelizacion/estadisticas";
        }

    }
}