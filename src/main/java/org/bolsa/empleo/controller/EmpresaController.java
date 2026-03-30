package org.bolsa.empleo.controller;

import org.bolsa.empleo.dto.CaracteristicaNivelDto;
import org.bolsa.empleo.repository.CaracteristicaRepository;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.Valid;
import org.bolsa.empleo.dto.OferenteMatchDto;
import org.bolsa.empleo.dto.PuestoCreateDto;
import org.bolsa.empleo.model.Empresa;
import org.bolsa.empleo.model.Oferente;
import org.bolsa.empleo.model.OferenteCaracteristica;
import org.bolsa.empleo.model.Puesto;
import org.bolsa.empleo.repository.EmpresaRepository;
import org.bolsa.empleo.repository.OferenteCaracteristicaRepository;
import org.bolsa.empleo.repository.UsuarioRepository;
import org.bolsa.empleo.service.OferenteService;
import org.bolsa.empleo.service.PuestoService;
import org.bolsa.empleo.model.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.server.ResponseStatusException;

@Controller
@PreAuthorize("hasRole('EMPRESA')")
public class EmpresaController {

    private final PuestoService puestoService;
    private final OferenteService oferenteService;
    private final OferenteCaracteristicaRepository oferenteCaracteristicaRepository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CaracteristicaRepository caracteristicaRepository;

    public EmpresaController(PuestoService puestoService,
                             OferenteService oferenteService,
                             OferenteCaracteristicaRepository oferenteCaracteristicaRepository,
                             EmpresaRepository empresaRepository,
                             UsuarioRepository usuarioRepository,
                             CaracteristicaRepository caracteristicaRepository) {
        this.puestoService = puestoService;
        this.oferenteService = oferenteService;
        this.oferenteCaracteristicaRepository = oferenteCaracteristicaRepository;
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
        this.caracteristicaRepository = caracteristicaRepository;
    }


    @GetMapping("/empresa/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails principal, Model model) {
        Integer idEmpresa = resolverIdEmpresa(principal);
        List<Puesto> puestos = puestoService.listarPorEmpresa(idEmpresa);

        long activos   = puestos.stream().filter(p -> "ACTIVO".equals(p.getEstado())).count();
        long inactivos = puestos.stream().filter(p -> "INACTIVO".equals(p.getEstado())).count();

        model.addAttribute("totalPuestos", puestos.size());
        model.addAttribute("puestosActivos", activos);
        model.addAttribute("puestosInactivos", inactivos);
        return "empresa/dashboard";
    }


    @GetMapping("/empresa/mis-puestos")
    public String misPuestos(@AuthenticationPrincipal UserDetails principal, Model model) {
        model.addAttribute("puestos", puestoService.listarPorEmpresa(resolverIdEmpresa(principal)));
        return "empresa/mis-puestos";
    }


    @GetMapping("/empresa/nuevo-puesto")
    public String crearPuesto(Model model) {
        if (!model.containsAttribute("puesto")) {
            PuestoCreateDto dto = new PuestoCreateDto();
            List<CaracteristicaNivelDto> lista = new ArrayList<>();
            for (int i = 0; i < 5; i++) lista.add(new CaracteristicaNivelDto());
            dto.setCaracteristicasRequeridas(lista);
            model.addAttribute("puesto", dto);
        }
        model.addAttribute("caracteristicas", caracteristicaRepository.findAll());
        return "empresa/nuevo-puesto";
    }


    @PostMapping("/empresa/nuevo-puesto")
    public String guardarPuestoDesdeVista(
            @Valid @ModelAttribute("puesto") PuestoCreateDto dto,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails principal,
            Model model) {
        if (bindingResult.hasErrors()) {

            model.addAttribute("caracteristicas", caracteristicaRepository.findAll());
            return "empresa/nuevo-puesto";
        }
        puestoService.crear(dto, resolverIdEmpresa(principal));
        return "redirect:/empresa/mis-puestos";
    }


    @GetMapping("/empresa/candidatos")
    public String candidatos(@RequestParam(required = false) Integer idPuesto,
                             @AuthenticationPrincipal UserDetails principal, Model model) {
        Integer idEmpresa = resolverIdEmpresa(principal);
        List<Puesto> puestos = puestoService.listarPorEmpresa(idEmpresa);
        model.addAttribute("puestos", puestos);

        if (idPuesto != null) {
            Puesto puestoSeleccionado = puestos.stream()
                    .filter(p -> p.getId().equals(idPuesto))
                    .findFirst().orElse(null);
            if (puestoSeleccionado != null) {
                model.addAttribute("candidatos", oferenteService.buscarCoincidencias(idPuesto));
                model.addAttribute("puestoSeleccionado", puestoSeleccionado);
            }
        }
        return "empresa/candidatos";
    }


    @GetMapping("/empresa/detalle-candidato")
    public String detalleCandidato(@RequestParam Integer idOferente,
                                   @RequestParam(required = false) Integer idPuesto,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        Oferente oferente;
        try {
            oferente = oferenteService.obtenerPorId(idOferente);
        } catch (ResponseStatusException ex) {
            redirectAttributes.addFlashAttribute("error", "No se encontro el candidato solicitado.");
            if (idPuesto != null) {
                return "redirect:/empresa/candidatos?idPuesto=" + idPuesto;
            }
            return "redirect:/empresa/candidatos";
        }

        List<OferenteCaracteristica> habilidades =
                oferenteCaracteristicaRepository.findDetalleByOferenteId(idOferente);

        model.addAttribute("oferente", oferente);
        model.addAttribute("habilidades", habilidades);
        model.addAttribute("idPuesto", idPuesto);
        return "empresa/detalle-candidato";
    }


    @PostMapping("/empresa/puestos/{idPuesto}/desactivar")
    public String desactivarPuestoDesdeVista(@PathVariable Integer idPuesto) {
        puestoService.desactivar(idPuesto);
        return "redirect:/empresa/mis-puestos";
    }


    @PostMapping("/api/empresa/puestos")
    @ResponseBody
    public ResponseEntity<Puesto> guardarPuesto(
            @Valid @RequestBody PuestoCreateDto dto,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(puestoService.crear(dto, resolverIdEmpresa(principal)));
    }

    @GetMapping("/api/empresa/puestos/{idPuesto}/candidatos")
    @ResponseBody
    public ResponseEntity<List<OferenteMatchDto>> buscarCandidatos(@PathVariable Integer idPuesto) {
        return ResponseEntity.ok(oferenteService.buscarCoincidencias(idPuesto));
    }

    @PatchMapping("/api/empresa/puestos/{idPuesto}/desactivar")
    @ResponseBody
    public ResponseEntity<Void> desactivarPuesto(@PathVariable Integer idPuesto) {
        puestoService.desactivar(idPuesto);
        return ResponseEntity.noContent().build();
    }


    private Integer resolverIdEmpresa(UserDetails principal) {
        Usuario usuario = usuarioRepository
                .findByCorreoIgnoreCaseOrIdentificacion(
                        principal.getUsername(), principal.getUsername())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "Usuario autenticado no encontrado"));

        Empresa empresa = empresaRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "Empresa no vinculada al usuario autenticado"));
        return empresa.getId();
    }
}