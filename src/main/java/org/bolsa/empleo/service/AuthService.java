package org.bolsa.empleo.service;

import org.bolsa.empleo.dto.LoginRequestDto;
import org.bolsa.empleo.dto.LoginResponseDto;

public interface AuthService {
    LoginResponseDto login(LoginRequestDto dto);

    void logout();
}

