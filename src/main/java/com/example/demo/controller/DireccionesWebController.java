package com.example.demo.controller;

import com.example.demo.dto.DireccionesForm;
import com.example.demo.model.Cliente;
import com.example.demo.model.Direcciones;
import com.example.demo.service.ClienteService;
import com.example.demo.service.DireccionesService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/direcciones")
public class DireccionesWebController {

    private final DireccionesService direccionesService;
    private final ClienteService clienteService;

    public DireccionesWebController(DireccionesService direccionesService, ClienteService clienteService) {
        this.direccionesService = direccionesService;
        this.clienteService = clienteService;
    }

    // Listado
    @GetMapping
    public String listarDirecciones(Model model) {
        List<Direcciones> lista = direccionesService.listarDirecciones();
        model.addAttribute("direcciones", lista);
        return "direcciones/list";
    }

    // Formulario nuevo
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("direccionForm", new DireccionesForm());
        model.addAttribute("clientes", clienteService.listarClientes());
        return "direcciones/form";
    }

    // Formulario edición
    @GetMapping("/editar/{id}")
    public String editarDireccion(@PathVariable("id") Long id, Model model) {
        Direcciones d = direccionesService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Dirección no encontrada: " + id));

        DireccionesForm form = new DireccionesForm();
        form.setIdDireccion(d.getIdDireccion());
        form.setClienteId(d.getCliente().getIdCliente());
        form.setTipoDireccion(d.getTipoDireccion());
        form.setDireccion(d.getDireccion());
        form.setCiudad(d.getCiudad());
        form.setDepartamento(d.getDepartamento());
        form.setPais(d.getPais());

        model.addAttribute("direccionForm", form);
        model.addAttribute("clientes", clienteService.listarClientes());
        return "direcciones/form";
    }

    // Guardar dirección (crear o actualizar)
    @PostMapping("/guardar")
    public String guardarDireccion(@Valid @ModelAttribute("direccionForm") DireccionesForm form,
                                   BindingResult result,
                                   Model model) {
        if (result.hasErrors()) {
            model.addAttribute("clientes", clienteService.listarClientes());
            return "direcciones/form";
        }

        Cliente cliente = clienteService.obtenerPorId(form.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        Direcciones direccion = new Direcciones();
        direccion.setIdDireccion(form.getIdDireccion());
        direccion.setCliente(cliente);
        direccion.setTipoDireccion(form.getTipoDireccion());
        direccion.setDireccion(form.getDireccion());
        direccion.setCiudad(form.getCiudad());
        direccion.setDepartamento(form.getDepartamento());
        direccion.setPais(form.getPais());

        direccionesService.guardarDireccion(direccion);
        return "redirect:/direcciones";
    }

    // Ver detalle
    @GetMapping("/ver/{id}")
    public String verDireccion(@PathVariable("id") Long id, Model model) {
        Direcciones d = direccionesService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Dirección no encontrada: " + id));
        model.addAttribute("direccion", d);
        return "direcciones/view";
    }

    // Eliminar
    @GetMapping("/eliminar/{id}")
    public String eliminarDireccion(@PathVariable("id") Long id) {
        direccionesService.eliminarDireccion(id);
        return "redirect:/direcciones";
    }
}


