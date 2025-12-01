package com.example.demo.controller;

import com.example.demo.model.Cliente;
import com.example.demo.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // 1. LISTADOS

    @GetMapping
    public ResponseEntity<List<Cliente>> obtenerTodos() {
        return ResponseEntity.ok(clienteService.listarClientes());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Cliente>> obtenerActivos() {
        return ResponseEntity.ok(clienteService.listarActivos());
    }

    @GetMapping("/morosos")
    public ResponseEntity<List<Cliente>> obtenerMorosos() {
        return ResponseEntity.ok(clienteService.listarMorosos());
    }

    @GetMapping("/tipo/{tipoCliente}/{subtipoCliente}")
    public ResponseEntity<List<Cliente>> obtenerPorTipo(
            @PathVariable String tipoCliente,
            @PathVariable String subtipoCliente) {
        return ResponseEntity.ok(clienteService.listarPorTipo(tipoCliente, subtipoCliente));
    }

    // 2. BUSQUEDA

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerPorId(@PathVariable Long id) {
        return clienteService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/documento/{documento}")
    public ResponseEntity<Cliente> obtenerPorDocumento(@PathVariable String documento) {
        Cliente cliente = clienteService.obtenerPorDocumento(documento);
        return cliente != null ? ResponseEntity.ok(cliente) : ResponseEntity.notFound().build();
    }

    @GetMapping("/buscar/nombre")
    public ResponseEntity<List<Cliente>> buscarPorNombre(@RequestParam String q) {
        return ResponseEntity.ok(clienteService.buscarPorNombre(q));
    }

    @GetMapping("/buscar/razon")
    public ResponseEntity<List<Cliente>> buscarPorRazonSocial(@RequestParam String q) {
        return ResponseEntity.ok(clienteService.buscarPorRazonSocial(q));
    }

    // 3. CREAR

    @PostMapping
    public ResponseEntity<Cliente> crearCliente(@RequestBody Cliente cliente) {
        Cliente creado = clienteService.guardarCliente(cliente);
        return ResponseEntity.status(201).body(creado);
    }

    // 4. ACTUALIZAR

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizarCliente(
            @PathVariable Long id,
            @RequestBody Cliente clienteActualizado) {

        return clienteService.obtenerPorId(id)
                .map(clienteExistente -> {
                    // Actualizar campos permitidos
                    clienteExistente.setTipoCliente(clienteActualizado.getTipoCliente());
                    clienteExistente.setSubtipoCliente(clienteActualizado.getSubtipoCliente());
                    clienteExistente.setNombres(clienteActualizado.getNombres());
                    clienteExistente.setApellidos(clienteActualizado.getApellidos());
                    clienteExistente.setRazonSocial(clienteActualizado.getRazonSocial());
                    clienteExistente.setTipoDocumento(clienteActualizado.getTipoDocumento());
                    clienteExistente.setDocumentoIdentidad(clienteActualizado.getDocumentoIdentidad());
                    clienteExistente.setEmail(clienteActualizado.getEmail());
                    clienteExistente.setTelefono(clienteActualizado.getTelefono());
                    clienteExistente.setDireccionPrincipal(clienteActualizado.getDireccionPrincipal());
                    clienteExistente.setEstado(clienteActualizado.getEstado());

                    // NO tocar deuda ni moroso (se actualiza por ventas/pagos)
                    Cliente actualizado = clienteService.guardarCliente(clienteExistente);
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 5. ELIMINAR / ESTADO
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable Long id,
            @RequestBody boolean estado) {
        clienteService.cambiarEstado(id, estado);
        return ResponseEntity.noContent().build();
    }

    // 6. DEUDA
    @PostMapping("/{id}/venta")
    public ResponseEntity<Cliente> registrarVenta(
            @PathVariable Long id,
            @RequestBody BigDecimal monto) {
        clienteService.registrarVenta(id, monto);
        return clienteService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/pago")
    public ResponseEntity<Cliente> registrarPago(
            @PathVariable Long id,
            @RequestBody BigDecimal monto) {
        clienteService.registrarPago(id, monto);
        return clienteService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
