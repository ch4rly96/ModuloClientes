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

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por", updatable = false)
    private Usuario creadoPor;

    @NotBlank(message = "Debe seleccionar el tipo de interacción")
    @Pattern(regexp = "consulta|compra|reclamo|devolucion",
            message = "Tipo debe ser: consulta, compra, reclamo o devolucion")
    @Column(length = 50, nullable = false)
    private String tipoInteraccion;

    @NotBlank(message = "El detalle no puede estar vacío")
    @Size(max = 500, message = "El detalle no puede superar los 500 caracteres")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String detalle;


    public Historial() {}

    public Historial(Cliente cliente, Usuario creadoPor, String tipoInteraccion, String detalle) {
        this.cliente = cliente;
        this.creadoPor = creadoPor;
        this.tipoInteraccion = tipoInteraccion;
        this.detalle = detalle;
    }

    // GETTERS (sin setter para creadoEn)

    public Long getIdHistorial() { return idHistorial; }
    public Cliente getCliente() { return cliente; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
    public Usuario getCreadoPor() { return creadoPor; }
    public String getTipoInteraccion() { return tipoInteraccion; }
    public String getDetalle() { return detalle; }

    // Setters (solo para campos editables)
    public void setTipoInteraccion(String tipoInteraccion) { this.tipoInteraccion = tipoInteraccion; }
    public void setDetalle(String detalle) { this.detalle = detalle; }

    @Override
    public String toString() {
        return "Historial{" +
                "idHistorial=" + idHistorial +
                ", creadoEn=" + creadoEn +
                ", tipoInteraccion='" + tipoInteraccion + '\'' +
                ", detalle='" + detalle + '\'' +
                '}';
    }
}
