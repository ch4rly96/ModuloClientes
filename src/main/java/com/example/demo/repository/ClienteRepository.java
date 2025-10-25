package com.example.demo.repository;

import com.example.demo.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // Buscar cliente por documento de identidad
    Cliente findByDocumentoIdentidad(String documentoIdentidad);

    // Buscar cliente por email
    Cliente findByEmail(String email);
}