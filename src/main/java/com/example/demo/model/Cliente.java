package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCliente;

    @NotBlank(message = "Debe seleccionar el tipo de cliente (persona o empresa)")
    @Column(nullable = false)
    private String tipoCliente; // persona o empresa

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

    private LocalDateTime fechaRegistro = LocalDateTime.now();

    private Boolean estado = true;

    //  Relaciones bidireccionales
    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "cliente"})
    private List<Direcciones> direcciones;

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "cliente"})
    private List<Historial> historial;

    // Constructor vacío (obligatorio para JPA)
    public Cliente() {
    }

    // Constructor con todos los campos
    public Cliente(Long idCliente, String tipoCliente, String nombres, String apellidos, String razonSocial,
                   String tipoDocumento, String documentoIdentidad, String email, String telefono,
                   String direccionPrincipal, LocalDateTime fechaRegistro, Boolean estado) {
        this.idCliente = idCliente;
        this.tipoCliente = tipoCliente;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.razonSocial = razonSocial;
        this.tipoDocumento = tipoDocumento;
        this.documentoIdentidad = documentoIdentidad;
        this.email = email;
        this.telefono = telefono;
        this.direccionPrincipal = direccionPrincipal;
        this.fechaRegistro = fechaRegistro;
        this.estado = estado;
    }

    // Getters y Setters
    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public String getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(String tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getDocumentoIdentidad() {
        return documentoIdentidad;
    }

    public void setDocumentoIdentidad(String documentoIdentidad) {
        this.documentoIdentidad = documentoIdentidad;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccionPrincipal() {
        return direccionPrincipal;
    }

    public void setDireccionPrincipal(String direccionPrincipal) {
        this.direccionPrincipal = direccionPrincipal;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    // 🔹 toString() (opcional)
    @Override
    public String toString() {
        return "Cliente{" +
                "idCliente=" + idCliente +
                ", tipoCliente='" + tipoCliente + '\'' +
                ", nombres='" + nombres + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", razonSocial='" + razonSocial + '\'' +
                ", tipoDocumento='" + tipoDocumento + '\'' +
                ", documentoIdentidad='" + documentoIdentidad + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", direccionPrincipal='" + direccionPrincipal + '\'' +
                ", fechaRegistro=" + fechaRegistro +
                ", estado=" + estado +
                '}';
    }
}
