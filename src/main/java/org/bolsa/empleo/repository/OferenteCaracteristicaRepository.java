package org.bolsa.empleo.repository;

import org.bolsa.empleo.model.OferenteCaracteristica;
import org.bolsa.empleo.model.OferenteCaracteristicaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OferenteCaracteristicaRepository
        extends JpaRepository<OferenteCaracteristica, OferenteCaracteristicaId> {

    List<OferenteCaracteristica> findByOferente_Id(Integer oferenteId);

    @Query("""
            SELECT oc
            FROM OferenteCaracteristica oc
            JOIN FETCH oc.caracteristica c
            WHERE oc.oferente.id = :idOferente
            """)
    List<OferenteCaracteristica> findDetalleByOferenteId(@Param("idOferente") Integer idOferente);

    @Transactional
    @Modifying
    void deleteByOferente_Id(Integer oferenteId);
}