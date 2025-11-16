package com.example.demo.controller;

import com.example.demo.model.Reclamo;
import com.example.demo.service.ClienteService;
import com.example.demo.service.ReclamoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/reclamos")
public class ReclamoController {

    private final ReclamoService reclamoService;
    private final ClienteService clienteService;

    public ReclamoController(ReclamoService reclamoService, ClienteService clienteService) {
        this.reclamoService = reclamoService;
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("reclamos", reclamoService.listarTodos());
        return "reclamos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("reclamo", new Reclamo());
        model.addAttribute("clientes", clienteService.listarClientes());
        return "reclamos/formulario";
    }

    @PostMapping
    public String guardar(@ModelAttribute Reclamo reclamo, @RequestParam String usuarioNombre) {
        reclamoService.crearReclamo(reclamo, usuarioNombre);
        return "redirect:/reclamos";
    }

    @GetMapping("/{id}")
    public String ver(@PathVariable Long id, Model model) {
        model.addAttribute("reclamo", reclamoService.obtenerPorId(id));
        return "reclamos/detalle";
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model) {
        model.addAttribute("reclamo", reclamoService.obtenerPorId(id));
        return "reclamos/formulario";
    }

    @PostMapping("/{id}/estado")
    public String actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado,
            @RequestParam String solucion,
            @RequestParam String usuarioNombre) {
        reclamoService.actualizarEstado(id, estado, solucion, usuarioNombre);
        return "redirect:/reclamos/" + id;
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        reclamoService.eliminarReclamo(id);
        return "redirect:/reclamos";
    }
}