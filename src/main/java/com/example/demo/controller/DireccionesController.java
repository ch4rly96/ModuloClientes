package com.example.demo.controller;

import com.example.demo.model.Direcciones;
import com.example.demo.service.DireccionesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/direcciones")
public class DireccionesController {

    private final DireccionesService direccionesService;

    public DireccionesController(DireccionesService direccionesService) {
        this.direccionesService = direccionesService;
    }

    // Obtener todas las direcciones
    @GetMapping
    public ResponseEntity<List<Direcciones>> obtenerTodas() {
        return ResponseEntity.ok(direccionesService.listarDirecciones());
    }

    // Obtener una dirección por ID
    @GetMapping("/{id}")
    public ResponseEntity<Direcciones> obtenerPorId(@PathVariable Long id) {
        return direccionesService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear nueva dirección
    @PostMapping
    public ResponseEntity<Direcciones> crearDirecciones(@RequestBody Direcciones direcciones) {
        return ResponseEntity.ok(direccionesService.guardarDireccion(direcciones));
    }

    // Actualizar una dirección existente
    @PutMapping("/{id}")
    public ResponseEntity<Direcciones> actualizarDirecciones(@PathVariable Long id, @RequestBody Direcciones direcciones) {
        if (direccionesService.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        direcciones.setIdDireccion(id);
        return ResponseEntity.ok(direccionesService.guardarDireccion(direcciones));
    }

    // Eliminar una dirección
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDirecciones(@PathVariable Long id) {
        direccionesService.eliminarDireccion(id);
        return ResponseEntity.noContent().build();
    }
}
