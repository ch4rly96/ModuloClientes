package com.example.demo.repository;

import com.example.demo.model.Historial;
import com.example.demo.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialRepository extends JpaRepository<Historial, Long> {
    // Buscar todo el historial de un cliente
    List<Historial> findByCliente(Cliente cliente);

    // Buscar historial por tipo de interacción (consulta, compra, reclamo, etc.)
    List<Historial> findByTipoInteraccion(String tipoInteraccion);
}