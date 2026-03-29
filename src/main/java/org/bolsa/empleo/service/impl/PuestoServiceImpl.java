package org.bolsa.empleo.service.impl;

import org.bolsa.empleo.dto.CaracteristicaNivelDto;
import org.bolsa.empleo.dto.PuestoCreateDto;
import org.bolsa.empleo.dto.PuestoFiltroDto;
import org.bolsa.empleo.model.*;
import org.bolsa.empleo.repository.*;
import org.bolsa.empleo.service.PuestoService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class PuestoServiceImpl implements PuestoService {

    private final PuestoRepository puestoRepository;
    private final EmpresaRepository empresaRepository;
    private final PuestoCaracteristicaRepository puestoCaracteristicaRepository;
    private final CaracteristicaRepository caracteristicaRepository;

    public PuestoServiceImpl(PuestoRepository puestoRepository,
                             EmpresaRepository empresaRepository,
                             PuestoCaracteristicaRepository puestoCaracteristicaRepository,
                             CaracteristicaRepository caracteristicaRepository) {
        this.puestoRepository = puestoRepository;
        this.empresaRepository = empresaRepository;
        this.puestoCaracteristicaRepository = puestoCaracteristicaRepository;
        this.caracteristicaRepository = caracteristicaRepository;
    }

    // Solo puestos PUBLICOS y ACTIVOS para la página de inicio
    @Override
    public List<Puesto> listarRecientes() {
        return puestoRepository.findRecientesPublicos(PageRequest.of(0, 5));
    }

    @Override
    public List<Puesto> listarPorEmpresa(Integer idEmpresa) {
        return puestoRepository.findByEmpresa_IdOrderByFechaPublicacionDesc(idEmpresa);
    }

    // Búsqueda pública: solo PUBLICOS. Si hay características seleccionadas las prioriza.
    @Override
    public List<Puesto> buscarPorFiltros(PuestoFiltroDto dto) {
        List<Integer> ids = dto.getIdsCaracteristicas();
        if (ids != null && !ids.isEmpty()) {
            return puestoRepository.buscarPublicosPorCaracteristicas(
                    ids,
                    dto.getPalabraClave(),
                    dto.getSalarioMin(),
                    dto.getSalarioMax());
        }
        return puestoRepository.buscarPublicosActivosPorFiltro(
                dto.getPalabraClave(),
                dto.getSalarioMin(),
                dto.getSalarioMax());
    }

    @Override
    @Transactional
    public Puesto crear(PuestoCreateDto dto, Integer idEmpresa) {
        Empresa empresa = empresaRepository.findById(idEmpresa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada"));

        Puesto puesto = new Puesto();
        puesto.setEmpresa(empresa);
        puesto.setTitulo(dto.getTitulo());
        puesto.setDescripcion(dto.getDescripcion());
        puesto.setSalario(dto.getSalario());
        puesto.setTipoPublicacion(dto.getTipoPublicacion().toUpperCase());
        puesto.setEstado(EstadoPuesto.ACTIVO.name());
        puesto.setFechaPublicacion(Instant.now());

        puesto = puestoRepository.save(puesto);

        if (dto.getCaracteristicasRequeridas() != null) {
            for (CaracteristicaNivelDto carDto : dto.getCaracteristicasRequeridas()) {
                if (carDto.getIdCaracteristica() == null || carDto.getNivel() == null) {
                    continue;
                }
                Caracteristica caracteristica = caracteristicaRepository
                        .findById(carDto.getIdCaracteristica())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Característica con ID " + carDto.getIdCaracteristica() + " no encontrada"));

                PuestoCaracteristica pc = new PuestoCaracteristica();
                PuestoCaracteristicaId id = new PuestoCaracteristicaId();
                id.setIdPuesto(puesto.getId());
                id.setIdCaracteristica(carDto.getIdCaracteristica());
                pc.setId(id);
                pc.setPuesto(puesto);
                pc.setCaracteristica(caracteristica);
                pc.setNivelRequerido(carDto.getNivel());

                puestoCaracteristicaRepository.save(pc);
            }
        }

        return puesto;
    }

    @Override
    @Transactional
    public void desactivar(Integer idPuesto) {
        Puesto puesto = puestoRepository.findById(idPuesto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Puesto no encontrado"));
        puesto.setEstado(EstadoPuesto.INACTIVO.name());
        puestoRepository.save(puesto);
    }

    @Override
    public List<Puesto> obtenerTodosLosPuestos() {
        return puestoRepository.findAllByOrderByFechaPublicacionDesc();
    }
}