package org.bolsa.empleo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/oferente")
public class OferenteController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "oferente/dashboard";
    }

    @GetMapping("/habilidades")
    public String habilidades() {
        return "oferente/habilidades";
    }

    @GetMapping("/subir-cv")
    public String subirCv() {
        return "oferente/subir-cv";
    }
}
