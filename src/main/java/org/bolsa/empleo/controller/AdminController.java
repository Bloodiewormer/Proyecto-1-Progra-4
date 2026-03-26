package org.bolsa.empleo.controller;

import org.bolsa.empleo.model.Usuario;
import org.bolsa.empleo.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@PreAuthorize("hasRole('ADMIN')")   // protección declarativa a nivel de clase
public class AdminController {

    private final UsuarioService usuarioService;

    public AdminController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/admin/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/admin/empresas-pendientes")
    public String empresasPendientes() {
        return "admin/empresas-pendientes";
    }

    @GetMapping("/admin/oferentes-pendientes")
    public String oferentesPendientes() {
        return "admin/oferentes-pendientes";
    }

    @PatchMapping("/api/admin/usuarios/{idUsuario}/aprobar")
    @ResponseBody
    public ResponseEntity<Usuario> aprobarUsuario(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(usuarioService.aprobarUsuario(idUsuario));
    }

    @GetMapping("/admin/caracteristicas")
    public String gestionarCaracteristicas() {
        return "admin/caracteristicas";
    }

    @GetMapping("/admin/reporte")
    public String generarReporte() {
        return "admin/reporte";
    }
}