package org.bolsa.empleo.controller;

import jakarta.validation.Valid;
import org.bolsa.empleo.dto.OferenteMatchDto;
import org.bolsa.empleo.dto.PuestoCreateDto;
import org.bolsa.empleo.model.Empresa;
import org.bolsa.empleo.model.Puesto;
import org.bolsa.empleo.repository.EmpresaRepository;
import org.bolsa.empleo.service.OferenteService;
import org.bolsa.empleo.service.PuestoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Controller
@PreAuthorize("hasRole('EMPRESA')")
public class EmpresaController {

    private final PuestoService      puestoService;
    private final OferenteService    oferenteService;
    private final EmpresaRepository  empresaRepository;

    public EmpresaController(PuestoService puestoService,
                             OferenteService oferenteService,
                             EmpresaRepository empresaRepository) {
        this.puestoService     = puestoService;
        this.oferenteService   = oferenteService;
        this.empresaRepository = empresaRepository;
    }

    @GetMapping("/empresa/dashboard")
    public String dashboard() {
        return "empresa/dashboard";
    }

    @GetMapping("/empresa/mis-puestos")
    public String misPuestos(@AuthenticationPrincipal UserDetails principal, Model model) {
        model.addAttribute("puestos",
                puestoService.listarPorEmpresa(resolverIdEmpresa(principal)));
        return "empresa/mis-puestos";
    }

    @GetMapping("/empresa/nuevo-puesto")
    public String crearPuesto(Model model) {
        if (!model.containsAttribute("puesto")) {
            model.addAttribute("puesto", new PuestoCreateDto());
        }
        return "empresa/nuevo-puesto";
    }

    @PostMapping("/empresa/nuevo-puesto")
    public String guardarPuestoDesdeVista(
            @Valid @ModelAttribute("puesto") PuestoCreateDto dto,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails principal) {

        if (bindingResult.hasErrors()) {
            return "empresa/nuevo-puesto";
        }
        puestoService.crear(dto, resolverIdEmpresa(principal));
        return "redirect:/empresa/mis-puestos";
    }

    @GetMapping("/empresa/candidatos")
    public String candidatos() {
        return "empresa/candidatos";
    }

    @GetMapping("/empresa/detalle-candidato")
    public String detalleCandidato() {
        return "empresa/detalle-candidato";
    }

    // ── API REST ──

    @PostMapping("/api/empresa/puestos")
    @ResponseBody
    public ResponseEntity<Puesto> guardarPuesto(
            @Valid @RequestBody PuestoCreateDto dto,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(puestoService.crear(dto, resolverIdEmpresa(principal)));
    }

    @GetMapping("/api/empresa/puestos/{idPuesto}/candidatos")
    @ResponseBody
    public ResponseEntity<List<OferenteMatchDto>> buscarCandidatos(
            @PathVariable Integer idPuesto) {
        return ResponseEntity.ok(oferenteService.buscarCoincidencias(idPuesto));
    }

    @PatchMapping("/api/empresa/puestos/{idPuesto}/desactivar")
    @ResponseBody
    public ResponseEntity<Void> desactivarPuesto(@PathVariable Integer idPuesto) {
        puestoService.desactivar(idPuesto);
        return ResponseEntity.noContent().build();
    }

    // ─────────────────────────────────────────────
    // Helper: obtener idEmpresa desde el principal autenticado
    // ─────────────────────────────────────────────
    private Integer resolverIdEmpresa(UserDetails principal) {
        // principal.getUsername() devuelve la credencial con la que hizo login (correo)
        String credencial = principal.getUsername();

        Empresa empresa = empresaRepository.findByUsuarioCorreoIgnoreCase(credencial)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "Empresa no vinculada al usuario autenticado"));
        return empresa.getId();
    }
}