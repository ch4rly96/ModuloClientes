package com.example.demo.controller;

import com.example.demo.model.Cliente;
import com.example.demo.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/clientes")
public class ClienteWebController {

    private final ClienteService clienteService;

    public ClienteWebController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listar(Model model,
                         @RequestParam(required = false) String q,
                         @RequestParam(defaultValue = "todos") String filtro) {

        List<Cliente> clientes = clienteService.listarClientes();

        if (q != null && !q.isBlank()) {
            if ("persona".equals(filtro)) {
                clientes = clienteService.buscarPorNombre(q);
            } else if ("empresa".equals(filtro)) {
                clientes = clienteService.buscarPorRazonSocial(q);
            } else {
                clientes = new ArrayList<>();
                clientes.addAll(clienteService.buscarPorNombre(q));
                clientes.addAll(clienteService.buscarPorRazonSocial(q));
            }
        }

        model.addAttribute("clientes", clientes);
        model.addAttribute("q", q);
        model.addAttribute("filtro", filtro);
        return "clientes/list";
    }

    @GetMapping({"/nuevo", "/editar/{idCliente}"})
    public String formulario(@PathVariable(required = false) Long idCliente, Model model) {
        Cliente cliente = idCliente == null
                ? new Cliente()
                : clienteService.obtenerPorId(idCliente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("cliente", cliente);
        return "clientes/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Cliente cliente,
                          BindingResult result,
                          RedirectAttributes flash,
                          Model model) {

        // === VALIDACIÓN PERSONALIZADA PARA TIPOCLIENTE ===
        if ("empresa".equals(cliente.getTipoCliente())) {
            if (cliente.getSubtipoCliente() == null || cliente.getSubtipoCliente().isBlank()) {
                result.rejectValue("subtipoCliente", "", "Debe seleccionar el subtipo para empresa");
            }
        }

        // Si es persona, forzamos "natural" automáticamente (opcional, pero recomendado)
        if ("persona".equals(cliente.getTipoCliente())) {
            cliente.setSubtipoCliente("natural");
        }

        // === SI HAY ERRORES, VUELVE AL FORMULARIO ===
        if (result.hasErrors()) {
            model.addAttribute("cliente", cliente);
            return "clientes/form";
        }

        // === GUARDAR ===
        try {
            clienteService.guardarCliente(cliente);
            flash.addFlashAttribute("success",
                    cliente.getIdCliente() != null ? "Cliente actualizado con éxito" : "Cliente creado con éxito");
        } catch (Exception e) {
            model.addAttribute("error", "Error al guardar: " + e.getMessage());
            return "clientes/form";
        }

        return "redirect:/clientes";
    }

    @GetMapping("/estado/{idCliente}")
    public String cambiarEstado(@PathVariable Long idCliente, RedirectAttributes flash) {
        Cliente c = clienteService.obtenerPorId(idCliente).orElseThrow();
        clienteService.cambiarEstado(idCliente, !c.getEstado());
        flash.addFlashAttribute("success", "Estado cambiado");
        return "redirect:/clientes";
    }

    @GetMapping("/detalle/{idCliente}")
    public String detalle(@PathVariable Long idCliente, Model model) {
        Cliente cliente = clienteService.obtenerPorId(idCliente).orElseThrow();
        model.addAttribute("cliente", cliente);
        return "clientes/view";
    }
}