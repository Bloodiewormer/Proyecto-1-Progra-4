package org.bolsa.empleo.controller;

import org.bolsa.empleo.model.Usuario;
import org.bolsa.empleo.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UsuarioService usuarioService;

    public AdminController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<String> dashboard() {
        return ResponseEntity.ok("Dashboard admin");
    }

    @PatchMapping("/usuarios/{idUsuario}/aprobar")
    public ResponseEntity<Usuario> aprobarUsuario(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(usuarioService.aprobarUsuario(idUsuario));
    }

    @GetMapping("/caracteristicas")
    public ResponseEntity<String> gestionarCaracteristicas() {
        return ResponseEntity.ok("Gestion de caracteristicas");
    }

    @GetMapping("/reportes")
    public ResponseEntity<String> generarReporte() {
        return ResponseEntity.ok("Reporte general");
    }
}

