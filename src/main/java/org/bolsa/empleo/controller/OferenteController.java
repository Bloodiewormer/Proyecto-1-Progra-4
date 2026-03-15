package org.bolsa.empleo.controller;

import jakarta.validation.Valid;
import org.bolsa.empleo.dto.CaracteristicaNivelDto;
import org.bolsa.empleo.service.OferenteService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Controller
@RequestMapping("/oferente")
public class OferenteController {
    private final OferenteService oferenteService;

    public OferenteController(OferenteService oferenteService) {
        this.oferenteService = oferenteService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<String> dashboard() {
        return ResponseEntity.ok("Dashboard de oferente");
    }

    @GetMapping("/habilidades")
    public ResponseEntity<String> gestionarHabilidades() {
        return ResponseEntity.ok("Gestion de habilidades");
    }

    @PostMapping("/{idOferente}/habilidades")
    public ResponseEntity<Void> guardarHabilidades(
            @PathVariable Integer idOferente,
            @Valid @RequestBody List<CaracteristicaNivelDto> habilidades
    ) {
        oferenteService.guardarHabilidades(idOferente, habilidades);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{idOferente}/cv")
    public ResponseEntity<Void> subirCV(@PathVariable Integer idOferente, @RequestParam String cvPath) {
        oferenteService.guardarCV(idOferente, cvPath);
        return ResponseEntity.noContent().build();
    }
}
