package com.example.demo.repository;

import com.example.demo.model.Historial;
import com.example.demo.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistorialRepository extends JpaRepository<Historial, Long> {
    // Listar por cliente (ordenado por fecha descendente)
    List<Historial> findByClienteIdClienteOrderByCreadoEnDesc(Long idCliente);

    // Opcional: filtrar por tipo
    List<Historial> findByClienteIdClienteAndTipoInteraccionOrderByCreadoEnDesc(
            Long idCliente, String tipoInteraccion);

    // Opcional: por rango de fechas
    List<Historial> findByClienteIdClienteAndCreadoEnBetweenOrderByCreadoEnDesc(
            Long idCliente, LocalDateTime desde, LocalDateTime hasta);
}