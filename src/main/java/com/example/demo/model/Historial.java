package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "direcciones", "historial"})
    private Cliente cliente;

    @PastOrPresent(message = "La fecha no puede ser futura")
    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    @NotBlank(message = "Debe seleccionar el tipo de interacción")
    @Size(max = 50, message = "El tipo de interacción no puede superar los 50 caracteres")
    @Pattern(
            regexp = "^(consulta|compra|reclamo|devolucion)$",
            message = "El tipo de interacción debe ser: consulta, compra, reclamo o devolucion"
    )
    @Column(length = 50, nullable = false)
    private String tipoInteraccion;

    @NotBlank(message = "El detalle no puede estar vacío")
    @Size(max = 500, message = "El detalle no puede superar los 500 caracteres")
    @Column(columnDefinition = "TEXT", nullable = false)
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
