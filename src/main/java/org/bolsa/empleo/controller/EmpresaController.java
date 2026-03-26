package org.bolsa.empleo.controller;

import org.bolsa.empleo.dto.CaracteristicaNivelDto;   // NUEVO
import org.bolsa.empleo.model.Caracteristica;          // NUEVO
import org.bolsa.empleo.repository.CaracteristicaRepository; // NUEVO
import java.util.ArrayList;
import java.util.List;
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
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import java.util.List;

@Controller
public class EmpresaController {
    private final PuestoService puestoService;
    private final OferenteService oferenteService;
    private final EmpresaRepository empresaRepository;
    private final CaracteristicaRepository caracteristicaRepository; // NUEVO

    public EmpresaController(PuestoService puestoService, OferenteService oferenteService, EmpresaRepository empresaRepository, CaracteristicaRepository caracteristicaRepository) {
        this.puestoService = puestoService;
        this.oferenteService = oferenteService;
        this.empresaRepository = empresaRepository;
        this.caracteristicaRepository = caracteristicaRepository;
    }

    @GetMapping("/empresa/dashboard")
    public String dashboard(HttpSession session) {
        validarRolEmpresa(session);
        return "empresa/dashboard";
    }

    @GetMapping("/empresa/mis-puestos")
    public String misPuestos(HttpSession session, Model model) {
        validarRolEmpresa(session);
        model.addAttribute("puestos", puestoService.listarPorEmpresa(obtenerIdEmpresa(session)));
        return "empresa/mis-puestos";
    }

    @GetMapping("/empresa/nuevo-puesto")
    public String crearPuesto(HttpSession session, Model model) {
        validarRolEmpresa(session);
        if (!model.containsAttribute("puesto")) {
            PuestoCreateDto dto = new PuestoCreateDto();
            List<CaracteristicaNivelDto> lista = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                lista.add(new CaracteristicaNivelDto());
            }
            dto.setCaracteristicasRequeridas(lista);

            model.addAttribute("puesto", dto);//new


        }
        model.addAttribute("caracteristicas", caracteristicaRepository.findAll());

        return "empresa/nuevo-puesto";
    }

    @PostMapping("/empresa/nuevo-puesto")
    public String guardarPuestoDesdeVista(
            @Valid @ModelAttribute("puesto") PuestoCreateDto dto,
            BindingResult bindingResult,
            HttpSession session
    ) {
        validarRolEmpresa(session);
        if (bindingResult.hasErrors()) {
            return "empresa/nuevo-puesto";
        }
        puestoService.crear(dto, obtenerIdEmpresa(session));
        return "redirect:/empresa/mis-puestos";
    }

    @GetMapping("/empresa/candidatos")
    public String candidatos(@RequestParam(required = false) Integer idPuesto,
                             HttpSession session, Model model) {
        validarRolEmpresa(session);
        Integer idEmpresa = obtenerIdEmpresa(session);

        // Todos los puestos de la empresa (para poder elegir)
        List<Puesto> puestos = puestoService.listarPorEmpresa(idEmpresa);
        model.addAttribute("puestos", puestos);

        if (idPuesto != null) {
            // Buscamos el puesto seleccionado
            Puesto puestoSeleccionado = puestos.stream()
                    .filter(p -> p.getId().equals(idPuesto))
                    .findFirst()
                    .orElse(null);

            if (puestoSeleccionado != null) {
                List<OferenteMatchDto> candidatos = oferenteService.buscarCoincidencias(idPuesto);
                model.addAttribute("candidatos", candidatos);
                model.addAttribute("puestoSeleccionado", puestoSeleccionado);
            }
        }
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

    @PostMapping("/empresa/puestos/{idPuesto}/desactivar")
    public String desactivarPuestoDesdeVista(@PathVariable Integer idPuesto, HttpSession session) {
        validarRolEmpresa(session);
        puestoService.desactivar(idPuesto);
        return "redirect:/empresa/mis-puestos";
    }
}