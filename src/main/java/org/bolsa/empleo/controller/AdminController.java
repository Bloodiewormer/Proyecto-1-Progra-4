package org.bolsa.empleo.controller;

import jakarta.servlet.http.HttpSession;
import org.bolsa.empleo.model.Caracteristica;
import org.bolsa.empleo.model.Puesto;
import org.bolsa.empleo.repository.CaracteristicaRepository;
import org.bolsa.empleo.service.PuestoService;
import org.bolsa.empleo.service.impl.UsuarioServiceImpl;
import org.springframework.stereotype.Controller;
import org.bolsa.empleo.model.Usuario;
import org.bolsa.empleo.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Controller
public class AdminController {
    private final UsuarioService usuarioService;
    private final CaracteristicaRepository caracteristicaRepository;
    private final PuestoService puestoService;

    public AdminController(UsuarioService usuarioService, CaracteristicaRepository caracteristicaRepository, PuestoService puestoService) {
        this.usuarioService = usuarioService;
        this.caracteristicaRepository = caracteristicaRepository;
        this.puestoService = puestoService;
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(HttpSession session) {
        validarRolAdmin(session);
        return "admin/dashboard";
    }

    @GetMapping("/admin/empresas-pendientes")
    public String empresasPendientes(HttpSession session, Model model) {
        validarRolAdmin(session);
        List<Usuario> pendientes = usuarioService.listarEmpresasPendientes();
        model.addAttribute("empresas", pendientes);
        return "admin/empresas-pendientes";
    }

    @GetMapping("/admin/oferentes-pendientes")
    public String oferentesPendientes(HttpSession session, Model model) {
        validarRolAdmin(session);
        List<Usuario> pendientes = usuarioService.listarOferentesPendientes();
        model.addAttribute("oferentes", pendientes);
        return "admin/oferentes-pendientes";
    }

    @PostMapping("/admin/usuarios/{idUsuario}/aprobar")
    public String aprobarUsuarioDesdeVista(@PathVariable Integer idUsuario, HttpSession session) {
        validarRolAdmin(session);
        usuarioService.aprobarUsuario(idUsuario);
        return "redirect:/admin/empresas-pendientes";
    }

    @GetMapping("/admin/caracteristicas")
    public String gestionarCaracteristicas(HttpSession session, Model model) {
        validarRolAdmin(session);
        model.addAttribute("todasCaracteristicas", caracteristicaRepository.findAll());
        return "admin/caracteristicas";
    }

    @PostMapping("/admin/caracteristicas")
    public String guardarCaracteristica(@RequestParam String nombre,
                                        @RequestParam(required = false) Integer idPadre,
                                        HttpSession session) {
        validarRolAdmin(session);

        Caracteristica nueva = new Caracteristica();
        nueva.setNombre(nombre);

        if (idPadre != null && idPadre > 0) {
            caracteristicaRepository.findById(idPadre).ifPresent(nueva::setPadre);
        }

        caracteristicaRepository.save(nueva);
        return "redirect:/admin/caracteristicas";
    }

    @GetMapping("/admin/reporte")
    public String generarReporte(HttpSession session, Model model) {
        validarRolAdmin(session);
        List<Puesto> puestos = puestoService.obtenerTodosLosPuestos();
        model.addAttribute("puestos", puestos);   // NUEVO: para mostrar tabla en pantalla
        return "admin/reporte";
    }



    private void validarRolAdmin(HttpSession session) {
        Object rol = session.getAttribute("rol");
        if (rol == null || !"ADMIN".equalsIgnoreCase(rol.toString())) {
            throw new ResponseStatusException(FORBIDDEN, "Acceso restringido a administradores");
        }
    }
}
