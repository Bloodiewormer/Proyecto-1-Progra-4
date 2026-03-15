package org.bolsa.empleo.service.impl;

import org.bolsa.empleo.dto.LoginRequestDto;
import org.bolsa.empleo.dto.LoginResponseDto;
import org.bolsa.empleo.model.EstadoUsuario;
import org.bolsa.empleo.model.Usuario;
import org.bolsa.empleo.repository.UsuarioRepository;
import org.bolsa.empleo.service.AuthService;
import org.bolsa.empleo.util.PasswordHashUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthServiceImpl implements AuthService {
    private final UsuarioRepository usuarioRepository;

    public AuthServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public LoginResponseDto login(LoginRequestDto dto) {
        String credencial = dto.getCredencial().trim();
        Usuario usuario = usuarioRepository
                .findByCorreoIgnoreCaseOrIdentificacion(credencial, credencial)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales invalidas"));

        if (!EstadoUsuario.ACTIVO.name().equalsIgnoreCase(usuario.getEstado())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuario no activo");
        }

        String hashCalculado = PasswordHashUtil.hash(dto.getClave(), usuario.getPasswordSalt());
        if (!hashCalculado.equals(usuario.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales invalidas");
        }

        return new LoginResponseDto(usuario.getId(), usuario.getRol(), usuario.getEstado());
    }

    @Override
    public void logout() {
        // Se mantiene como no-op: el controller invalida la sesion HTTP.
    }
}

