package com.example.demo.repository;

import com.example.demo.model.PagosClientes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagosClientesRepository extends JpaRepository<PagosClientes, Long> {
    List<PagosClientes> findByCuentaPorCobrarIdCuenta(Long idCuenta);
}