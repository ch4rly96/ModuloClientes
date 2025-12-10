package com.example.demo.controller;

import com.example.demo.model.Fidelizacion;
import com.example.demo.service.ClienteService;
import com.example.demo.service.FidelizacionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final ClienteService clienteService;
    private final FidelizacionService fidelizacionService;

    public HomeController(ClienteService clienteService, FidelizacionService fidelizacionService) {
        this.clienteService = clienteService;
        this.fidelizacionService = fidelizacionService;
    }

    @GetMapping("/home")
    public String mostrarDashboard(Model model) {
        try {
            System.out.println("=== INICIANDO DASHBOARD ===");

            // 1. Obtener estadísticas generales
            long totalClientes = clienteService.listarClientes().size();
            long clientesActivos = clienteService.listarActivos().size();
            long clientesMorosos = clienteService.listarMorosos().size();

            // 2. Obtener TOP 3 clientes con más puntos
            List<Fidelizacion> topClientes = fidelizacionService.listarTopClientes(3);

            // 3. Calcular clientes en fidelización (todos, no solo top 3)
            long clientesEnFidelizacion = fidelizacionService.listarFidelizaciones().size();

            // 4. Segmentación por tipo
            long clientesNaturales = clienteService.contarClientesPorSubtipo("persona", "natural");
            long clientesJuridicos = clienteService.contarClientesPorSubtipo("empresa", "juridico");
            long clientesConstructor = clienteService.contarClientesPorSubtipo("empresa", "constructor");
            long clientesCorporativos = clienteService.contarClientesPorSubtipo("empresa", "corporativo");

            // 5. Calcular porcentajes
            double porcentajeNaturales = totalClientes > 0 ?
                    Math.round((double) clientesNaturales / totalClientes * 100 * 100.0) / 100.0 : 0.0;
            double porcentajeJuridicos = totalClientes > 0 ?
                    Math.round((double) clientesJuridicos / totalClientes * 100 * 100.0) / 100.0 : 0.0;
            double porcentajeConstructor = totalClientes > 0 ?
                    Math.round((double) clientesConstructor / totalClientes * 100 * 100.0) / 100.0 : 0.0;
            double porcentajeCorporativos = totalClientes > 0 ?
                    Math.round((double) clientesCorporativos / totalClientes * 100 * 100.0) / 100.0 : 0.0;

            // 6. DEBUG: Ver qué datos estamos obteniendo
            System.out.println("Total clientes: " + totalClientes);
            System.out.println("Clientes en fidelización (todos): " + clientesEnFidelizacion);
            System.out.println("Top clientes solicitados (3), obtenidos: " + (topClientes != null ? topClientes.size() : 0));

            if (topClientes != null && !topClientes.isEmpty()) {
                System.out.println("=== DETALLE DE TOP CLIENTES ===");
                for (int i = 0; i < topClientes.size(); i++) {
                    Fidelizacion f = topClientes.get(i);
                    System.out.println("Posición " + (i+1) + ": " +
                            (f.getCliente().getRazonSocial() != null ?
                                    f.getCliente().getRazonSocial() : f.getCliente().getNombreCompleto()) +
                            " - Puntos: " + f.getPuntosAcumulados() +
                            " - Nivel: " + (f.getNivel() != null ? f.getNivel().toString() : "NULL"));
                }
            } else {
                System.out.println("No se obtuvieron top clientes");
            }

            // 7. Pasar datos al modelo
            model.addAttribute("totalClientes", totalClientes);
            model.addAttribute("clientesActivos", clientesActivos);
            model.addAttribute("clientesEnFidelizacion", clientesEnFidelizacion);
            model.addAttribute("clientesMorosos", clientesMorosos);
            model.addAttribute("clientesNaturales", clientesNaturales);
            model.addAttribute("clientesJuridicos", clientesJuridicos);
            model.addAttribute("clientesConstructor", clientesConstructor);
            model.addAttribute("clientesCorporativos", clientesCorporativos);
            model.addAttribute("porcentajeNaturales", porcentajeNaturales);
            model.addAttribute("porcentajeJuridicos", porcentajeJuridicos);
            model.addAttribute("porcentajeConstructor", porcentajeConstructor);
            model.addAttribute("porcentajeCorporativos", porcentajeCorporativos);
            model.addAttribute("topClientes", topClientes != null ? topClientes : List.of());

            model.addAttribute("view", "home/dashboard");
            return "layout/main";

        } catch (Exception e) {
            System.out.println("ERROR en dashboard: " + e.getMessage());
            e.printStackTrace();

            model.addAttribute("totalClientes", 0);
            model.addAttribute("clientesActivos", 0);
            model.addAttribute("clientesEnFidelizacion", 0);
            model.addAttribute("clientesMorosos", 0);
            model.addAttribute("porcentajeNaturales", 0);
            model.addAttribute("porcentajeJuridicos", 0);
            model.addAttribute("porcentajeConstructor", 0);
            model.addAttribute("porcentajeCorporativos", 0);
            model.addAttribute("topClientes", List.of());

            model.addAttribute("view", "home/dashboard");
            return "layout/main";
        }
    }
}