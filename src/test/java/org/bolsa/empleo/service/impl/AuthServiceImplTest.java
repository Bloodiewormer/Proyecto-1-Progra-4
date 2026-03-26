package org.bolsa.empleo.service.impl;

import org.bolsa.empleo.dto.RegistroEmpresaDto;
import org.bolsa.empleo.model.Empresa;
import org.bolsa.empleo.model.Usuario;
import org.bolsa.empleo.repository.EmpresaRepository;
import org.bolsa.empleo.repository.OferenteRepository;
import org.bolsa.empleo.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UsuarioRepository  usuarioRepository;
    @Mock private EmpresaRepository  empresaRepository;
    @Mock private OferenteRepository oferenteRepository;

    private AuthServiceImpl authService;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthServiceImpl(
                usuarioRepository, empresaRepository, oferenteRepository, passwordEncoder);
    }

    @Test
    void registrarEmpresaGuardaUsuarioConHashBcrypt() {
        RegistroEmpresaDto dto = new RegistroEmpresaDto();
        dto.setNombre("Empresa Test");
        dto.setIdentificacion("3-101-123456");
        dto.setCorreo("empresa@test.com");
        dto.setTelefono("2222-3333");
        dto.setLocalizacion("San José");
        dto.setDescripcion("Empresa de prueba");
        dto.setPassword("MiClave123");

        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setId(1);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);

        authService.registrarEmpresa(dto);

        // Capturar el usuario que se intentó persistir
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());

        Usuario usuario = captor.getValue();
        assertEquals("EMPRESA", usuario.getRol());
        assertEquals("PENDIENTE", usuario.getEstado());

        // Verificar que el hash almacenado corresponde a la contraseña original
        assertTrue(passwordEncoder.matches("MiClave123", usuario.getPasswordHash()),
                "El hash BCrypt debe corresponder a la contraseña original");

        // Verificar que también se guardó la empresa
        verify(empresaRepository).save(any(Empresa.class));
    }
}