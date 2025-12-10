package com.example.demo.controller;

import com.example.demo.service.ClienteService;
import com.example.demo.service.FidelizacionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @Autowired
    private ClienteService clienteService;

    @Autowired
    private FidelizacionService fidelizacionService;

    @GetMapping("/home")
    public String home(Model model) {
        long totalClientes = clienteService.listarClientes().size(); // Usamos listarClientes() para obtener el total
        long clientesActivos = clienteService.listarActivos().size(); // Usamos listarActivos() para obtener clientes activos
        long clientesMorosos = clienteService.listarMorosos().size(); // Usamos listarMorosos() para obtener clientes morosos

        // Asumimos que "Clientes en Fidelización" es algún tipo de cliente, por ejemplo "persona" y "fidelizacion"
        long clientesEnFidelizacion = fidelizacionService.listarFidelizaciones().size();

        // Obtener segmentación por tipo
        long clientesNaturales = clienteService.contarClientesPorSubtipo("persona", "natural");
        long clientesJuridicos = clienteService.contarClientesPorSubtipo("empresa", "juridico");
        long clientesConstructor = clienteService.contarClientesPorSubtipo("empresa", "constructor");
        long clientesCorporativos = clienteService.contarClientesPorSubtipo("empresa", "corporativo");

        // Calcular los porcentajes
        double porcentajeNaturales = Math.round((double) clientesNaturales / totalClientes * 100 * 100.0) / 100.0;
        double porcentajeJuridicos = Math.round((double) clientesJuridicos / totalClientes * 100 * 100.0) / 100.0;
        double porcentajeConstructor = Math.round((double) clientesConstructor / totalClientes * 100 * 100.0) / 100.0;
        double porcentajeCorporativos = Math.round((double) clientesCorporativos / totalClientes * 100 * 100.0) / 100.0;

        // Pasar los datos al modelo para Thymeleaf
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

        model.addAttribute("view", "home/dashboard");
        return "layout/main";
    }
}
