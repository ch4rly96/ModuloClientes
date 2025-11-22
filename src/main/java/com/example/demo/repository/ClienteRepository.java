package com.example.demo.repository;

import com.example.demo.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // === BÚSQUEDA POR DOCUMENTO Y EMAIL ===
    Cliente findByDocumentoIdentidad(String documentoIdentidad);
    Cliente findByEmail(String email);

    // === FILTROS ERP ===
    List<Cliente> findByEstadoTrue(); // clientes activos
    List<Cliente> findByEsMorosoTrueOrderByDeudaActualDesc(); // morosos ordenados por deuda

    // === BÚSQUEDA POR TIPO ===
    List<Cliente> findByClienteAndTipoCliente(String cliente, String tipoCliente);
    List<Cliente> findByCliente(String cliente); // todos los persona o empresa

    // === BÚSQUEDA AVANZADA ===
    @Query(value = "SELECT * FROM clientes c WHERE unaccent(c.nombres) ILIKE unaccent(:texto) OR unaccent(c.apellidos) ILIKE unaccent(:texto)", nativeQuery = true)
    List<Cliente> buscarPorNombre(@Param("texto") String texto);

    @Query(value = "SELECT * FROM clientes c WHERE unaccent(c.razon_social) ILIKE unaccent(:texto)", nativeQuery = true)
    List<Cliente> buscarPorRazonSocial(@Param("texto") String texto);

    // === DEUDA ===
    List<Cliente> findByDeudaActualGreaterThan(BigDecimal monto);
}