package org.bolsa.empleo.repository;

import org.bolsa.empleo.model.Oferente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OferenteRepository extends JpaRepository<Oferente, Integer> {
    Optional<Oferente> findByUsuarioId(Integer idUsuario);
}

