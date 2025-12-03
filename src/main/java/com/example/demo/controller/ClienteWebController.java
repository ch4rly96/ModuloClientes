package com.example.demo.controller;

import com.example.demo.model.Cliente;
import com.example.demo.model.Direcciones;
import com.example.demo.service.ClienteService;
import com.example.demo.service.DireccionesService;
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
    private final DireccionesService direccionesService;

    public ClienteWebController(ClienteService clienteService, DireccionesService direccionesService) {
        this.clienteService = clienteService;
        this.direccionesService = direccionesService;
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

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/form";
    }

    @GetMapping("/editar/{idCliente}")
    public String editar(@PathVariable Long idCliente, Model model) {
        Cliente cliente = clienteService.obtenerPorId(idCliente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("cliente", cliente);
        return "clientes/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Cliente cliente,
                          BindingResult result,
                          RedirectAttributes flash,
                          Model model) {
        System.out.println("=== DEBUG GUARDAR ===");
        System.out.println("ID: " + cliente.getIdCliente());
        System.out.println("Tipo: " + cliente.getTipoCliente());
        System.out.println("Subtipo: " + cliente.getSubtipoCliente());
        System.out.println("Razón Social: " + cliente.getRazonSocial());
        System.out.println("Tiene errores: " + result.hasErrors());

        // === VALIDACIÓN PERSONALIZADA PARA TIPOCLIENTE ===
        if ("empresa".equals(cliente.getTipoCliente())) {
            if (cliente.getSubtipoCliente() == null || cliente.getSubtipoCliente().isBlank()) {
                result.rejectValue("subtipoCliente", "", "Debe seleccionar el subtipo para empresa");
            }
        } else {
            cliente.setSubtipoCliente("natural");
        }

        // === SI HAY ERRORES → VUELVE AL FORM ===
        if (result.hasErrors()) {
            model.addAttribute("cliente", cliente);
            System.out.println("HAY ERRORES → VOLVIENDO AL FORM");
            return "clientes/form";  // Aquí se queda con el cliente y los errores
        }

        // === GUARDAR ===
        try {
            Cliente guardado = clienteService.guardarCliente(cliente);
            System.out.println("GUARDADO CON ID: " + guardado.getIdCliente());
            flash.addFlashAttribute("success", "Cliente actualizado con éxito");
        } catch (Exception e) {
            model.addAttribute("error", "Error al guardar: " + e.getMessage());
            model.addAttribute("cliente", cliente);  // también aquí por si acaso
            return "clientes/form";
        }

        return "redirect:/clientes";
    }

    @PostMapping("/actualizar/{idCliente}")
    public String actualizar(
            @PathVariable Long idCliente,
            @Valid @ModelAttribute("cliente") Cliente cliente,
            BindingResult result,
            RedirectAttributes flash,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("cliente", cliente);
            return "clientes/form";
        }

        Cliente existente = clienteService.obtenerPorId(idCliente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // copiar campos
        existente.setNombres(cliente.getNombres());
        existente.setApellidos(cliente.getApellidos());
        existente.setDireccionPrincipal(cliente.getDireccionPrincipal());
        existente.setDocumentoIdentidad(cliente.getDocumentoIdentidad());
        existente.setEmail(cliente.getEmail());
        existente.setTelefono(cliente.getTelefono());
        existente.setTipoCliente(cliente.getTipoCliente());
        existente.setSubtipoCliente(cliente.getSubtipoCliente());
        existente.setRazonSocial(cliente.getRazonSocial());
        existente.setEstado(cliente.getEstado());

        clienteService.guardarCliente(existente);
        flash.addFlashAttribute("success", "Cliente actualizado correctamente");
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
        //model.addAttribute("direcciones", direccionesService.listarPorCliente(idCliente));  // Agregar direcciones
        //model.addAttribute("tienePrincipal", direccionesService.obtenerPrincipal(idCliente).isPresent()); // Verifica si tiene dirección principal
        //model.addAttribute("nuevaDireccion", new Direcciones()); // Para el formulario de nueva dirección
        return "clientes/view";
    }


    @GetMapping("/eliminar/{idCliente}")
    public String eliminar(@PathVariable Long idCliente, RedirectAttributes flash) {

        clienteService.eliminar(idCliente);
        flash.addFlashAttribute("success", "Cliente eliminado correctamente");

        return "redirect:/clientes";
    }
}