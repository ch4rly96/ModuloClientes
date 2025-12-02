package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pagos_clientes")
public class PagosClientes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cuenta_cobrar", nullable = false)
    private CuentasPorCobrar cuentaPorCobrar;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDate fechaPago;

    @NotNull
    @Column(name = "monto", precision = 12, scale = 2, nullable = false)
    private BigDecimal monto;

    @NotBlank
    @Column(name = "metodo_pago", length = 20, nullable = false)
    private String metodoPago;

    @Column(name = "referencia", length = 50)
    private String referencia;

    // Constructor vacio
    public PagosClientes() {
    }

    public PagosClientes(CuentasPorCobrar cuentaPorCobrar, LocalDate fechaPago, BigDecimal monto, String metodoPago, String referencia) {
        this.cuentaPorCobrar = cuentaPorCobrar;
        this.fechaPago = fechaPago;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.referencia = referencia;
    }

    public Long getIdPago() {
        return idPago;
    }

    public void setIdPago(Long idPago) {
        this.idPago = idPago;
    }

    public CuentasPorCobrar getCuentaPorCobrar() {
        return cuentaPorCobrar;
    }

    public void setCuentaPorCobrar(CuentasPorCobrar cuentaPorCobrar) {
        this.cuentaPorCobrar = cuentaPorCobrar;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}
