package com.example.demo.repository;

import com.example.demo.model.Direcciones;
import com.example.demo.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DireccionesRepository extends JpaRepository<Direcciones, Long> {
    // Listar todas las direcciones de un cliente
    List<Direcciones> findByCliente(Cliente cliente);

    // Listar direcciones por tipo (ej: principal, envio, facturacion)
    List<Direcciones> findByTipoDireccion(String tipoDireccion);
}