package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reclamos_clientes")
public class Reclamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReclamo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "numero_reclamo", length = 20, unique = true, nullable = false)
    private String numeroReclamo; // Ej: REC-2025-0001

    @NotBlank(message = "El motivo es obligatorio")
    @Size(max = 100)
    @Column(length = 100, nullable = false)
    private String motivo;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 1000)
    @Column(columnDefinition = "TEXT", nullable = false)
    private String descripcion;

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "PENDIENTE|EN_PROCESO|RESUELTO|CERRADO|RECHAZADO")
    @Column(length = 20, nullable = false)
    private String estado = "PENDIENTE";

    @Column(name = "fecha_reclamo", nullable = false, updatable = false)
    private LocalDateTime fechaReclamo = LocalDateTime.now();

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    @Size(max = 500)
    @Column(length = 500)
    private String solucion;

    // === AUDITORIA ===
    @Column(name = "creado_por", length = 100, updatable = false)
    private String creadoPorNombre;

    @Column(name = "actualizado_por", length = 100)
    private String actualizadoPorNombre;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    // CONSTRUCTORES
    public Reclamo() {}

    public Reclamo(Cliente cliente, String numeroReclamo, String motivo, String descripcion, String creadoPor) {
        this.cliente = cliente;
        this.numeroReclamo = numeroReclamo;
        this.motivo = motivo;
        this.descripcion = descripcion;
        this.creadoPorNombre = creadoPor;
    }

    // GETTERS Y SETTERS
    public Long getIdReclamo() { return idReclamo; }
    public void setIdReclamo(Long idReclamo) { this.idReclamo = idReclamo; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public String getNumeroReclamo() { return numeroReclamo; }
    public void setNumeroReclamo(String numeroReclamo) { this.numeroReclamo = numeroReclamo; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaReclamo() { return fechaReclamo; }

    public LocalDateTime getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(LocalDateTime fechaResolucion) { this.fechaResolucion = fechaResolucion; }

    public String getSolucion() { return solucion; }
    public void setSolucion(String solucion) { this.solucion = solucion; }

    public String getCreadoPorNombre() { return creadoPorNombre; }
    public void setCreadoPorNombre(String creadoPorNombre) { this.creadoPorNombre = creadoPorNombre; }

    public String getActualizadoPorNombre() { return actualizadoPorNombre; }
    public void setActualizadoPorNombre(String actualizadoPorNombre) { this.actualizadoPorNombre = actualizadoPorNombre; }

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(LocalDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }

    @PreUpdate
    public void preUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }
}
