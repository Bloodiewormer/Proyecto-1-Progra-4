package org.bolsa.empleo.service;

import org.bolsa.empleo.dto.RegistroEmpresaDto;
import org.bolsa.empleo.dto.RegistroOferenteDto;


public interface AuthService {
    void registrarEmpresa(RegistroEmpresaDto dto);
    void registrarOferente(RegistroOferenteDto dto);
}