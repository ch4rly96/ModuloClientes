package com.example.demo.controller;

import com.example.demo.model.Historial;
import com.example.demo.service.HistorialService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historiales")
public class HistorialController {

    private final HistorialService historialService;

    public HistorialController(HistorialService historialService) {
        this.historialService = historialService;
    }

    // Listar todos los historiales
    @GetMapping
    public ResponseEntity<List<Historial>> obtenerTodas() {
        return ResponseEntity.ok(historialService.listarHistoriales());
    }

    // Obtener historial por ID
    @GetMapping("/{id}")
    public ResponseEntity<Historial> obtenerPorId(@PathVariable Long id) {
        return historialService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear historial
    @PostMapping
    public ResponseEntity<Historial> crearHistorial(@RequestBody Historial historial) {
        return ResponseEntity.ok(historialService.guardarHistorial(historial));
    }

    // Actualizar historial
    @PutMapping("/{id}")
    public ResponseEntity<Historial> actualizarHistorial(@PathVariable Long id, @RequestBody Historial historial) {
        if (historialService.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        historial.setIdHistorial(id);
        return ResponseEntity.ok(historialService.guardarHistorial(historial));
    }

    // Eliminar historial
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarHistorial(@PathVariable Long id) {
        historialService.eliminarHistorial(id);
        return ResponseEntity.noContent().build();
    }
}
