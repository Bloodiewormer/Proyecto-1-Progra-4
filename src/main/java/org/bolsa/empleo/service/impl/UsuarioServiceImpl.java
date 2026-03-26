package org.bolsa.empleo.service.impl;

import org.bolsa.empleo.model.EstadoUsuario;
import org.bolsa.empleo.model.Usuario;
import org.bolsa.empleo.repository.UsuarioRepository;
import org.bolsa.empleo.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario aprobarUsuario(Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        usuario.setEstado(EstadoUsuario.ACTIVO.name());
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario desactivarUsuario(Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        usuario.setEstado(EstadoUsuario.INACTIVO.name());
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarEmpresasPendientes() {
        return usuarioRepository.findByEstadoAndRol(EstadoUsuario.PENDIENTE.name(), "EMPRESA");
    }

    public List<Usuario> listarOferentesPendientes() {
        return usuarioRepository.findByEstadoAndRol(EstadoUsuario.PENDIENTE.name(), "OFERENTE");
    }
}

