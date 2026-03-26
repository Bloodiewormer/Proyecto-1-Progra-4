package org.bolsa.empleo.controller;

import org.bolsa.empleo.model.Caracteristica;
import org.bolsa.empleo.model.Empresa;
import org.bolsa.empleo.model.Oferente;
import org.bolsa.empleo.model.Puesto;
import org.bolsa.empleo.repository.CaracteristicaRepository;
import org.bolsa.empleo.repository.EmpresaRepository;
import org.bolsa.empleo.repository.OferenteRepository;
import org.bolsa.empleo.service.PuestoService;
import org.bolsa.empleo.model.Usuario;
import org.bolsa.empleo.service.UsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UsuarioService usuarioService;
    private final CaracteristicaRepository caracteristicaRepository;
    private final PuestoService puestoService;
    private final EmpresaRepository empresaRepository;
    private final OferenteRepository oferenteRepository;

    public AdminController(UsuarioService usuarioService,
                           CaracteristicaRepository caracteristicaRepository,
                           PuestoService puestoService,
                           EmpresaRepository empresaRepository,
                           OferenteRepository oferenteRepository) {
        this.usuarioService = usuarioService;
        this.caracteristicaRepository = caracteristicaRepository;
        this.puestoService = puestoService;
        this.empresaRepository = empresaRepository;
        this.oferenteRepository = oferenteRepository;
    }

    // ── Dashboard con datos reales ──
    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        long empresasPendientes = usuarioService.listarEmpresasPendientes().size();
        long oferentesPendientes = usuarioService.listarOferentesPendientes().size();
        long totalCaracteristicas = caracteristicaRepository.count();
        long totalPuestos = puestoService.obtenerTodosLosPuestos().size();

        model.addAttribute("empresasPendientes", empresasPendientes);
        model.addAttribute("oferentesPendientes", oferentesPendientes);
        model.addAttribute("totalCaracteristicas", totalCaracteristicas);
        model.addAttribute("totalPuestos", totalPuestos);
        return "admin/dashboard";
    }

    // ── Empresas pendientes: pasar objetos Empresa, no Usuario ──
    @GetMapping("/admin/empresas-pendientes")
    public String empresasPendientes(Model model) {
        List<Usuario> usuariosPendientes = usuarioService.listarEmpresasPendientes();
        // Convertir a Empresa para tener nombre, telefono, etc.
        List<Empresa> empresas = usuariosPendientes.stream()
                .map(u -> empresaRepository.findByUsuarioId(u.getId()).orElse(null))
                .filter(e -> e != null)
                .toList();
        model.addAttribute("empresas", empresas);
        return "admin/empresas-pendientes";
    }

    // ── Oferentes pendientes: pasar objetos Oferente ──
    @GetMapping("/admin/oferentes-pendientes")
    public String oferentesPendientes(Model model) {
        List<Usuario> usuariosPendientes = usuarioService.listarOferentesPendientes();
        List<Oferente> oferentes = usuariosPendientes.stream()
                .map(u -> oferenteRepository.findByUsuarioId(u.getId()).orElse(null))
                .filter(o -> o != null)
                .toList();
        model.addAttribute("oferentes", oferentes);
        return "admin/oferentes-pendientes";
    }

    // ── Aprobar usuario (empresa u oferente) ──
    @PostMapping("/admin/usuarios/{idUsuario}/aprobar")
    public String aprobarUsuario(@PathVariable Integer idUsuario,
                                 @RequestParam(required = false) String origen) {
        usuarioService.aprobarUsuario(idUsuario);
        // Redirigir según de donde vino
        if ("oferente".equals(origen)) {
            return "redirect:/admin/oferentes-pendientes";
        }
        return "redirect:/admin/empresas-pendientes";
    }

    // ── Características ──
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

    // ── Reporte con datos reales ──
    @GetMapping("/admin/reporte")
    public String generarReporte(Model model) {
        List<Puesto> puestos = puestoService.obtenerTodosLosPuestos();

        long puestosActivos   = puestos.stream().filter(p -> "ACTIVO".equals(p.getEstado())).count();
        long puestosInactivos = puestos.stream().filter(p -> "INACTIVO".equals(p.getEstado())).count();
        long totalEmpresas    = empresaRepository.count();
        long totalOferentes   = oferenteRepository.count();

        model.addAttribute("puestos", puestos);
        model.addAttribute("puestosActivos", puestosActivos);
        model.addAttribute("puestosInactivos", puestosInactivos);
        model.addAttribute("totalEmpresas", totalEmpresas);
        model.addAttribute("totalOferentes", totalOferentes);
        return "admin/reporte";
    }
}