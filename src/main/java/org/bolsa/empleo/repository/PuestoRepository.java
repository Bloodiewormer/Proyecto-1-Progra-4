package org.bolsa.empleo.repository;

import org.bolsa.empleo.model.Puesto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface PuestoRepository extends JpaRepository<Puesto, Integer> {
    List<Puesto> findByEstadoOrderByFechaPublicacionDesc(String estado, Pageable pageable);

    List<Puesto> findByEmpresa_IdOrderByFechaPublicacionDesc(Integer idEmpresa);

    @Query("""
            select p from Puesto p
            where p.estado = 'ACTIVO'
              and (:palabraClave is null or lower(p.titulo) like lower(concat('%', :palabraClave, '%'))
                or lower(p.descripcion) like lower(concat('%', :palabraClave, '%')))
              and (:salarioMin is null or p.salario >= :salarioMin)
              and (:salarioMax is null or p.salario <= :salarioMax)
            order by p.fechaPublicacion desc
            """)
    List<Puesto> buscarActivosPorFiltro(String palabraClave, BigDecimal salarioMin, BigDecimal salarioMax);
}

