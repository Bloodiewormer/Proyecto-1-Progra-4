package org.bolsa.empleo.controller;

import jakarta.validation.Valid;
import org.bolsa.empleo.dto.CaracteristicaNivelDto;
import org.bolsa.empleo.model.Oferente;
import org.bolsa.empleo.repository.OferenteRepository;
import org.bolsa.empleo.service.OferenteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Controller
@PreAuthorize("hasRole('OFERENTE')")
public class OferenteController {

    private final OferenteService   oferenteService;
    private final OferenteRepository oferenteRepository;

    public OferenteController(OferenteService oferenteService,
                              OferenteRepository oferenteRepository) {
        this.oferenteService    = oferenteService;
        this.oferenteRepository = oferenteRepository;
    }

    @GetMapping("/oferente/dashboard")
    public String dashboard() {
        return "oferente/dashboard";
    }

    @GetMapping("/oferente/habilidades")
    public String gestionarHabilidades() {
        return "oferente/habilidades";
    }

    @GetMapping("/oferente/subir-cv")
    public String vistaSubirCv() {
        return "oferente/subir-cv";
    }

    @PostMapping("/api/oferente/habilidades")
    @ResponseBody
    public ResponseEntity<Void> guardarHabilidades(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody List<CaracteristicaNivelDto> habilidades) {

        oferenteService.guardarHabilidades(resolverIdOferente(principal), habilidades);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/api/oferente/cv")
    @ResponseBody
    public ResponseEntity<Void> subirCV(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam String cvPath) {

        oferenteService.guardarCV(resolverIdOferente(principal), cvPath);
        return ResponseEntity.noContent().build();
    }

    // ─────────────────────────────────────────────
    // Helper: obtener idOferente desde el principal autenticado
    // ─────────────────────────────────────────────
    private Integer resolverIdOferente(UserDetails principal) {
        String credencial = principal.getUsername();   // correo del oferente

        Oferente oferente = oferenteRepository.findByUsuarioCorreoIgnoreCase(credencial)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "Oferente no vinculado al usuario autenticado"));
        return oferente.getId();
    }
}