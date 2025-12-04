package com.example.demo.controller;

import com.example.demo.model.Cliente;
import com.example.demo.model.Reclamo;
import com.example.demo.service.ClienteService;
import com.example.demo.service.ReclamoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/reclamos")
public class ReclamoWebController {

    private final ReclamoService reclamoService;
    private final ClienteService clienteService;

    public ReclamoWebController(ReclamoService reclamoService, ClienteService clienteService) {
        this.reclamoService = reclamoService;
        this.clienteService = clienteService;
    }

    // LISTA
    @GetMapping
    public String listar(@RequestParam(value = "q", required = false) String q,
                         @RequestParam(value = "estado", required = false, defaultValue = "todos") String estado,
                         Model model) {

        List<Reclamo> reclamos = reclamoService.buscar(q, estado);

        model.addAttribute("reclamos", reclamos);
        model.addAttribute("q", q);
        model.addAttribute("estado", estado);

        return "reclamos/list";
    }

    // NUEVO FORM
    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        Reclamo r = new Reclamo();
        r.setCliente(new Cliente());
        r.setNumeroReclamo(reclamoService.generarNumeroReclamo());
        model.addAttribute("reclamo", r);
        model.addAttribute("clientes", clienteService.listarClientes());
        return "reclamos/form";    // form.html
    }

    // GUARDAR
    @PostMapping
    public String guardar(@ModelAttribute Reclamo reclamo, @RequestParam String usuarioNombre) {
        reclamoService.crearReclamo(reclamo, usuarioNombre);
        return "redirect:/reclamos";
    }

    // ACTUALIZAR RECLAMO
    @PostMapping("/{id}")
    public String actualizarReclamo(
            @PathVariable Long id,
            @ModelAttribute Reclamo reclamo,
            @RequestParam String usuarioNombre) {
        reclamo.setNumeroReclamo(reclamoService.obtenerPorId(id).getNumeroReclamo());
        reclamoService.actualizarReclamo(reclamo);
        return "redirect:/reclamos/" + id;
    }


    // DETALLE
    @GetMapping("/{id}")
    public String ver(@PathVariable Long id, Model model) {
        model.addAttribute("reclamo", reclamoService.obtenerPorId(id));
        return "reclamos/view";    // view.html
    }

    // EDITAR FORM
    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model) {
        model.addAttribute("reclamo", reclamoService.obtenerPorId(id));
        model.addAttribute("clientes", clienteService.listarClientes());
        return "reclamos/form";    // form.html
    }

    // ACTUALIZAR ESTADO
    @PostMapping("/{id}/estado")
    public String actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado,
            @RequestParam String solucion,
            @RequestParam String usuarioNombre) {
        reclamoService.actualizarEstado(id, estado, solucion, usuarioNombre);
        return "redirect:/reclamos";
    }

    // ELIMINAR
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        reclamoService.eliminarReclamo(id);
        return "redirect:/reclamos";
    }
}