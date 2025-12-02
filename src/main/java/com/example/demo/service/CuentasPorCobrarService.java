package com.example.demo.service;

import com.example.demo.model.CuentasPorCobrar;
import com.example.demo.model.PagosClientes;
import com.example.demo.repository.CuentasPorCobrarRepository;
import com.example.demo.repository.PagosClientesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class CuentasPorCobrarService {

    @Autowired
    private CuentasPorCobrarRepository cuentasPorCobrarRepository;

    @Autowired
    private PagosClientesRepository pagosClientesRepository;

    // Crear cuenta por cobrar
    public CuentasPorCobrar crearCuenta(CuentasPorCobrar cuenta) {
        return cuentasPorCobrarRepository.save(cuenta);
    }

    // Realizar un pago y actualizar la cuenta
    public CuentasPorCobrar realizarPago(Long idCuenta, BigDecimal montoPago) {
        CuentasPorCobrar cuenta = cuentasPorCobrarRepository.findById(idCuenta)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));

        if (montoPago.compareTo(cuenta.getMontoPendiente()) > 0) {
            throw new IllegalArgumentException("El monto del pago no puede ser mayor al monto pendiente");
        }

        // Registrar el pago
        PagosClientes pago = new PagosClientes();
        pago.setCuentaPorCobrar(cuenta);
        pago.setMonto(montoPago);
        pago.setMetodoPago("efectivo");  // Aquí se podría capturar desde el formulario
        pago.setFechaPago(LocalDate.now());
        pagosClientesRepository.save(pago);

        // Actualizar el estado de la cuenta
        cuenta.setMontoPagado(cuenta.getMontoPagado().add(montoPago));
        cuenta.setMontoPendiente(cuenta.getMontoTotal().subtract(cuenta.getMontoPagado()));

        if (cuenta.getMontoPendiente().compareTo(BigDecimal.ZERO) == 0) {
            cuenta.setEstado(CuentasPorCobrar.EstadoCuenta.PAGADO);
        } else if (cuenta.getMontoPagado().compareTo(BigDecimal.ZERO) > 0) {
            cuenta.setEstado(CuentasPorCobrar.EstadoCuenta.PARCIAL);
        }

        return cuentasPorCobrarRepository.save(cuenta);
    }

    // Actualizar el estado manualmente
    public CuentasPorCobrar actualizarEstado(Long idCuenta, String estado) {
        CuentasPorCobrar cuenta = cuentasPorCobrarRepository.findById(idCuenta)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));

        cuenta.setEstado(CuentasPorCobrar.EstadoCuenta.valueOf(estado));
        return cuentasPorCobrarRepository.save(cuenta);
    }
}
