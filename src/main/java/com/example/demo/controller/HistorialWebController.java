package com.example.demo.controller;

import com.example.demo.model.Historial;
import com.example.demo.model.Cliente;
import com.example.demo.service.HistorialService;
import com.example.demo.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/historial")
public class HistorialWebController {

    @Autowired
    private HistorialService historialService;

    @Autowired
    private ClienteService clienteService;

    // Listar todos los historiales
    @GetMapping
    public String listarHistoriales(Model model) {
        List<Historial> historiales = historialService.listarHistoriales();
        model.addAttribute("historiales", historiales);
        return "historial/list"; // archivo: historiallist.html
    }

    // Mostrar formulario para nuevo historial
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        Historial historial = new Historial();
        List<Cliente> clientes = clienteService.listarClientes();

        model.addAttribute("historial", historial);
        model.addAttribute("clientes", clientes);

        return "historial/form"; // archivo: historialform.html
    }

    // Guardar historial nuevo o editado
    @PostMapping("/guardar")
    public String guardarHistorial(@ModelAttribute("historial") Historial historial) {
        historialService.guardarHistorial(historial);
        return "redirect:/historial";
    }

    // Ver detalle de un historial
    @GetMapping("/ver/{id}")
    public String verHistorial(@PathVariable Long id, Model model) {
        Optional<Historial> historialOpt = historialService.obtenerPorId(id);
        if (historialOpt.isPresent()) {
            model.addAttribute("historial", historialOpt.get());
            return "historial/view"; // archivo: historialview.html
        } else {
            return "redirect:/historial";
        }
    }

    // Editar historial
    @GetMapping("/editar/{id}")
    public String editarHistorial(@PathVariable Long id, Model model) {
        Optional<Historial> historialOpt = historialService.obtenerPorId(id);
        List<Cliente> clientes = clienteService.listarClientes();

        if (historialOpt.isPresent()) {
            model.addAttribute("historial", historialOpt.get());
            model.addAttribute("clientes", clientes);
            return "historial/form"; // reutiliza el mismo formulario
        } else {
            return "redirect:/historial";
        }
    }

    // Eliminar historial
    @GetMapping("/eliminar/{id}")
    public String eliminarHistorial(@PathVariable Long id) {
        historialService.eliminarHistorial(id);
        return "redirect:/historial";
    }
}

