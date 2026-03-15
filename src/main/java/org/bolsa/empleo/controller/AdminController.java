package org.bolsa.empleo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.bolsa.empleo.model.Usuario;
import org.bolsa.empleo.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Controller
public class AdminController {
    private final UsuarioService usuarioService;

    public AdminController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(HttpSession session) {
        validarRolAdmin(session);
        return "admin/dashboard";
    }

    @GetMapping("/admin/empresas-pendientes")
    public String empresasPendientes(HttpSession session) {
        validarRolAdmin(session);
        return "admin/empresas-pendientes";
    }

    @GetMapping("/admin/oferentes-pendientes")
    public String oferentesPendientes(HttpSession session) {
        validarRolAdmin(session);
        return "admin/oferentes-pendientes";
    }

    @PatchMapping("/api/admin/usuarios/{idUsuario}/aprobar")
    @ResponseBody
    public ResponseEntity<Usuario> aprobarUsuario(@PathVariable Integer idUsuario, HttpSession session) {
        validarRolAdmin(session);
        return ResponseEntity.ok(usuarioService.aprobarUsuario(idUsuario));
    }

    @GetMapping("/admin/caracteristicas")
    public String gestionarCaracteristicas(HttpSession session) {
        validarRolAdmin(session);
        return "admin/caracteristicas";
    }

    @GetMapping("/admin/reporte")
    public String generarReporte(HttpSession session) {
        validarRolAdmin(session);
        return "admin/reporte";
    }


    private void validarRolAdmin(HttpSession session) {
        Object rol = session.getAttribute("rol");
        if (rol == null || !"ADMIN".equalsIgnoreCase(rol.toString())) {
            throw new ResponseStatusException(FORBIDDEN, "Acceso restringido a administradores");
        }
    }
}
