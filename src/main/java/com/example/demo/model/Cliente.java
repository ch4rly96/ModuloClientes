package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCliente;

    @NotBlank(message = "Debe seleccionar si es persona o empresa")
    @Column(nullable = false)
    private String cliente; // 'persona' o 'empresa'

    @NotBlank(message = "Debe seleccionar el subtipo de cliente")
    @Column(name = "tipo_cliente", nullable = false)
    private String tipoCliente; // 'natural', 'juridico', 'constructor', 'corporativo'

    // DATOS PERSONALES / EMPRESA

    @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
    private String nombres;

    @Size(max = 50, message = "El apellido no puede superar los 50 caracteres")
    private String apellidos;

    @Size(max = 100, message = "La razón social no puede superar los 100 caracteres")
    private String razonSocial;

    @NotBlank(message = "Debe seleccionar el tipo de documento")
    @Column(nullable = false)
    private String tipoDocumento; // DNI, RUC, CE, PAS

    @NotBlank(message = "El número de documento es obligatorio")
    @Column(nullable = false, unique = true)
    @Size(max = 20, message = "El número de documento no puede superar los 20 caracteres")
    private String documentoIdentidad;

    @Email(message = "Debe ingresar un correo válido")
    @Column(unique = true)
    private String email;

    @Pattern(regexp = "\\d{9}", message = "El teléfono debe tener 9 dígitos")
    private String telefono;

    @Size(max = 255, message = "La dirección principal no puede tener más de 255 caracteres")
    @Column(columnDefinition = "TEXT")
    private String direccionPrincipal;

    // ERP: DEUDA Y ESTADO

    @Column(name = "deuda_actual", nullable = false)
    private BigDecimal deudaActual = BigDecimal.ZERO;

    @Column(name = "es_moroso", nullable = false)
    private Boolean esMoroso = false;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean estado = true;

    // RELACIONES

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "cliente"})
    private List<Direcciones> direcciones;

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "cliente"})
    private List<Historial> historial;

    // CONSTRUCTORES

    public Cliente() {}

    public Cliente(String cliente, String tipoCliente) {
        this.cliente = cliente;
        this.tipoCliente = tipoCliente;
    }

    // GETTERS Y SETTERS

    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getTipoCliente() { return tipoCliente; }
    public void setTipoCliente(String tipoCliente) { this.tipoCliente = tipoCliente; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getDocumentoIdentidad() { return documentoIdentidad; }
    public void setDocumentoIdentidad(String documentoIdentidad) { this.documentoIdentidad = documentoIdentidad; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccionPrincipal() { return direccionPrincipal; }
    public void setDireccionPrincipal(String direccionPrincipal) { this.direccionPrincipal = direccionPrincipal; }

    public BigDecimal getDeudaActual() { return deudaActual; }
    public void setDeudaActual(BigDecimal deudaActual) { this.deudaActual = deudaActual != null ? deudaActual : BigDecimal.ZERO; }

    public Boolean getEsMoroso() { return esMoroso; }
    public void setEsMoroso(Boolean esMoroso) { this.esMoroso = esMoroso != null ? esMoroso : false; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado != null ? estado : true; }

    public List<Direcciones> getDirecciones() { return direcciones; }
    public void setDirecciones(List<Direcciones> direcciones) { this.direcciones = direcciones; }

    public List<Historial> getHistorial() { return historial; }
    public void setHistorial(List<Historial> historial) { this.historial = historial; }

    // METODO AUXILIAR: Nombre completo o razon social

    public String getNombreCompleto() {
        if ("persona".equalsIgnoreCase(cliente)) {
            return String.join(" ",
                    nombres != null ? nombres.trim() : "",
                    apellidos != null ? apellidos.trim() : ""
            ).trim();
        } else {
            return razonSocial != null ? razonSocial.trim() : "";
        }
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "idCliente=" + idCliente +
                ", cliente='" + cliente + '\'' +
                ", tipoCliente='" + tipoCliente + '\'' +
                ", nombreCompleto='" + getNombreCompleto() + '\'' +
                ", documentoIdentidad='" + documentoIdentidad + '\'' +
                ", email='" + email + '\'' +
                ", deudaActual=" + deudaActual +
                ", esMoroso=" + esMoroso +
                ", estado=" + estado +
                '}';
    }
}
