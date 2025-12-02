package com.example.demo.repository;

import com.example.demo.model.CuentasPorCobrar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuentasPorCobrarRepository extends JpaRepository<CuentasPorCobrar, Long> {
    List<CuentasPorCobrar> findByClienteIdCliente(Long idCliente);
    List<CuentasPorCobrar> findByEstadoOrderByFechaVencimientoDesc(String estado);
}
