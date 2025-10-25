package com.example.demo.controller;

import com.example.demo.model.Cliente;
import com.example.demo.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Listar todos los clientes
    @GetMapping
    public ResponseEntity<List<Cliente>> obtenerTodos() {
        return ResponseEntity.ok(clienteService.listarClientes());
    }

    // Obtener cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerPorId(@PathVariable Long id) {
        return clienteService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear nuevo cliente
    @PostMapping
    public ResponseEntity<Cliente> crearCliente(@RequestBody Cliente cliente) {
        return ResponseEntity.ok(clienteService.guardarCliente(cliente));
    }

    // Actualizar cliente existente
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizarCliente(@PathVariable Long id, @RequestBody Cliente clienteActualizado) {
        return clienteService.obtenerPorId(id)
                .map(clienteExistente -> {
                    // Actualizamos solo los campos editables
                    clienteExistente.setTipoCliente(clienteActualizado.getTipoCliente());
                    clienteExistente.setNombres(clienteActualizado.getNombres());
                    clienteExistente.setApellidos(clienteActualizado.getApellidos());
                    clienteExistente.setRazonSocial(clienteActualizado.getRazonSocial());
                    clienteExistente.setTipoDocumento(clienteActualizado.getTipoDocumento());
                    clienteExistente.setDocumentoIdentidad(clienteActualizado.getDocumentoIdentidad());
                    clienteExistente.setEmail(clienteActualizado.getEmail());
                    clienteExistente.setTelefono(clienteActualizado.getTelefono());
                    clienteExistente.setDireccionPrincipal(clienteActualizado.getDireccionPrincipal());
                    clienteExistente.setEstado(clienteActualizado.getEstado());

                    // No tocar direcciones ni historial
                    Cliente actualizado = clienteService.guardarCliente(clienteExistente);
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar cliente por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }
}
