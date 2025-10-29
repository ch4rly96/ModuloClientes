package com.example.demo.dto;

public class DireccionesForm {
    private Long idDireccion;    // Para edición
    private Long clienteId;      // ID seleccionado del cliente
    private String tipoDireccion;
    private String direccion;
    private String ciudad;
    private String departamento;
    private String pais = "Perú";

    // Getters y Setters
    public Long getIdDireccion() { return idDireccion; }
    public void setIdDireccion(Long idDireccion) { this.idDireccion = idDireccion; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public String getTipoDireccion() { return tipoDireccion; }
    public void setTipoDireccion(String tipoDireccion) { this.tipoDireccion = tipoDireccion; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
}
