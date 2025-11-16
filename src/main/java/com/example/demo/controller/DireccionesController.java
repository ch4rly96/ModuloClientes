package com.example.demo.controller;

import com.example.demo.model.Direcciones;
import com.example.demo.model.Cliente;
import com.example.demo.service.ClienteService;
import com.example.demo.service.DireccionesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clientes")
public class DireccionesController {

    private final ClienteService clienteService;
    private final DireccionesService direccionesService;

    public DireccionesController(ClienteService clienteService, DireccionesService direccionesService) {
        this.clienteService = clienteService;
        this.direccionesService = direccionesService;
    }

    @GetMapping("/{id}")
    public String verCliente(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        model.addAttribute("cliente", cliente);
        model.addAttribute("direcciones", direccionesService.listarPorCliente(id));
        model.addAttribute("tienePrincipal",
                direccionesService.obtenerPrincipal(id).isPresent());

        // Para el modal de nueva dirección
        model.addAttribute("nuevaDireccion", new Direcciones());

        return "clientes/ficha"; // → ficha.html
    }

    @PostMapping("/{idCliente}/direcciones")
    public String guardarDireccion(
            @PathVariable Long idCliente,
            @ModelAttribute("nuevaDireccion") Direcciones direccion,
            @RequestParam(required = false) Long idDireccion) {

        Cliente cliente = clienteService.obtenerPorId(idCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        direccion.setCliente(cliente);
        if (idDireccion != null) {
            direccion.setIdDireccion(idDireccion);
        }

        direccionesService.guardarDireccion(direccion);

        return "redirect:/clientes/" + idCliente;
    }

    @PostMapping("/{idCliente}/direcciones/{idDireccion}/eliminar")
    public String eliminarDireccion(
            @PathVariable Long idCliente,
            @PathVariable Long idDireccion) {

        direccionesService.eliminarDireccion(idDireccion, idCliente);

        return "redirect:/clientes/" + idCliente;
    }
}
