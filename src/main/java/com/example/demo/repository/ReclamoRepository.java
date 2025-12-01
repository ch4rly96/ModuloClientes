package com.example.demo.repository;

import com.example.demo.model.Reclamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReclamoRepository extends JpaRepository<Reclamo, Long> {

    List<Reclamo> findByClienteIdClienteOrderByFechaReclamoDesc(Long idCliente);

    List<Reclamo> findByNumeroReclamoContainingIgnoreCase(String numero);

    List<Reclamo> findAllByOrderByFechaReclamoDesc();

    @Query("SELECT r FROM Reclamo r " +
            "WHERE LOWER(FUNCTION('unaccent', CONCAT(" +
            "COALESCE(r.cliente.nombres, ''), ' ', " +
            "COALESCE(r.cliente.apellidos, ''), ' ', " +
            "COALESCE(r.cliente.razonSocial, '')" +
            "))) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "ORDER BY r.fechaReclamo DESC")
    List<Reclamo> buscarPorCliente(@Param("q") String q);

    @Query("SELECT r FROM Reclamo r " +
            "WHERE LOWER(FUNCTION('unaccent', CONCAT(" +
            "COALESCE(r.cliente.nombres, ''), ' ', " +
            "COALESCE(r.cliente.apellidos, ''), ' ', " +
            "COALESCE(r.cliente.razonSocial, '')" +
            "))) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "AND r.estado = :estado " +
            "ORDER BY r.fechaReclamo DESC")
    List<Reclamo> buscarPorClienteYEstado(@Param("q") String q, @Param("estado") String estado);

    List<Reclamo> findByEstadoOrderByFechaReclamoDesc(String estado);

    // Para generar numero automatico
    Reclamo findTopByOrderByIdReclamoDesc();
}