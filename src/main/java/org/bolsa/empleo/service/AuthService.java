package org.bolsa.empleo.service;

import org.bolsa.empleo.dto.LoginRequestDto;
import org.bolsa.empleo.dto.LoginResponseDto;
import org.bolsa.empleo.dto.RegistroEmpresaDto;
import org.bolsa.empleo.dto.RegistroOferenteDto;

public interface AuthService {
    LoginResponseDto login(LoginRequestDto dto);

    void registrarEmpresa(RegistroEmpresaDto dto);

    void registrarOferente(RegistroOferenteDto dto);

    void logout();
}

