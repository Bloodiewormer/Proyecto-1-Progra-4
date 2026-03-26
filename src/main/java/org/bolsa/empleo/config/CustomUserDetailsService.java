package org.bolsa.empleo.config;

import org.bolsa.empleo.model.Usuario;
import org.bolsa.empleo.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String credencial) throws UsernameNotFoundException {
        String trimmed = credencial.trim();

        Usuario usuario = usuarioRepository
                .findByCorreoIgnoreCaseOrIdentificacion(trimmed, trimmed)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado: " + trimmed));

        // Verificar que el usuario esté activo
        if (!"ACTIVO".equalsIgnoreCase(usuario.getEstado())) {
            // Lanzar excepción especial para que Spring Security muestre "cuenta deshabilitada"
            throw new org.springframework.security.authentication.DisabledException(
                    "La cuenta no está activa. Estado: " + usuario.getEstado());
        }

        // El rol en BD es "ADMIN", "EMPRESA" u "OFERENTE".
        // Spring Security requiere el prefijo "ROLE_" para hasRole().
        String authority = "ROLE_" + usuario.getRol().toUpperCase();

        return User.builder()
                .username(trimmed)                     // credencial que escribió el usuario
                .password(usuario.getPasswordHash())   // hash BCrypt almacenado en BD
                .authorities(new SimpleGrantedAuthority(authority))
                .build();
    }
}