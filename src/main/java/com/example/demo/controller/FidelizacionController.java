package com.example.demo.controller;

import com.example.demo.model.Fidelizacion;
import com.example.demo.service.FidelizacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/fidelizacion")
public class FidelizacionController {

    private final FidelizacionService fidelizacionService;

    public FidelizacionController(FidelizacionService fidelizacionService) {
        this.fidelizacionService = fidelizacionService;
    }

    // 1. LISTADOS

    @GetMapping
    public ResponseEntity<List<Fidelizacion>> obtenerTodos() {
        return ResponseEntity.ok(fidelizacionService.listarFidelizaciones());
    }

    @GetMapping("/nivel/{nivel}")
    public ResponseEntity<List<Fidelizacion>> obtenerPorNivel(@PathVariable String nivel) {
        try {
            Fidelizacion.NivelFidelizacion nivelEnum = Fidelizacion.NivelFidelizacion.valueOf(nivel.toUpperCase());
            return ResponseEntity.ok(fidelizacionService.listarPorNivel(nivelEnum));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/top-clientes")
    public ResponseEntity<List<Fidelizacion>> obtenerTopClientes(
            @RequestParam(defaultValue = "10") Integer limite) {
        return ResponseEntity.ok(fidelizacionService.listarTopClientes(limite));
    }

    @GetMapping("/clientes-canje")
    public ResponseEntity<List<Fidelizacion>> obtenerClientesParaCanje(
            @RequestParam(defaultValue = "1") Integer puntosMinimos) {
        return ResponseEntity.ok(fidelizacionService.listarClientesParaCanje(puntosMinimos));
    }

    // 2. BÚSQUEDA

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<Fidelizacion> obtenerPorCliente(@PathVariable Long idCliente) {
        try {
            Fidelizacion fidelizacion = fidelizacionService.obtenerPorCliente(idCliente);
            return ResponseEntity.ok(fidelizacion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fidelizacion> obtenerPorId(@PathVariable Long id) {
        return fidelizacionService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. CREAR

    @PostMapping("/cliente/{idCliente}")
    public ResponseEntity<Fidelizacion> crearFidelizacion(@PathVariable Long idCliente) {
        try {
            Fidelizacion creado = fidelizacionService.crearFidelizacion(idCliente);
            return ResponseEntity.status(201).body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 4. GESTIÓN DE PUNTOS

    @PostMapping("/cliente/{idCliente}/agregar-puntos")
    public ResponseEntity<Fidelizacion> agregarPuntos(
            @PathVariable Long idCliente,
            @RequestParam Integer puntos,
            @RequestParam(required = false) String concepto) {
        try {
            Fidelizacion actualizado;
            if (concepto != null && !concepto.trim().isEmpty()) {
                actualizado = fidelizacionService.agregarPuntos(idCliente, puntos, concepto);
            } else {
                actualizado = fidelizacionService.agregarPuntos(idCliente, puntos);
            }
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/cliente/{idCliente}/canjear-puntos")
    public ResponseEntity<Fidelizacion> canjearPuntos(
            @PathVariable Long idCliente,
            @RequestParam Integer puntos,
            @RequestParam(required = false) String concepto) {
        try {
            Fidelizacion actualizado = fidelizacionService.canjearPuntos(idCliente, puntos, concepto);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/cliente/{idCliente}/compra")
    public ResponseEntity<Fidelizacion> agregarPuntosPorCompra(
            @PathVariable Long idCliente,
            @RequestParam BigDecimal montoCompra) {
        try {
            fidelizacionService.agregarPuntosPorCompra(idCliente, montoCompra);
            Fidelizacion actualizado = fidelizacionService.obtenerPorCliente(idCliente);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 5. ACTUALIZACIONES

    @PutMapping("/cliente/{idCliente}/actualizar-nivel")
    public ResponseEntity<Fidelizacion> actualizarNivel(@PathVariable Long idCliente) {
        try {
            Fidelizacion actualizado = fidelizacionService.actualizarNivel(idCliente);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/cliente/{idCliente}/reiniciar-puntos")
    public ResponseEntity<Fidelizacion> reiniciarPuntos(@PathVariable Long idCliente) {
        try {
            Fidelizacion actualizado = fidelizacionService.reiniciarPuntos(idCliente);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 6. ELIMINAR

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarFidelizacion(@PathVariable Long id) {
        try {
            fidelizacionService.eliminarFidelizacion(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/cliente/{idCliente}")
    public ResponseEntity<Void> eliminarPorCliente(@PathVariable Long idCliente) {
        try {
            fidelizacionService.eliminarPorCliente(idCliente);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 7. VERIFICACIONES Y ESTADÍSTICAS

    @GetMapping("/cliente/{idCliente}/puede-canjear")
    public ResponseEntity<Boolean> puedeCanjearPuntos(
            @PathVariable Long idCliente,
            @RequestParam Integer puntos) {
        try {
            boolean puedeCanjear = fidelizacionService.puedeCanjearPuntos(idCliente, puntos);
            return ResponseEntity.ok(puedeCanjear);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping("/cliente/{idCliente}/existe")
    public ResponseEntity<Boolean> existeFidelizacion(@PathVariable Long idCliente) {
        boolean existe = fidelizacionService.existeFidelizacion(idCliente);
        return ResponseEntity.ok(existe);
    }

    @GetMapping("/cliente/{idCliente}/puntos")
    public ResponseEntity<Integer> obtenerPuntosCliente(@PathVariable Long idCliente) {
        try {
            Integer puntos = fidelizacionService.obtenerPuntosCliente(idCliente);
            return ResponseEntity.ok(puntos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/cliente/{idCliente}/descuento")
    public ResponseEntity<Double> obtenerDescuentoCliente(@PathVariable Long idCliente) {
        try {
            Double descuento = fidelizacionService.obtenerDescuentoCliente(idCliente);
            return ResponseEntity.ok(descuento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<Object[]> obtenerEstadisticas() {
        try {
            Object[] estadisticas = fidelizacionService.obtenerEstadisticasGenerales();
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/estadisticas/niveles")
    public ResponseEntity<List<Object[]>> obtenerEstadisticasPorNivel() {
        try {
            List<Object[]> estadisticas = fidelizacionService.obtenerEstadisticasPorNivel();
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}