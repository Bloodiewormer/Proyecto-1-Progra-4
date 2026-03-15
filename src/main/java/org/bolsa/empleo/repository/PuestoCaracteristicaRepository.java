package org.bolsa.empleo.repository;

import org.bolsa.empleo.model.PuestoCaracteristica;
import org.bolsa.empleo.model.PuestoCaracteristicaId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PuestoCaracteristicaRepository extends JpaRepository<PuestoCaracteristica, PuestoCaracteristicaId> {
    List<PuestoCaracteristica> findByPuesto_Id(Integer puestoId);
}
