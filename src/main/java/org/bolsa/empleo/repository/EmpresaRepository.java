package org.bolsa.empleo.repository;

import org.bolsa.empleo.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {
    Optional<Empresa> findByUsuarioId(Integer idUsuario);
}

