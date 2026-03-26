package org.bolsa.empleo.service;

import org.bolsa.empleo.model.Usuario;

import java.util.List;

public interface UsuarioService {
    Usuario aprobarUsuario(Integer idUsuario);

    Usuario desactivarUsuario(Integer idUsuario);

    List<Usuario> listarEmpresasPendientes();

    List<Usuario> listarOferentesPendientes();
}

