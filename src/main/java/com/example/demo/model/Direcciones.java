package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

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

    @Column(length = 20)
    private String tipoDireccion; // principal, envio, facturacion

    @Column(columnDefinition = "TEXT", nullable = false)
    private String direccion;

    private String ciudad;
    private String departamento;

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

