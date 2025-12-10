package com.example.demo.repository;

import com.example.demo.model.Fidelizacion;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FidelizacionRepository extends JpaRepository<Fidelizacion, Long> {
    Optional<Fidelizacion> findByClienteIdCliente(Long idCliente);

    List<Fidelizacion> findByNivel(@Param("nivel") Fidelizacion.NivelFidelizacion nivel);

    @Query("SELECT f FROM Fidelizacion f WHERE (LOWER(FUNCTION('unaccent', CONCAT(COALESCE(f.cliente.nombres, ''), ' ', COALESCE(f.cliente.apellidos, '')))) LIKE LOWER(CONCAT('%', FUNCTION('unaccent', COALESCE(:q, '')), '%')) OR LOWER(FUNCTION('unaccent', f.cliente.razonSocial)) LIKE LOWER(CONCAT('%', FUNCTION('unaccent', COALESCE(:q, '')), '%'))) AND (:nivel IS NULL OR :nivel = '' OR LOWER(CAST(f.nivel AS string)) LIKE LOWER(CONCAT('%', :nivel, '%'))) ORDER BY f.cliente.nombres")
    List<Fidelizacion> buscarClientes(@Param("q") String q, @Param("nivel") String nivel);

    @Query("SELECT f FROM Fidelizacion f WHERE (LOWER(FUNCTION('unaccent', CONCAT(COALESCE(f.cliente.nombres, ''), ' ', COALESCE(f.cliente.apellidos, ''), ' ', COALESCE(f.cliente.razonSocial, '')))) LIKE LOWER(CONCAT('%', FUNCTION('unaccent', :termino), '%'))) ORDER BY f.cliente.nombres")
    List<Fidelizacion> buscarPorCualquierParteDelNombre(@Param("termino") String termino);

    List<Fidelizacion> findByPuntosAcumuladosGreaterThanEqual(Integer puntosMinimos);

    List<Fidelizacion> findByPuntosAcumuladosLessThanEqual(Integer puntosMaximos);

    List<Fidelizacion> findByPuntosAcumuladosBetween(Integer puntosMin, Integer puntosMax);

    List<Fidelizacion> findByOrderByPuntosAcumuladosDesc();

    @Query(value = "SELECT * FROM fidelizacion_clientes ORDER BY puntos_acumulados DESC LIMIT :limite", nativeQuery = true)
    List<Fidelizacion> findTopClientes(@Param("limite") Integer limite);

    List<Fidelizacion> findByUltimaActualizacionBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    @Query("SELECT f FROM Fidelizacion f WHERE f.puntosAcumulados >= :puntosMinimos ORDER BY f.puntosAcumulados DESC")
    List<Fidelizacion> findClientesParaCanje(@Param("puntosMinimos") Integer puntosMinimos);

    // ============ CONSULTAS DE ESTADÍSTICAS CORREGIDAS (NATIVE QUERIES) ============

    // Consulta nativa para estadísticas generales - usa el nombre real de la tabla
    @Query(value = "SELECT " +
            "COUNT(*) as totalClientes, " +
            "COALESCE(SUM(puntos_acumulados), 0) as totalPuntos, " +
            "CASE WHEN COUNT(*) > 0 THEN COALESCE(AVG(puntos_acumulados), 0.0) ELSE 0.0 END as promedioPuntos " +
            "FROM fidelizacion_clientes", nativeQuery = true)
    Object[] obtenerEstadisticasGenerales();

    // Consulta nativa para estadísticas por nivel - usa el nombre real de la tabla
    @Query(value = "SELECT " +
            "UPPER(nivel) as nivel, " +
            "COUNT(*) as cantidadClientes, " +
            "COALESCE(SUM(puntos_acumulados), 0) as puntosTotales, " +
            "CASE WHEN COUNT(*) > 0 THEN COALESCE(AVG(puntos_acumulados), 0.0) ELSE 0.0 END as promedioPuntos " +
            "FROM fidelizacion_clientes " +
            "GROUP BY nivel " +
            "ORDER BY " +
            "CASE UPPER(nivel) " +
            "   WHEN 'PLATINUM' THEN 1 " +
            "   WHEN 'GOLD' THEN 2 " +
            "   WHEN 'SILVER' THEN 3 " +
            "   WHEN 'BRONZE' THEN 4 " +
            "   ELSE 5 END", nativeQuery = true)
    List<Object[]> obtenerEstadisticasPorNivel();

    // Consulta de prueba para verificar conexión con la tabla
    @Query(value = "SELECT COUNT(*) FROM fidelizacion_clientes", nativeQuery = true)
    Long contarTotalRegistros();

    // Consulta para ver todos los registros (para debug)
    @Query(value = "SELECT id_cliente, puntos_acumulados, nivel FROM fidelizacion_clientes", nativeQuery = true)
    List<Object[]> obtenerTodosRegistros();

    boolean existsByClienteIdCliente(Long idCliente);

    List<Fidelizacion> findByNivelOrderByPuntosAcumuladosDesc(Fidelizacion.NivelFidelizacion nivel);
}