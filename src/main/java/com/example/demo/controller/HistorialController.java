package com.example.demo.controller;

import com.example.demo.model.Cliente;
import com.example.demo.service.ClienteService;
import com.example.demo.service.HistorialService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/clientes")
public class HistorialController {

    private final ClienteService clienteService;
    private final HistorialService historialService;

    public HistorialController(ClienteService clienteService, HistorialService historialService) {
        this.clienteService = clienteService;
        this.historialService = historialService;
    }

    // ========================================
    // MOSTRAR HISTORIAL EN FICHA DE CLIENTE
    // ========================================
    @GetMapping("/{id}/historial")
    public String verHistorial(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        model.addAttribute("cliente", cliente);
        model.addAttribute("historiales", historialService.listarPorCliente(id));

        return "clientes/historial"; // → historial.html (pestaña o modal)
    }
}
