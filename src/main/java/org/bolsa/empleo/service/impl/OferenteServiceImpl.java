package org.bolsa.empleo.service.impl;

import org.bolsa.empleo.dto.CaracteristicaNivelDto;
import org.bolsa.empleo.dto.OferenteMatchDto;
import org.bolsa.empleo.model.Oferente;
import org.bolsa.empleo.model.OferenteCaracteristica;
import org.bolsa.empleo.model.OferenteCaracteristicaId;
import org.bolsa.empleo.model.PuestoCaracteristica;
import org.bolsa.empleo.repository.CaracteristicaRepository;
import org.bolsa.empleo.repository.OferenteCaracteristicaRepository;
import org.bolsa.empleo.repository.OferenteRepository;
import org.bolsa.empleo.repository.PuestoCaracteristicaRepository;
import org.bolsa.empleo.service.OferenteService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OferenteServiceImpl implements OferenteService {
    private final OferenteRepository oferenteRepository;
    private final CaracteristicaRepository caracteristicaRepository;
    private final OferenteCaracteristicaRepository oferenteCaracteristicaRepository;
    private final PuestoCaracteristicaRepository puestoCaracteristicaRepository;

    public OferenteServiceImpl(
            OferenteRepository oferenteRepository,
            CaracteristicaRepository caracteristicaRepository,
            OferenteCaracteristicaRepository oferenteCaracteristicaRepository,
            PuestoCaracteristicaRepository puestoCaracteristicaRepository
    ) {
        this.oferenteRepository = oferenteRepository;
        this.caracteristicaRepository = caracteristicaRepository;
        this.oferenteCaracteristicaRepository = oferenteCaracteristicaRepository;
        this.puestoCaracteristicaRepository = puestoCaracteristicaRepository;
    }

    @Override
    @Transactional
    public void guardarHabilidades(Integer idOferente, List<CaracteristicaNivelDto> habilidades) {
        Oferente oferente = oferenteRepository.findById(idOferente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Oferente no encontrado"));

        oferenteCaracteristicaRepository.deleteByOferente_Id(idOferente);

        for (CaracteristicaNivelDto habilidad : habilidades) {
            if (habilidad.getIdCaracteristica() == null || habilidad.getNivel() == null) {
                continue;
            }

            OferenteCaracteristica relacion = new OferenteCaracteristica();
            OferenteCaracteristicaId id = new OferenteCaracteristicaId();
            id.setIdOferente(idOferente);
            id.setIdCaracteristica(habilidad.getIdCaracteristica());
            relacion.setId(id);
            relacion.setOferente(oferente);
            relacion.setCaracteristica(caracteristicaRepository.findById(habilidad.getIdCaracteristica())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Caracteristica no encontrada")));
            relacion.setNivel(habilidad.getNivel());
            oferenteCaracteristicaRepository.save(relacion);
        }
    }

    @Override
    @Transactional
    public void guardarCV(Integer idOferente, String cvPath) {
        Oferente oferente = oferenteRepository.findById(idOferente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Oferente no encontrado"));
        oferente.setCvPath(cvPath);
        oferenteRepository.save(oferente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OferenteMatchDto> buscarCoincidencias(Integer idPuesto) {
        List<PuestoCaracteristica> requeridas = puestoCaracteristicaRepository.findByPuesto_Id(idPuesto);
        if (requeridas.isEmpty()) {
            return List.of();
        }

        List<OferenteMatchDto> resultados = new ArrayList<>();
        List<Oferente> oferentes = oferenteRepository.findAll();

        for (Oferente oferente : oferentes) {
            Map<Integer, Integer> nivelesPorCaracteristica = new HashMap<>();
            oferenteCaracteristicaRepository.findByOferente_Id(oferente.getId())
                    .forEach(item -> nivelesPorCaracteristica.put(item.getCaracteristica().getId(), item.getNivel()));

            int score = 0;
            for (PuestoCaracteristica requerida : requeridas) {
                Integer nivelOferente = nivelesPorCaracteristica.get(requerida.getCaracteristica().getId());
                if (nivelOferente != null && nivelOferente >= requerida.getNivelRequerido()) {
                    score++;
                }
            }

            if (score > 0) {
                String nombreCompleto = oferente.getNombre() + " " + oferente.getApellido();
                resultados.add(new OferenteMatchDto(oferente.getId(), nombreCompleto.trim(), score));
            }
        }

        resultados.sort((a, b) -> Integer.compare(b.getScoreCoincidencia(), a.getScoreCoincidencia()));
        return resultados;
    }
}
