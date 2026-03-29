package org.bolsa.empleo.repository;

import org.bolsa.empleo.model.Oferente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OferenteRepository extends JpaRepository<Oferente, Integer> {

    Optional<Oferente> findByUsuarioId(Integer idUsuario);

    Optional<Oferente> findByUsuarioCorreoIgnoreCase(String correo);

    List<Oferente> findByUsuarioEstado(String estado);

    @Query("""
            SELECT o
            FROM Oferente o
            JOIN FETCH o.usuario u
            WHERE o.id = :idOferente
            """)
    Optional<Oferente> findDetalleById(@Param("idOferente") Integer idOferente);
}