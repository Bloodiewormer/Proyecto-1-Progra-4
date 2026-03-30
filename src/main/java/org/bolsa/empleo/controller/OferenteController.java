package org.bolsa.empleo.controller;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.bolsa.empleo.model.Oferente;
import org.bolsa.empleo.model.Usuario;
import org.bolsa.empleo.repository.CaracteristicaRepository;
import org.bolsa.empleo.repository.OferenteCaracteristicaRepository;
import org.bolsa.empleo.dto.CaracteristicaNivelDto;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.Valid;
import org.bolsa.empleo.repository.OferenteRepository;
import org.bolsa.empleo.repository.UsuarioRepository;
import org.bolsa.empleo.service.OferenteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;

@Controller
@PreAuthorize("hasRole('OFERENTE')")
public class OferenteController {

    private final OferenteService oferenteService;
    private final OferenteRepository oferenteRepository;
    private final UsuarioRepository usuarioRepository;
    private final CaracteristicaRepository caracteristicaRepository;
    private final OferenteCaracteristicaRepository oferenteCaracteristicaRepository;

    public OferenteController(OferenteService oferenteService,
                              OferenteRepository oferenteRepository,
                              UsuarioRepository usuarioRepository,
                              CaracteristicaRepository caracteristicaRepository,
                              OferenteCaracteristicaRepository oferenteCaracteristicaRepository) {
        this.oferenteService = oferenteService;
        this.oferenteRepository = oferenteRepository;
        this.usuarioRepository = usuarioRepository;
        this.caracteristicaRepository = caracteristicaRepository;
        this.oferenteCaracteristicaRepository = oferenteCaracteristicaRepository;
    }


    @GetMapping("/oferente/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails principal, Model model) {
        Integer idOferente = resolverIdOferente(principal);
        Oferente oferente = oferenteRepository.findById(idOferente).orElseThrow();

        long totalHabilidades = oferenteCaracteristicaRepository.findByOferente_Id(idOferente).size();
        boolean tieneCV = oferente.getCvPath() != null && !oferente.getCvPath().isBlank();

        model.addAttribute("nombreOferente", oferente.getNombre() + " " + oferente.getApellido());
        model.addAttribute("totalHabilidades", totalHabilidades);
        model.addAttribute("tieneCV", tieneCV);
        model.addAttribute("cvPath", oferente.getCvPath());
        return "oferente/dashboard";
    }

    @GetMapping("/oferente/habilidades")
    public String gestionarHabilidades(@AuthenticationPrincipal UserDetails principal, Model model) {
        Integer idOferente = resolverIdOferente(principal);


        List<CaracteristicaNivelDto> habilidadesActuales = oferenteCaracteristicaRepository
                .findByOferente_Id(idOferente)
                .stream()
                .map(oc -> {
                    CaracteristicaNivelDto dto = new CaracteristicaNivelDto();
                    dto.setIdCaracteristica(oc.getCaracteristica().getId());
                    dto.setNivel(oc.getNivel());
                    return dto;
                }).collect(java.util.stream.Collectors.toList());


        while (habilidadesActuales.size() < 5) {
            habilidadesActuales.add(new CaracteristicaNivelDto());
        }

        model.addAttribute("habilidades", habilidadesActuales);
        model.addAttribute("caracteristicas", caracteristicaRepository.findAll());
        return "oferente/habilidades";
    }

    @PostMapping("/oferente/habilidades")
    public String guardarHabilidadesDesdeVista(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(value = "idCaracteristica", required = false) List<Integer> ids,
            @RequestParam(value = "nivel", required = false) List<Integer> niveles) {

        Integer idOferente = resolverIdOferente(principal);
        List<CaracteristicaNivelDto> habilidades = new ArrayList<>();

        if (ids != null) {
            for (int i = 0; i < ids.size(); i++) {
                if (ids.get(i) != null && niveles != null && i < niveles.size() && niveles.get(i) != null) {
                    CaracteristicaNivelDto dto = new CaracteristicaNivelDto();
                    dto.setIdCaracteristica(ids.get(i));
                    dto.setNivel(niveles.get(i));
                    habilidades.add(dto);
                }
            }
        }
        oferenteService.guardarHabilidades(idOferente, habilidades);
        return "redirect:/oferente/habilidades";
    }

    @GetMapping("/oferente/subir-cv")
    public String vistaSubirCv(@AuthenticationPrincipal UserDetails principal, Model model) {
        Integer idOferente = resolverIdOferente(principal);
        Oferente oferente = oferenteRepository.findById(idOferente).orElse(null);
        model.addAttribute("cvPath", oferente != null ? oferente.getCvPath() : null);
        return "oferente/subir-cv";
    }

    @PostMapping("/oferente/subir-cv")
    public String subirCV(@AuthenticationPrincipal UserDetails principal,
                          @RequestParam("cv") MultipartFile archivo) {
        Integer idOferente = resolverIdOferente(principal);
        if (archivo.isEmpty()) return "redirect:/oferente/subir-cv";

        try {
            Path uploadDir = Paths.get("src/main/resources/static/uploads/cv");
            if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);


            String original = archivo.getOriginalFilename() != null ? archivo.getOriginalFilename() : "cv.pdf";
            String cleanName = original.replaceAll("[^a-zA-Z0-9._-]", "_");
            if (!cleanName.toLowerCase().endsWith(".pdf")) {
                cleanName += ".pdf";
            }

            String filename = idOferente + "_" + System.currentTimeMillis() + "_" + cleanName;

            Files.copy(archivo.getInputStream(), uploadDir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);

            String cvPath = "/uploads/cv/" + filename;
            oferenteService.guardarCV(idOferente, cvPath);

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
    public ResponseEntity<Void> subirCV(@AuthenticationPrincipal UserDetails principal,
                                        @RequestParam String cvPath) {
        oferenteService.guardarCV(resolverIdOferente(principal), cvPath);
        return ResponseEntity.noContent().build();
    }


    private Integer resolverIdOferente(UserDetails principal) {
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCaseOrIdentificacion(
                        principal.getUsername(), principal.getUsername())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "Usuario autenticado no encontrado"));

        return oferenteRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "Oferente no vinculado al usuario autenticado"))
                .getId();
    }
}