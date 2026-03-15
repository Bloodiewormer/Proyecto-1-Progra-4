package org.bolsa.empleo.service;

import org.bolsa.empleo.model.Usuario;

public interface UsuarioService {
    Usuario aprobarUsuario(Integer idUsuario);

    Usuario desactivarUsuario(Integer idUsuario);
}

