package org.bolsa.empleo.service.impl;

import org.bolsa.empleo.dto.RegistroEmpresaDto;
import org.bolsa.empleo.dto.RegistroOferenteDto;
import org.bolsa.empleo.model.*;
import org.bolsa.empleo.repository.EmpresaRepository;
import org.bolsa.empleo.repository.OferenteRepository;
import org.bolsa.empleo.repository.UsuarioRepository;
import org.bolsa.empleo.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación de AuthService con Spring Security.
 *
 * Cambios respecto a la versión anterior:
 *  - Se elimina PasswordHashUtil (SHA-256 + salt manual).
 *  - Se usa BCryptPasswordEncoder inyectado desde SecurityConfig.
 *  - BCrypt maneja su propio salt internamente → passwordSalt ya no se usa
 *    (se guarda vacío para no romper el esquema de BD existente).
 *  - El login/logout lo gestiona Spring Security; esta clase solo registra usuarios.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final OferenteRepository oferenteRepository;
    private final PasswordEncoder passwordEncoder;   // ← BCrypt desde SecurityConfig

    public AuthServiceImpl(UsuarioRepository usuarioRepository,
                           EmpresaRepository empresaRepository,
                           OferenteRepository oferenteRepository,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository  = usuarioRepository;
        this.empresaRepository  = empresaRepository;
        this.oferenteRepository = oferenteRepository;
        this.passwordEncoder    = passwordEncoder;
    }

    // ─────────────────────────────────────────────
    // Registro de empresa
    // ─────────────────────────────────────────────
    @Override
    @Transactional
    public void registrarEmpresa(RegistroEmpresaDto dto) {

        Usuario usuario = new Usuario();
        usuario.setCorreo(dto.getCorreo());
        usuario.setIdentificacion(dto.getIdentificacion());
        usuario.setRol(Rol.EMPRESA.name());
        usuario.setEstado(EstadoUsuario.PENDIENTE.name());

        // BCrypt genera y embebe el salt automáticamente en el hash
        usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        usuario.setPasswordSalt("");   // ya no se usa; se mantiene para no alterar esquema BD

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        Empresa empresa = new Empresa();
        empresa.setUsuario(usuarioGuardado);
        empresa.setNombre(dto.getNombre());
        empresa.setLocalizacion(dto.getLocalizacion());
        empresa.setTelefono(dto.getTelefono());
        empresa.setDescripcion(dto.getDescripcion());

        empresaRepository.save(empresa);
    }

    // ─────────────────────────────────────────────
    // Registro de oferente
    // ─────────────────────────────────────────────
    @Override
    @Transactional
    public void registrarOferente(RegistroOferenteDto dto) {

        Usuario usuario = new Usuario();
        usuario.setCorreo(dto.getCorreo());
        usuario.setIdentificacion(dto.getIdentificacion());
        usuario.setRol(Rol.OFERENTE.name());
        usuario.setEstado(EstadoUsuario.PENDIENTE.name());

        usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        usuario.setPasswordSalt("");

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        Oferente oferente = new Oferente();
        oferente.setUsuario(usuarioGuardado);
        oferente.setNombre(dto.getNombre());
        oferente.setApellido(dto.getApellido());
        oferente.setNumIdentificacion(dto.getIdentificacion());
        oferente.setNacionalidad(dto.getNacionalidad());
        oferente.setTelefono(dto.getTelefono());
        oferente.setResidencia(dto.getResidencia());

        oferenteRepository.save(oferente);
    }
}