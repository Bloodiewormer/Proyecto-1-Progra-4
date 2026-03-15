package org.bolsa.empleo.controller;

import jakarta.validation.Valid;
import org.bolsa.empleo.dto.PuestoFiltroDto;
import org.bolsa.empleo.model.Puesto;
import org.bolsa.empleo.service.PuestoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/publico")
public class PublicoController {
    private final PuestoService puestoService;

    public PublicoController(PuestoService puestoService) {
        this.puestoService = puestoService;
    }

    @GetMapping("/puestos/recientes")
    public ResponseEntity<List<Puesto>> listarRecientes() {
        return ResponseEntity.ok(puestoService.listarRecientes());
    }

    @PostMapping("/puestos/buscar")
    public ResponseEntity<List<Puesto>> buscarPuestos(@Valid @RequestBody PuestoFiltroDto dto) {
        return ResponseEntity.ok(puestoService.buscarPorFiltros(dto));
    }
}

