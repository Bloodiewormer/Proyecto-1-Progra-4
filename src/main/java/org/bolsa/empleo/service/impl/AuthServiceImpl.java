package org.bolsa.empleo.service.impl;

import org.bolsa.empleo.dto.LoginRequestDto;
import org.bolsa.empleo.dto.LoginResponseDto;
import org.bolsa.empleo.dto.RegistroEmpresaDto;
import org.bolsa.empleo.dto.RegistroOferenteDto;
import org.bolsa.empleo.model.*;
import org.bolsa.empleo.repository.EmpresaRepository;
import org.bolsa.empleo.repository.OferenteRepository;
import org.bolsa.empleo.repository.UsuarioRepository;
import org.bolsa.empleo.service.AuthService;
import org.bolsa.empleo.util.PasswordHashUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthServiceImpl implements AuthService {
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final OferenteRepository oferenteRepository;

    public AuthServiceImpl(UsuarioRepository usuarioRepository, EmpresaRepository empresaRepository, OferenteRepository oferenteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.oferenteRepository = oferenteRepository;
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

    @Override
    @Transactional
    public void registrarEmpresa(RegistroEmpresaDto dto) {

        Usuario usuario = new Usuario();
        usuario.setCorreo(dto.getCorreo());
        usuario.setIdentificacion(dto.getIdentificacion());
        usuario.setRol(Rol.EMPRESA.name());
        usuario.setEstado(EstadoUsuario.PENDIENTE.name());

        String salt = PasswordHashUtil.generateSalt();
        String hash = PasswordHashUtil.hash(dto.getPassword(), salt);
        usuario.setPasswordHash(hash);
        usuario.setPasswordSalt(salt);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // Crear Empresa
        Empresa empresa = new Empresa();
        empresa.setUsuario(usuarioGuardado);
        empresa.setNombre(dto.getNombre());
        empresa.setLocalizacion(dto.getLocalizacion());
        empresa.setTelefono(dto.getTelefono());
        empresa.setDescripcion(dto.getDescripcion());

        empresaRepository.save(empresa);
    }

    @Override
    @Transactional
    public void registrarOferente(RegistroOferenteDto dto) {
        // Crear Usuario
        Usuario usuario = new Usuario();
        usuario.setCorreo(dto.getCorreo());
        usuario.setIdentificacion(dto.getIdentificacion());
        usuario.setRol(Rol.OFERENTE.name());
        usuario.setEstado(EstadoUsuario.PENDIENTE.name());

        String salt = PasswordHashUtil.generateSalt();
        String hash = PasswordHashUtil.hash(dto.getPassword(), salt);
        usuario.setPasswordHash(hash);
        usuario.setPasswordSalt(salt);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // Crear Oferente
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

