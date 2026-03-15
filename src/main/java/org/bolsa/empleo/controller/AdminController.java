package org.bolsa.empleo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/empresas-pendientes")
    public String empresasPendientes() {
        return "admin/empresas-pendientes";
    }

    @GetMapping("/oferentes-pendientes")
    public String oferentesPendientes() {
        return "admin/oferentes-pendientes";
    }

    @GetMapping("/caracteristicas")
    public String caracteristicas() {
        return "admin/caracteristicas";
    }

    @GetMapping("/reporte")
    public String reporte() {
        return "admin/reporte";
    }
}
