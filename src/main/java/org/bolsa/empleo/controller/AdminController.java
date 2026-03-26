package org.bolsa.empleo.controller;

import org.bolsa.empleo.model.Caracteristica;
import org.bolsa.empleo.model.Puesto;
import org.bolsa.empleo.repository.CaracteristicaRepository;
import org.bolsa.empleo.service.PuestoService;
import org.bolsa.empleo.model.Usuario;
import org.bolsa.empleo.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@PreAuthorize("hasRole('ADMIN')")   // protección declarativa a nivel de clase
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
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/admin/empresas-pendientes")
    public String empresasPendientes(Model model) {
        List<Usuario> pendientes = usuarioService.listarEmpresasPendientes();
        model.addAttribute("empresas", pendientes);
        return "admin/empresas-pendientes";
    }

    @GetMapping("/admin/oferentes-pendientes")
    public String oferentesPendientes(Model model) {
        List<Usuario> pendientes = usuarioService.listarOferentesPendientes();
        model.addAttribute("oferentes", pendientes);
        return "admin/oferentes-pendientes";
    }

    @PostMapping("/admin/usuarios/{idUsuario}/aprobar")
    public String aprobarUsuarioDesdeVista(@PathVariable Integer idUsuario) {
        usuarioService.aprobarUsuario(idUsuario);
        return "redirect:/admin/empresas-pendientes";
    }

    @GetMapping("/admin/caracteristicas")
    public String gestionarCaracteristicas(Model model) {
        model.addAttribute("todasCaracteristicas", caracteristicaRepository.findAll());
        return "admin/caracteristicas";
    }

    @PostMapping("/admin/caracteristicas")
    public String guardarCaracteristica(@RequestParam String nombre,
                                        @RequestParam(required = false) Integer idPadre) {

        Caracteristica nueva = new Caracteristica();
        nueva.setNombre(nombre);

        if (idPadre != null && idPadre > 0) {
            caracteristicaRepository.findById(idPadre).ifPresent(nueva::setPadre);
        }

        caracteristicaRepository.save(nueva);
        return "redirect:/admin/caracteristicas";
    }

    @GetMapping("/admin/reporte")
    public String generarReporte(Model model) {
        List<Puesto> puestos = puestoService.obtenerTodosLosPuestos();
        model.addAttribute("puestos", puestos);
        return "admin/reporte";
    }
}
