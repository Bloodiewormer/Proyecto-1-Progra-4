package org.bolsa.empleo.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.bolsa.empleo.dto.OferenteMatchDto;
import org.bolsa.empleo.dto.PuestoCreateDto;
import org.bolsa.empleo.model.Empresa;
import org.bolsa.empleo.model.Puesto;
import org.bolsa.empleo.repository.EmpresaRepository;
import org.bolsa.empleo.service.OferenteService;
import org.bolsa.empleo.service.PuestoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import java.util.List;

@Controller
public class EmpresaController {
    private final PuestoService puestoService;
    private final OferenteService oferenteService;
    private final EmpresaRepository empresaRepository;

    public EmpresaController(PuestoService puestoService, OferenteService oferenteService, EmpresaRepository empresaRepository) {
        this.puestoService = puestoService;
        this.oferenteService = oferenteService;
        this.empresaRepository = empresaRepository;
    }

    @GetMapping("/empresa/dashboard")
    public String dashboard(HttpSession session) {
        validarRolEmpresa(session);
        return "empresa/dashboard";
    }

    @GetMapping("/empresa/mis-puestos")
    public String misPuestos(HttpSession session) {
        validarRolEmpresa(session);
        return "empresa/mis-puestos";
    }

    @GetMapping("/empresa/nuevo-puesto")
    public String crearPuesto(HttpSession session) {
        validarRolEmpresa(session);
        return "empresa/nuevo-puesto";
    }

    @GetMapping("/empresa/candidatos")
    public String candidatos(HttpSession session) {
        validarRolEmpresa(session);
        return "empresa/candidatos";
    }

    @GetMapping("/empresa/detalle-candidato")
    public String detalleCandidato(HttpSession session) {
        validarRolEmpresa(session);
        return "empresa/detalle-candidato";
    }

    @PostMapping("/api/empresa/puestos")
    @ResponseBody
    public ResponseEntity<Puesto> guardarPuesto(@Valid @RequestBody PuestoCreateDto dto, HttpSession session) {
        validarRolEmpresa(session);
        return ResponseEntity.ok(puestoService.crear(dto, obtenerIdEmpresa(session)));
    }

    @GetMapping("/api/empresa/puestos/{idPuesto}/candidatos")
    @ResponseBody
    public ResponseEntity<List<OferenteMatchDto>> buscarCandidatos(@PathVariable Integer idPuesto, HttpSession session) {
        validarRolEmpresa(session);
        return ResponseEntity.ok(oferenteService.buscarCoincidencias(idPuesto));
    }

    @PatchMapping("/api/empresa/puestos/{idPuesto}/desactivar")
    @ResponseBody
    public ResponseEntity<Void> desactivarPuesto(@PathVariable Integer idPuesto, HttpSession session) {
        validarRolEmpresa(session);
        puestoService.desactivar(idPuesto);
        return ResponseEntity.noContent().build();
    }

    private void validarRolEmpresa(HttpSession session) {
        Object rol = session.getAttribute("rol");
        if (rol == null || !"EMPRESA".equalsIgnoreCase(rol.toString())) {
            throw new ResponseStatusException(FORBIDDEN, "Acceso restringido a empresas");
        }
    }

    private Integer obtenerIdEmpresa(HttpSession session) {
        Integer idUsuario = (Integer) session.getAttribute("usuarioId");
        if (idUsuario == null) {
            throw new ResponseStatusException(FORBIDDEN, "Sesion invalida");
        }

        Empresa empresa = empresaRepository.findByUsuarioId(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(FORBIDDEN, "Empresa no vinculada al usuario"));
        return empresa.getId();
    }
}