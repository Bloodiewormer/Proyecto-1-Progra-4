package org.bolsa.empleo.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.bolsa.empleo.dto.CaracteristicaNivelDto;
import org.bolsa.empleo.model.Oferente;
import org.bolsa.empleo.repository.OferenteRepository;
import org.bolsa.empleo.service.OferenteService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import java.util.List;

@Controller
public class OferenteController {
    private final OferenteService oferenteService;
    private final OferenteRepository oferenteRepository;

    public OferenteController(OferenteService oferenteService, OferenteRepository oferenteRepository) {
        this.oferenteService = oferenteService;
        this.oferenteRepository = oferenteRepository;
    }

    @GetMapping("/oferente/dashboard")
    public String dashboard(HttpSession session) {
        validarRolOferente(session);
        return "oferente/dashboard";
    }

    @GetMapping("/oferente/habilidades")
    public String gestionarHabilidades(HttpSession session) {
        validarRolOferente(session);
        return "oferente/habilidades";
    }

    @GetMapping("/oferente/subir-cv")
    public String vistaSubirCv(HttpSession session) {
        validarRolOferente(session);
        return "oferente/subir-cv";
    }

    @PostMapping("/api/oferente/habilidades")
    @ResponseBody
    public ResponseEntity<Void> guardarHabilidades(
            HttpSession session,
            @Valid @RequestBody List<CaracteristicaNivelDto> habilidades
    ) {
        validarRolOferente(session);
        oferenteService.guardarHabilidades(obtenerIdOferente(session), habilidades);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/api/oferente/cv")
    @ResponseBody
    public ResponseEntity<Void> subirCV(HttpSession session, @RequestParam String cvPath) {
        validarRolOferente(session);
        oferenteService.guardarCV(obtenerIdOferente(session), cvPath);
        return ResponseEntity.noContent().build();
    }

    private void validarRolOferente(HttpSession session) {
        Object rol = session.getAttribute("rol");
        if (rol == null || !"OFERENTE".equalsIgnoreCase(rol.toString())) {
            throw new ResponseStatusException(FORBIDDEN, "Acceso restringido a oferentes");
        }
    }

    private Integer obtenerIdOferente(HttpSession session) {
        Integer idUsuario = (Integer) session.getAttribute("usuarioId");
        if (idUsuario == null) {
            throw new ResponseStatusException(FORBIDDEN, "Sesion invalida");
        }

        Oferente oferente = oferenteRepository.findByUsuarioId(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(FORBIDDEN, "Oferente no vinculado al usuario"));
        return oferente.getId();
    }
}
