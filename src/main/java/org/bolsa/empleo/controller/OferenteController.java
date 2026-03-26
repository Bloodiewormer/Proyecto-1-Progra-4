package org.bolsa.empleo.controller;


import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.bolsa.empleo.model.Oferente;
import org.bolsa.empleo.repository.CaracteristicaRepository;
import org.bolsa.empleo.dto.CaracteristicaNivelDto;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.bolsa.empleo.repository.OferenteRepository;
import org.bolsa.empleo.service.OferenteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Controller
@PreAuthorize("hasRole('OFERENTE')")
public class OferenteController {

    private final OferenteService   oferenteService;
    private final OferenteRepository oferenteRepository;
    private final CaracteristicaRepository caracteristicaRepository; // NUEVO

    public OferenteController(OferenteService oferenteService, OferenteRepository oferenteRepository, CaracteristicaRepository caracteristicaRepository) {
        this.oferenteService = oferenteService;
        this.oferenteRepository = oferenteRepository;
        this.caracteristicaRepository = caracteristicaRepository;
    }

    @GetMapping("/oferente/dashboard")
    public String dashboard() {
        return "oferente/dashboard";
    }

    @GetMapping("/oferente/habilidades")
    public String gestionarHabilidades(HttpSession session, Model model) {
        validarRolOferente(session);

        if (!model.containsAttribute("habilidades")) {
            List<CaracteristicaNivelDto> lista = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                lista.add(new CaracteristicaNivelDto());
            }
            model.addAttribute("habilidades", lista);
        }

        model.addAttribute("caracteristicas", caracteristicaRepository.findAll());
        return "oferente/habilidades";
    }

    @GetMapping("/oferente/subir-cv")
    public String vistaSubirCv(HttpSession session, Model model) {
        validarRolOferente(session);
        Integer idOferente = obtenerIdOferente(session);
        Oferente oferente = oferenteRepository.findById(idOferente).orElse(null);
        model.addAttribute("cvPath", oferente != null ? oferente.getCvPath() : null);
        return "oferente/subir-cv";
    }

    @PostMapping("/oferente/subir-cv")
    public String subirCV(HttpSession session,
                          @RequestParam("cv") MultipartFile archivo) {
        validarRolOferente(session);
        Integer idOferente = obtenerIdOferente(session);

        if (archivo.isEmpty()) {
            // puedes agregar mensaje de error si quieres
            return "redirect:/oferente/subir-cv";
        }
        try {
            // Crear directorio si no existe
            Path uploadDir = Paths.get("src/main/resources/static/uploads/cv");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String filename = idOferente + "_" + System.currentTimeMillis() + "_" + archivo.getOriginalFilename();
            Path filePath = uploadDir.resolve(filename);
            Files.copy(archivo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String cvUrl = "/uploads/cv/" + filename;
            oferenteService.guardarCV(idOferente, cvUrl);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/oferente/subir-cv";
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
