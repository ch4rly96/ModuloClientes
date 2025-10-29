package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "direcciones_clientes")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Direcciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDireccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "direcciones"})
    private Cliente cliente;

    @NotBlank(message = "Debe seleccionar el tipo de dirección")
    @Pattern(regexp = "principal|envio|facturacion", message = "El tipo de dirección debe ser 'principal', 'envio' o 'facturacion'")
    @Column(length = 20)
    private String tipoDireccion;

    @NotBlank(message = "La dirección no puede estar vacía")
    @Size(max = 255, message = "La dirección no puede tener más de 255 caracteres")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String direccion;

    @NotBlank(message = "La ciudad no puede estar vacía")
    @Size(max = 50, message = "La ciudad no puede tener más de 50 caracteres")
    private String ciudad;

    @NotBlank(message = "El departamento no puede estar vacío")
    @Size(max = 50, message = "El departamento no puede tener más de 50 caracteres")
    private String departamento;

    @NotBlank(message = "El país no puede estar vacío")
    @Size(max = 50, message = "El país no puede tener más de 50 caracteres")
    @Column(nullable = false, columnDefinition = "VARCHAR(100) DEFAULT 'Perú'")
    private String pais = "Perú";

    // Constructor vacío (obligatorio)
    public Direcciones() {
    }

    // Constructor con parámetros
    public Direcciones(Long idDireccion, Cliente cliente, String tipoDireccion, String direccion,
                              String ciudad, String departamento, String pais) {
        this.idDireccion = idDireccion;
        this.cliente = cliente;
        this.tipoDireccion = tipoDireccion;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.departamento = departamento;
        this.pais = pais;
    }

    // Getters y Setters
    public Long getIdDireccion() {
        return idDireccion;
    }

    public void setIdDireccion(Long idDireccion) {
        this.idDireccion = idDireccion;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getTipoDireccion() {
        return tipoDireccion;
    }

    public void setTipoDireccion(String tipoDireccion) {
        this.tipoDireccion = tipoDireccion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    @Override
    public String toString() {
        return "Direcciones{" +
                "idDireccion=" + idDireccion +
                ", tipoDireccion='" + tipoDireccion + '\'' +
                ", direccion='" + direccion + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", departamento='" + departamento + '\'' +
                ", pais='" + pais + '\'' +
                '}';
    }
}

