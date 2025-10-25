package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_clientes")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Historial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHistorial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler","direcciones", "historial"})
    private Cliente cliente;

    private LocalDateTime fecha = LocalDateTime.now();

    @Column(length = 50)
    private String tipoInteraccion; // consulta, compra, reclamo, devolucion

    @Column(columnDefinition = "TEXT")
    private String detalle;

    // Constructor vacío (obligatorio)
    public Historial() {
    }

    // Constructor con parámetros
    public Historial(Long idHistorial, Cliente cliente, LocalDateTime fecha, String tipoInteraccion, String detalle) {
        this.idHistorial = idHistorial;
        this.cliente = cliente;
        this.fecha = fecha;
        this.tipoInteraccion = tipoInteraccion;
        this.detalle = detalle;
    }

    // Getters y Setters
    public Long getIdHistorial() {
        return idHistorial;
    }

    public void setIdHistorial(Long idHistorial) {
        this.idHistorial = idHistorial;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getTipoInteraccion() {
        return tipoInteraccion;
    }

    public void setTipoInteraccion(String tipoInteraccion) {
        this.tipoInteraccion = tipoInteraccion;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    @Override
    public String toString() {
        return "Historial{" +
                "idHistorial=" + idHistorial +
                ", fecha=" + fecha +
                ", tipoInteraccion='" + tipoInteraccion + '\'' +
                ", detalle='" + detalle + '\'' +
                '}';
    }
}
