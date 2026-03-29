package org.bolsa.empleo.repository;

import org.bolsa.empleo.model.Puesto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PuestoRepository extends JpaRepository<Puesto, Integer> {

    // --- Usada internamente por empresa (no filtra por tipo) ---
    List<Puesto> findByEmpresa_IdOrderByFechaPublicacionDesc(Integer idEmpresa);

    // --- Página de inicio: solo públicos activos (últimos 5) ---
    @Query("""
            SELECT p FROM Puesto p
            WHERE p.estado = 'ACTIVO'
              AND p.tipoPublicacion = 'PUBLICO'
            ORDER BY p.fechaPublicacion DESC
            """)
    List<Puesto> findRecientesPublicos(Pageable pageable);

    // --- Búsqueda pública por palabra clave / salario (sin características) ---
    @Query("""
            SELECT p FROM Puesto p
            WHERE p.estado = 'ACTIVO'
              AND p.tipoPublicacion = 'PUBLICO'
              AND (:palabraClave IS NULL
                   OR lower(p.titulo) LIKE lower(concat('%', :palabraClave, '%'))
                   OR lower(p.descripcion) LIKE lower(concat('%', :palabraClave, '%')))
              AND (:salarioMin IS NULL OR p.salario >= :salarioMin)
              AND (:salarioMax IS NULL OR p.salario <= :salarioMax)
            ORDER BY p.fechaPublicacion DESC
            """)
    List<Puesto> buscarPublicosActivosPorFiltro(
            @Param("palabraClave") String palabraClave,
            @Param("salarioMin") BigDecimal salarioMin,
            @Param("salarioMax") BigDecimal salarioMax);

    // --- Búsqueda pública por características (+ filtros opcionales) ---
    @Query("""
            SELECT DISTINCT p FROM Puesto p
            JOIN p.puestoCaracteristicas pc
            WHERE p.estado = 'ACTIVO'
              AND p.tipoPublicacion = 'PUBLICO'
              AND pc.caracteristica.id IN :idsCaracteristicas
              AND (:palabraClave IS NULL
                   OR lower(p.titulo) LIKE lower(concat('%', :palabraClave, '%'))
                   OR lower(p.descripcion) LIKE lower(concat('%', :palabraClave, '%')))
              AND (:salarioMin IS NULL OR p.salario >= :salarioMin)
              AND (:salarioMax IS NULL OR p.salario <= :salarioMax)
            ORDER BY p.fechaPublicacion DESC
            """)
    List<Puesto> buscarPublicosPorCaracteristicas(
            @Param("idsCaracteristicas") List<Integer> idsCaracteristicas,
            @Param("palabraClave") String palabraClave,
            @Param("salarioMin") BigDecimal salarioMin,
            @Param("salarioMax") BigDecimal salarioMax);

    // --- Reporte admin: todos los puestos ---
    List<Puesto> findAllByOrderByFechaPublicacionDesc();
}