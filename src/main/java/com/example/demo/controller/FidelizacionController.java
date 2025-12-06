package com.example.demo.controller;

import com.example.demo.model.Fidelizacion;
import com.example.demo.service.ClienteService;
import com.example.demo.service.FidelizacionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/fidelizacion")
public class FidelizacionController {

    private final FidelizacionService fidelizacionService;
    private final ClienteService clienteService;

    public FidelizacionController(FidelizacionService fidelizacionService, ClienteService clienteService) {
        this.fidelizacionService = fidelizacionService;
        this.clienteService = clienteService;
    }

    // 1. LISTADOS
    @GetMapping
    public String listarClientesFidelizados(Model model) {
        List<Fidelizacion> fidelizaciones = fidelizacionService.listarFidelizaciones();
        model.addAttribute("fidelizaciones", fidelizaciones);
        return "fidelizacion/lista"; // Nombre del archivo HTML para mostrar la lista de fidelización
    }

    @GetMapping("/nivel/{nivel}")
    public String obtenerPorNivel(@PathVariable String nivel, Model model) {
        try {
            Fidelizacion.NivelFidelizacion nivelEnum = Fidelizacion.NivelFidelizacion.valueOf(nivel.toUpperCase());
            model.addAttribute("fidelizaciones", fidelizacionService.listarPorNivel(nivelEnum));
            return "fidelizacion/lista";
        } catch (IllegalArgumentException e) {
            return "error";  // Página de error si el nivel no es válido
        }
    }

    @GetMapping("/top-clientes")
    public String obtenerTopClientes(@RequestParam(defaultValue = "10") Integer limite, Model model) {
        model.addAttribute("fidelizaciones", fidelizacionService.listarTopClientes(limite));
        return "fidelizacion/topClientes"; // Vista para mostrar el top de clientes
    }

    @GetMapping("/clientes-canje")
    public String obtenerClientesParaCanje(@RequestParam(defaultValue = "1") Integer puntosMinimos, Model model) {
        model.addAttribute("fidelizaciones", fidelizacionService.listarClientesParaCanje(puntosMinimos));
        return "fidelizacion/clienteCanje"; // Vista para mostrar clientes que pueden canjear puntos
    }

    // 2. DETALLE
    @GetMapping("/detalle/{idCliente}")
    public String obtenerPorCliente(@PathVariable Long idCliente, Model model) {
        try {
            Fidelizacion fidelizacion = fidelizacionService.obtenerPorCliente(idCliente);
            model.addAttribute("fidelizacion", fidelizacion);
            return "fidelizacion/detalle"; // Vista para mostrar los detalles de la fidelización de un cliente
        } catch (IllegalArgumentException e) {
            return "error";  // Página de error si no se encuentra el cliente
        }
    }


    // 3. CREAR
    @PostMapping("/cliente/{idCliente}")
    public String crearFidelizacion(@PathVariable Long idCliente, Model model) {
        try {
            Fidelizacion creado = fidelizacionService.crearFidelizacion(idCliente);
            model.addAttribute("fidelizacion", creado);
            return "fidelizacion/detalle";  // Redirigir a la vista de detalle de fidelización del cliente
        } catch (IllegalArgumentException e) {
            return "error";  // Página de error si algo sale mal
        }
    }

    // 4. GESTIÓN DE PUNTOS
    @GetMapping("/cliente/{idCliente}/agregar-puntos")
    public String agregarPuntos(@PathVariable Long idCliente, Model model) {
        model.addAttribute("idCliente", idCliente);
        return "fidelizacion/agregarPuntos";  // Vista para agregar puntos
    }

    @PostMapping("/cliente/{idCliente}/agregar-puntos")
    public String agregarPuntos(@PathVariable Long idCliente, @RequestParam Integer puntos, @RequestParam(required = false) String concepto, Model model) {
        try {
            Fidelizacion actualizado;
            if (concepto != null && !concepto.trim().isEmpty()) {
                actualizado = fidelizacionService.agregarPuntos(idCliente, puntos, concepto);
            } else {
                actualizado = fidelizacionService.agregarPuntos(idCliente, puntos);
            }
            model.addAttribute("fidelizacion", actualizado);
            return "fidelizacion/detalle";  // Vista de los detalles de la fidelización después de agregar los puntos
        } catch (IllegalArgumentException e) {
            return "error";  // Página de error si algo sale mal
        }
    }

    @GetMapping("/cliente/{idCliente}/canjear-puntos")
    public String canjearPuntos(@PathVariable Long idCliente, Model model) {
        model.addAttribute("idCliente", idCliente);
        return "fidelizacion/canjearPuntos";  // Vista para canjear puntos
    }

    @PostMapping("/cliente/{idCliente}/canjear-puntos")
    public String canjearPuntos(@PathVariable Long idCliente, @RequestParam Integer puntos, @RequestParam(required = false) String concepto, Model model) {
        try {
            Fidelizacion actualizado = fidelizacionService.canjearPuntos(idCliente, puntos, concepto);
            model.addAttribute("fidelizacion", actualizado);
            return "fidelizacion/detalle";  // Redirigir a la vista de detalle después de canjear los puntos
        } catch (IllegalArgumentException e) {
            return "error";  // Página de error si no se pueden canjear puntos
        }
    }

    // 5. ACTUALIZACIONES
    @PutMapping("/cliente/{idCliente}/actualizar-nivel")
    public String actualizarNivel(@PathVariable Long idCliente, Model model) {
        try {
            Fidelizacion actualizado = fidelizacionService.actualizarNivel(idCliente);
            model.addAttribute("fidelizacion", actualizado);
            return "fidelizacion/detalle";  // Redirigir al detalle con el nivel actualizado
        } catch (IllegalArgumentException e) {
            return "error";  // Página de error si no se encuentra el cliente
        }
    }

    // 6. ELIMINAR
    @DeleteMapping("/cliente/{idCliente}")
    public String eliminarPorCliente(@PathVariable Long idCliente, Model model) {
        try {
            fidelizacionService.eliminarPorCliente(idCliente);
            return "redirect:/fidelizacion";  // Redirigir a la lista de fidelizaciones
        } catch (IllegalArgumentException e) {
            return "error";  // Página de error si no se puede eliminar
        }
    }

    // 7. VERIFICACIONES Y ESTADÍSTICAS
    @GetMapping("/estadisticas")
    public String obtenerEstadisticas(Model model) {
        model.addAttribute("estadisticas", fidelizacionService.obtenerEstadisticasGenerales());
        model.addAttribute("estadisticasPorNivel", fidelizacionService.obtenerEstadisticasPorNivel());
        return "fidelizacion/estadisticas";  // Vista para mostrar las estadísticas
    }
}
