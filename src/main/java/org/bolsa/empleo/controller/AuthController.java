package org.bolsa.empleo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/registro/empresa")
    public String registroEmpresa() {
        return "auth/registro-empresa";
    }

    @GetMapping("/registro/oferente")
    public String registroOferente() {
        return "auth/registro-oferente";
    }
}