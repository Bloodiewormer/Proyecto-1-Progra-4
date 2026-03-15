package org.bolsa.empleo.controller;

import jakarta.validation.Valid;
import org.bolsa.empleo.dto.OferenteMatchDto;
import org.bolsa.empleo.dto.PuestoCreateDto;
import org.bolsa.empleo.model.Puesto;
import org.bolsa.empleo.service.OferenteService;
import org.bolsa.empleo.service.PuestoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/empresa")
public class EmpresaController {
    private final PuestoService puestoService;
    private final OferenteService oferenteService;

    public EmpresaController(PuestoService puestoService, OferenteService oferenteService) {
        this.puestoService = puestoService;
        this.oferenteService = oferenteService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<String> dashboard() {
        return ResponseEntity.ok("Dashboard de empresa");
    }

    @GetMapping("/puestos/nuevo")
    public ResponseEntity<String> crearPuesto() {
        return ResponseEntity.ok("Formulario de creacion de puesto");
    }

    @PostMapping("/puestos")
    public ResponseEntity<Puesto> guardarPuesto(@Valid @RequestBody PuestoCreateDto dto, @RequestParam Integer idEmpresa) {
        return ResponseEntity.ok(puestoService.crear(dto, idEmpresa));
    }

    @GetMapping("/puestos/{idPuesto}/candidatos")
    public ResponseEntity<List<OferenteMatchDto>> buscarCandidatos(@PathVariable Integer idPuesto) {
        return ResponseEntity.ok(oferenteService.buscarCoincidencias(idPuesto));
    }

    @PatchMapping("/puestos/{idPuesto}/desactivar")
    public ResponseEntity<Void> desactivarPuesto(@PathVariable Integer idPuesto) {
        puestoService.desactivar(idPuesto);
        return ResponseEntity.noContent().build();
    }
}

