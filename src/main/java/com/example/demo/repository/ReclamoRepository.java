package com.example.demo.repository;

import com.example.demo.model.Reclamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReclamoRepository extends JpaRepository<Reclamo, Long> {

    List<Reclamo> findByClienteIdClienteOrderByFechaReclamoDesc(Long idCliente);

    List<Reclamo> findByEstadoOrderByFechaReclamoDesc(String estado);

    List<Reclamo> findByNumeroReclamoContainingIgnoreCase(String numero);

    // Para generar numero automatico
    Reclamo findTopByOrderByIdReclamoDesc();
}