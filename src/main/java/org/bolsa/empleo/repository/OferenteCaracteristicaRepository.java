package org.bolsa.empleo.repository;

import org.bolsa.empleo.model.OferenteCaracteristica;
import org.bolsa.empleo.model.OferenteCaracteristicaId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OferenteCaracteristicaRepository extends JpaRepository<OferenteCaracteristica, OferenteCaracteristicaId> {
    List<OferenteCaracteristica> findByOferente_Id(Integer oferenteId);
}
