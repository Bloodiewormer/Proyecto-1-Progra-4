package org.bolsa.empleo.service.impl;

import org.bolsa.empleo.dto.PuestoCreateDto;
import org.bolsa.empleo.dto.PuestoFiltroDto;
import org.bolsa.empleo.model.Empresa;
import org.bolsa.empleo.model.EstadoPuesto;
import org.bolsa.empleo.model.Puesto;
import org.bolsa.empleo.model.TipoPublicacion;
import org.bolsa.empleo.repository.EmpresaRepository;
import org.bolsa.empleo.repository.PuestoRepository;
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

    public PuestoServiceImpl(PuestoRepository puestoRepository, EmpresaRepository empresaRepository) {
        this.puestoRepository = puestoRepository;
        this.empresaRepository = empresaRepository;
    }

    @Override
    public List<Puesto> listarRecientes() {
        return puestoRepository.findByEstadoOrderByFechaPublicacionDesc(EstadoPuesto.ACTIVO.name(), PageRequest.of(0, 10));
    }

    @Override
    public List<Puesto> listarPorEmpresa(Integer idEmpresa) {
        return puestoRepository.findByEmpresa_IdOrderByFechaPublicacionDesc(idEmpresa);
    }

    @Override
    public List<Puesto> buscarPorFiltros(PuestoFiltroDto dto) {
        return puestoRepository.buscarActivosPorFiltro(dto.getPalabraClave(), dto.getSalarioMin(), dto.getSalarioMax());
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
        puesto.setTipoPublicacion(TipoPublicacion.PUBLICO.name());
        puesto.setEstado(EstadoPuesto.ACTIVO.name());
        puesto.setFechaPublicacion(Instant.now());

        return puestoRepository.save(puesto);
    }

    @Override
    @Transactional
    public void desactivar(Integer idPuesto) {
        Puesto puesto = puestoRepository.findById(idPuesto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Puesto no encontrado"));
        puesto.setEstado(EstadoPuesto.INACTIVO.name());
        puestoRepository.save(puesto);
    }
}

