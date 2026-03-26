package org.bolsa.empleo.service;

import org.bolsa.empleo.dto.RegistroEmpresaDto;
import org.bolsa.empleo.dto.RegistroOferenteDto;

/**
 * Servicio de autenticación simplificado.
 *
 * El login/logout ya es manejado completamente por Spring Security
 * (DaoAuthenticationProvider + formLogin/logout en SecurityConfig).
 *
 * Esta interfaz solo conserva el registro de nuevos usuarios.
 */
public interface AuthService {
    void registrarEmpresa(RegistroEmpresaDto dto);
    void registrarOferente(RegistroOferenteDto dto);
}