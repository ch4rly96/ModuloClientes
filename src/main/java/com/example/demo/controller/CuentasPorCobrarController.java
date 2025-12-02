package com.example.demo.controller;

import com.example.demo.model.CuentasPorCobrar;
import com.example.demo.service.CuentasPorCobrarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/cuentas")
public class CuentasPorCobrarController {

    @Autowired
    private CuentasPorCobrarService cuentasPorCobrarService;

    // Crear cuenta
    @PostMapping("/crear")
    public String crearCuenta(@ModelAttribute CuentasPorCobrar cuenta) {
        cuentasPorCobrarService.crearCuenta(cuenta);
        return "redirect:/cuentas";  // Redirigir a la lista de cuentas
    }

    // Realizar pago
    @PostMapping("/{id}/pago")
    public String realizarPago(@PathVariable Long id, @RequestParam BigDecimal montoPago) {
        cuentasPorCobrarService.realizarPago(id, montoPago);
        return "redirect:/cuentas/" + id;  // Redirigir al detalle de la cuenta
    }

    // Actualizar estado manualmente
    @PostMapping("/{id}/estado")
    public String actualizarEstado(@PathVariable Long id, @RequestParam String estado) {
        cuentasPorCobrarService.actualizarEstado(id, estado);
        return "redirect:/cuentas/" + id;  // Redirigir al detalle de la cuenta
    }
}
