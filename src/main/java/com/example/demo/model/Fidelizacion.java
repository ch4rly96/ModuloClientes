package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "fidelizacion_clientes")
public class Fidelizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fidelizacion")
    private Long idFidelizacion;

    @NotNull(message = "El cliente es obligatorio")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", referencedColumnName = "idCliente", nullable = false, unique = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "fidelizacion"})
    private Cliente cliente;

    @Column(name = "puntos_acumulados", nullable = false)
    private Integer puntosAcumulados = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel", length = 20)
    private NivelFidelizacion nivel;

    @Column(name = "ultima_actualizacion", nullable = false)
    private LocalDateTime ultimaActualizacion = LocalDateTime.now();

    // ENUM para los niveles de fidelización
    public enum NivelFidelizacion {
        BRONZE("Bronze", 0, 100),
        SILVER("Silver", 101, 500),
        GOLD("Gold", 501, 1000),
        PLATINUM("Platinum", 1001, Integer.MAX_VALUE);

        private final String nombre;
        private final int puntosMinimos;
        private final int puntosMaximos;

        NivelFidelizacion(String nombre, int puntosMinimos, int puntosMaximos) {
            this.nombre = nombre;
            this.puntosMinimos = puntosMinimos;
            this.puntosMaximos = puntosMaximos;
        }

        public String getNombre() { return nombre; }
        public int getPuntosMinimos() { return puntosMinimos; }
        public int getPuntosMaximos() { return puntosMaximos; }

        public static NivelFidelizacion determinarNivel(int puntos) {
            for (NivelFidelizacion nivel : values()) {
                if (puntos >= nivel.puntosMinimos && puntos <= nivel.puntosMaximos) {
                    return nivel;
                }
            }
            return BRONZE;
        }
    }

    // CONSTRUCTORES
    public Fidelizacion() {}

    public Fidelizacion(Cliente cliente) {
        this.cliente = cliente;
        this.puntosAcumulados = 0;
        this.nivel = NivelFidelizacion.BRONZE;
        this.ultimaActualizacion = LocalDateTime.now();
    }

    public Fidelizacion(Cliente cliente, Integer puntosAcumulados) {
        this.cliente = cliente;
        this.puntosAcumulados = puntosAcumulados != null ? puntosAcumulados : 0;
        actualizarNivel();
        this.ultimaActualizacion = LocalDateTime.now();
    }

    // MÉTODOS DE NEGOCIO
    public void agregarPuntos(Integer puntos) {
        if (puntos != null && puntos > 0) {
            this.puntosAcumulados += puntos;
            actualizarNivel();
            this.ultimaActualizacion = LocalDateTime.now();
        }
    }

    public void canjearPuntos(Integer puntos) {
        if (puntos != null && puntos > 0 && this.puntosAcumulados >= puntos) {
            this.puntosAcumulados -= puntos;
            actualizarNivel();
            this.ultimaActualizacion = LocalDateTime.now();
        }
    }

    public void actualizarNivel() {
        this.nivel = NivelFidelizacion.determinarNivel(this.puntosAcumulados);
    }

    public Double calcularDescuento() {
        return switch (this.nivel) {
            case BRONZE -> 0.0;      // 0% descuento
            case SILVER -> 5.0;      // 5% descuento
            case GOLD -> 10.0;       // 10% descuento
            case PLATINUM -> 15.0;   // 15% descuento
        };
    }

    // GETTERS Y SETTERS
    public Long getIdFidelizacion() { return idFidelizacion; }
    public void setIdFidelizacion(Long idFidelizacion) { this.idFidelizacion = idFidelizacion; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Integer getPuntosAcumulados() { return puntosAcumulados; }
    public void setPuntosAcumulados(Integer puntosAcumulados) {
        this.puntosAcumulados = puntosAcumulados != null ? puntosAcumulados : 0;
        actualizarNivel();
    }

    public NivelFidelizacion getNivel() { return nivel; }
    public void setNivel(NivelFidelizacion nivel) {
        this.nivel = nivel;
    }

    public LocalDateTime getUltimaActualizacion() { return ultimaActualizacion; }
    public void setUltimaActualizacion(LocalDateTime ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }

    // MÉTODOS AUXILIARES
    public boolean puedeCanjear(Integer puntos) {
        return puntos != null && puntos > 0 && this.puntosAcumulados >= puntos;
    }

    public Integer getPuntosDisponibles() {
        return this.puntosAcumulados;
    }

    public String getNombreNivel() {
        return this.nivel != null ? this.nivel.getNombre() : "Sin nivel";
    }

    @Override
    public String toString() {
        return "Fidelizacion{" +
                "idFidelizacion=" + idFidelizacion +
                ", cliente=" + (cliente != null ? cliente.getNombreCompleto() : "null") +
                ", puntosAcumulados=" + puntosAcumulados +
                ", nivel=" + nivel +
                ", ultimaActualizacion=" + ultimaActualizacion +
                '}';
    }
}