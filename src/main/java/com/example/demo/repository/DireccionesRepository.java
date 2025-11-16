package com.example.demo.repository;

import com.example.demo.model.Direcciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DireccionesRepository extends JpaRepository<Direcciones, Long> {
    List<Direcciones> findByClienteIdCliente(Long idCliente);

    List<Direcciones> findByTipoDireccion(String tipoDireccion);

    Optional<Direcciones> findByClienteIdClienteAndTipoDireccion(
            Long idCliente, String tipoDireccion);

    long countByClienteIdClienteAndTipoDireccion(
            Long idCliente, String tipoDireccion);
}