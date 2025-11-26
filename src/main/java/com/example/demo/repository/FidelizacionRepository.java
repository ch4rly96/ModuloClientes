package com.example.demo.repository;

import com.example.demo.model.Fidelizacion;
import com.example.demo.model.Fidelizacion.NivelFidelizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FidelizacionRepository extends JpaRepository<Fidelizacion, Long> {

    // === BÚSQUEDA POR CLIENTE ===
    Optional<Fidelizacion> findByClienteIdCliente(Long idCliente);

    // === BÚSQUEDA POR NIVEL ===
    List<Fidelizacion> findByNivel(NivelFidelizacion nivel);

    // === FILTROS POR PUNTOS ===
    List<Fidelizacion> findByPuntosAcumuladosGreaterThanEqual(Integer puntosMinimos);
    List<Fidelizacion> findByPuntosAcumuladosLessThanEqual(Integer puntosMaximos);
    List<Fidelizacion> findByPuntosAcumuladosBetween(Integer puntosMin, Integer puntosMax);

    // === CLIENTES CON MÁS PUNTOS ===
    List<Fidelizacion> findByOrderByPuntosAcumuladosDesc();

    // === TOP N CLIENTES CON MÁS PUNTOS ===
    @Query("SELECT f FROM Fidelizacion f ORDER BY f.puntosAcumulados DESC LIMIT :limite")
    List<Fidelizacion> findTopClientes(@Param("limite") Integer limite);

    // === BÚSQUEDA POR RANGO DE FECHAS ===
    List<Fidelizacion> findByUltimaActualizacionBetween(java.time.LocalDateTime fechaInicio, java.time.LocalDateTime fechaFin);

    // === CLIENTES ELEGIBLES PARA CANJE (más de X puntos) ===
    @Query("SELECT f FROM Fidelizacion f WHERE f.puntosAcumulados >= :puntosMinimos ORDER BY f.puntosAcumulados DESC")
    List<Fidelizacion> findClientesParaCanje(@Param("puntosMinimos") Integer puntosMinimos);

    // === ESTADÍSTICAS DE FIDELIZACIÓN ===
    @Query("SELECT COUNT(f), COALESCE(SUM(f.puntosAcumulados), 0), COALESCE(AVG(f.puntosAcumulados), 0) FROM Fidelizacion f")
    Object[] obtenerEstadisticasGenerales();

    @Query("SELECT f.nivel, COUNT(f), COALESCE(AVG(f.puntosAcumulados), 0) FROM Fidelizacion f GROUP BY f.nivel")
    List<Object[]> obtenerEstadisticasPorNivel();

    // === VERIFICACIÓN DE EXISTENCIA ===
    boolean existsByClienteIdCliente(Long idCliente);

    // === CLIENTES POR NIVEL ORDENADOS POR PUNTOS ===
    List<Fidelizacion> findByNivelOrderByPuntosAcumuladosDesc(NivelFidelizacion nivel);
}