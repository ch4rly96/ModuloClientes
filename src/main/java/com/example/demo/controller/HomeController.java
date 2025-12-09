package com.example.demo.controller;

import com.example.demo.service.ClienteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @Autowired
    private ClienteService clienteService;

    @GetMapping("/home")
    public String home(Model model) {
        long totalClientes = clienteService.listarClientes().size(); // Usamos listarClientes() para obtener el total
        long clientesActivos = clienteService.listarActivos().size(); // Usamos listarActivos() para obtener clientes activos
        long clientesMorosos = clienteService.listarMorosos().size(); // Usamos listarMorosos() para obtener clientes morosos

        // Asumimos que "Clientes en Fidelización" es algún tipo de cliente, por ejemplo "persona" y "fidelizacion"
        long clientesEnFidelizacion = clienteService.listarPorTipo("persona", "fidelizacion").size();

        // Obtener segmentación por tipo
        long clientesNaturales = clienteService.contarClientesPorTipo("persona");
        long clientesJuridicos = clienteService.contarClientesPorTipo("empresa");
        long clientesConstructor = clienteService.contarClientesPorSubtipo("empresa", "constructor");
        long clientesCorporativos = clienteService.contarClientesPorSubtipo("empresa", "corporativo");

        // Calcular los porcentajes
        double porcentajeNaturales = (double) clientesNaturales / totalClientes * 100;
        double porcentajeJuridicos = (double) clientesJuridicos / totalClientes * 100;
        double porcentajeConstructor = (double) clientesConstructor / totalClientes * 100;
        double porcentajeCorporativos = (double) clientesCorporativos / totalClientes * 100;

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
