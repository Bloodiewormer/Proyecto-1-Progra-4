package org.bolsa.empleo.service;

import org.bolsa.empleo.dto.PuestoCreateDto;
import org.bolsa.empleo.dto.PuestoFiltroDto;
import org.bolsa.empleo.model.Puesto;

import java.util.List;

public interface PuestoService {
    List<Puesto> listarRecientes();

    List<Puesto> listarPorEmpresa(Integer idEmpresa);

    List<Puesto> buscarPorFiltros(PuestoFiltroDto dto);

    Puesto crear(PuestoCreateDto dto, Integer idEmpresa);

    void desactivar(Integer idPuesto);
}

