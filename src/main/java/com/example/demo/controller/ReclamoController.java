package com.example.demo.controller;

import com.example.demo.model.Reclamo;
import com.example.demo.service.ReclamoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reclamos")
public class ReclamoController {

    private final ReclamoService reclamoService;

    public ReclamoController(ReclamoService reclamoService) {
        this.reclamoService = reclamoService;
    }

    @GetMapping
    public List<Reclamo> listarTodos() {
        return reclamoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Reclamo obtenerPorId(@PathVariable Long id) {
        return reclamoService.obtenerPorId(id);
    }

    @PostMapping
    public Reclamo crear(@RequestBody Reclamo reclamo, @RequestParam String usuarioNombre) {
        return reclamoService.crearReclamo(reclamo, usuarioNombre);
    }

    // AGREGAR ESTE MÉTODO PARA EDITAR RECLAMOS
    @PutMapping("/{id}")
    public Reclamo actualizarReclamo(@PathVariable Long id, @RequestBody Reclamo reclamoActualizado) {
        Reclamo reclamoExistente = reclamoService.obtenerPorId(id);

        // Actualizar campos permitidos
        reclamoExistente.setCliente(reclamoActualizado.getCliente());
        reclamoExistente.setMotivo(reclamoActualizado.getMotivo());
        reclamoExistente.setDescripcion(reclamoActualizado.getDescripcion());

        // Solo permitir cambiar estado si se proporciona
        if (reclamoActualizado.getEstado() != null) {
            reclamoExistente.setEstado(reclamoActualizado.getEstado());
        }

        // Guardar cambios
        return reclamoService.actualizarReclamo(reclamoExistente);
    }

    @PostMapping("/{id}/estado")
    public Reclamo actualizarEstado(@PathVariable Long id, @RequestBody ActualizarEstadoRequest request) {
        return reclamoService.actualizarEstado(id, request.getEstado(), request.getSolucion(), request.getUsuarioNombre());
    }

    // Clase auxiliar para el request
    public static class ActualizarEstadoRequest {
        private String estado;
        private String solucion;
        private String usuarioNombre;

        // getters y setters
        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
        public String getSolucion() { return solucion; }
        public void setSolucion(String solucion) { this.solucion = solucion; }
        public String getUsuarioNombre() { return usuarioNombre; }
        public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }
    }
}