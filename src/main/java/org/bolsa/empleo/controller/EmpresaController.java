package org.bolsa.empleo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/empresa")
public class EmpresaController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "empresa/dashboard";
    }

    @GetMapping("/mis-puestos")
    public String misPuestos() {
        return "empresa/mis-puestos";
    }

    @GetMapping("/nuevo-puesto")
    public String nuevoPuesto() {
        return "empresa/nuevo-puesto";
    }

    @GetMapping("/candidatos")
    public String candidatos() {
        return "empresa/candidatos";
    }

    @GetMapping("/detalle-candidato")
    public String detalleCandidato() {
        return "empresa/detalle-candidato";
    }
}