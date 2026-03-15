package org.bolsa.empleo.service;

import org.bolsa.empleo.dto.CaracteristicaNivelDto;
import org.bolsa.empleo.dto.OferenteMatchDto;

import java.util.List;

public interface OferenteService {
    void guardarHabilidades(Integer idOferente, List<CaracteristicaNivelDto> habilidades);

    void guardarCV(Integer idOferente, String cvPath);

    List<OferenteMatchDto> buscarCoincidencias(Integer idPuesto);
}

