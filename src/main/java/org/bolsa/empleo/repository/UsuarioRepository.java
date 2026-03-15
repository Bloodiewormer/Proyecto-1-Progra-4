package org.bolsa.empleo.repository;

import org.bolsa.empleo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByCorreoIgnoreCaseOrIdentificacion(String correo, String identificacion);
}

