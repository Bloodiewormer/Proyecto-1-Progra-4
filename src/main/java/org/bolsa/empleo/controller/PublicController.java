package org.bolsa.empleo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicController {

    @GetMapping("/")
    public String mostrarInicio() {
        return "public/index";
    }

    @GetMapping("/buscar")
    public String mostrarBusqueda() {
        return "public/buscar";
    }
}
