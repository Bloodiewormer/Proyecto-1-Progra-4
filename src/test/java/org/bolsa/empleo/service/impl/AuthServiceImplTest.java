package org.bolsa.empleo.service.impl;

import org.bolsa.empleo.dto.LoginRequestDto;
import org.bolsa.empleo.dto.LoginResponseDto;
import org.bolsa.empleo.model.Usuario;
import org.bolsa.empleo.repository.UsuarioRepository;
import org.bolsa.empleo.util.PasswordHashUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(usuarioRepository);
    }

    @Test
    void loginExitosoConCorreo() {
        LoginRequestDto request = new LoginRequestDto();
        request.setCredencial("empresa@demo.com");
        request.setClave("Secreta123");

        Usuario usuario = new Usuario();
        usuario.setId(10);
        usuario.setCorreo("empresa@demo.com");
        usuario.setRol("EMPRESA");
        usuario.setEstado("ACTIVO");
        usuario.setPasswordSalt("SALT01");
        usuario.setPasswordHash(PasswordHashUtil.hash("Secreta123", "SALT01"));

        when(usuarioRepository.findByCorreoIgnoreCaseOrIdentificacion("empresa@demo.com", "empresa@demo.com"))
                .thenReturn(Optional.of(usuario));

        LoginResponseDto response = authService.login(request);

        assertEquals(10, response.getIdUsuario());
        assertEquals("EMPRESA", response.getRol());
    }

    @Test
    void loginFallaPorClaveInvalida() {
        LoginRequestDto request = new LoginRequestDto();
        request.setCredencial("admin001");
        request.setClave("incorrecta");

        Usuario usuario = new Usuario();
        usuario.setIdentificacion("admin001");
        usuario.setRol("ADMIN");
        usuario.setEstado("ACTIVO");
        usuario.setPasswordSalt("SALT02");
        usuario.setPasswordHash(PasswordHashUtil.hash("Correcta123", "SALT02"));

        when(usuarioRepository.findByCorreoIgnoreCaseOrIdentificacion("admin001", "admin001"))
                .thenReturn(Optional.of(usuario));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.login(request));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void loginFallaPorUsuarioNoActivo() {
        LoginRequestDto request = new LoginRequestDto();
        request.setCredencial("oferente@demo.com");
        request.setClave("Clave123");

        Usuario usuario = new Usuario();
        usuario.setCorreo("oferente@demo.com");
        usuario.setRol("OFERENTE");
        usuario.setEstado("PENDIENTE");
        usuario.setPasswordSalt("SALT03");
        usuario.setPasswordHash(PasswordHashUtil.hash("Clave123", "SALT03"));

        when(usuarioRepository.findByCorreoIgnoreCaseOrIdentificacion("oferente@demo.com", "oferente@demo.com"))
                .thenReturn(Optional.of(usuario));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.login(request));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }
}

