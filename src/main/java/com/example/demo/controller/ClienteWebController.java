package com.example.demo.controller;

import com.example.demo.model.Cliente;
import com.example.demo.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cliente")
public class ClienteWebController {

    @Autowired
    private ClienteService clienteService;

    // Listar clientes
    @GetMapping
    public String listarClientes(Model model) {
        model.addAttribute("clientes", clienteService.listarClientes());
        return "cliente/list"; // Archivo list.html
    }

    // Mostrar formulario de registro
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "cliente/form"; // Archivo form.html
    }

    // Guardar cliente (registrar) con validaciones
    @PostMapping("/guardar")
    public String guardarCliente(@Valid @ModelAttribute("cliente") Cliente cliente,
                                 BindingResult result,
                                 Model model, RedirectAttributes redirectAttributes) {
        try {
            clienteService.guardarCliente(cliente);
            // Enviar mensaje de éxito
            redirectAttributes.addFlashAttribute("mensajeExito", "Cliente registrado correctamente");
            return "redirect:/cliente/nuevo"; // Volver al mismo formulario
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al registrar el cliente: " + e.getMessage());
            return "redirect:/cliente/nuevo";
        }
    }

    // Ver detalles del cliente
    @GetMapping("/ver/{id}")
    public String verCliente(@PathVariable Long id, Model model) {
        clienteService.obtenerPorId(id).ifPresent(c -> model.addAttribute("cliente", c));
        return "cliente/view"; // Archivo view.html
    }

    // Eliminar cliente
    @GetMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable Long id) {
        clienteService.eliminarCliente(id);
        return "redirect:/cliente";
    }
}

