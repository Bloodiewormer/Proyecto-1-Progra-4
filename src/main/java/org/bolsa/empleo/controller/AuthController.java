package org.bolsa.empleo.controller;

import jakarta.validation.Valid;
import org.bolsa.empleo.dto.RegistroEmpresaDto;
import org.bolsa.empleo.dto.RegistroOferenteDto;
import org.bolsa.empleo.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String mostrarLogin(@RequestParam(required = false) String error,
                               @RequestParam(required = false) String logout,
                               Model model) {
        if (error != null)  model.addAttribute("loginError",  true);
        if (logout != null) model.addAttribute("logoutMsg",   true);
        return "auth/login";
    }


    @GetMapping("/registro/empresa")
    public String registroEmpresa(Model model) {
        if (!model.containsAttribute("empresaDto")) {
            model.addAttribute("empresaDto", new RegistroEmpresaDto());
        }
        return "auth/registro-empresa";
    }

    @PostMapping("/registro/empresa")
    public String registrarEmpresa(@Valid @ModelAttribute("empresaDto") RegistroEmpresaDto dto,
                                   BindingResult bindingResult,
                                   Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/registro-empresa";
        }
        try {
            authService.registrarEmpresa(dto);
            model.addAttribute("success", true);
            model.addAttribute("empresaDto", new RegistroEmpresaDto());
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar empresa: " + e.getMessage());
        }
        return "auth/registro-empresa";
    }

    @GetMapping("/registro/oferente")
    public String registroOferente(Model model) {
        if (!model.containsAttribute("oferenteDto")) {
            model.addAttribute("oferenteDto", new RegistroOferenteDto());
        }
        return "auth/registro-oferente";
    }

    @PostMapping("/registro/oferente")
    public String registrarOferente(@Valid @ModelAttribute("oferenteDto") RegistroOferenteDto dto,
                                    BindingResult bindingResult,
                                    Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/registro-oferente";
        }
        try {
            authService.registrarOferente(dto);
            model.addAttribute("success", true);
            model.addAttribute("oferenteDto", new RegistroOferenteDto());
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar oferente: " + e.getMessage());
        }
        return "auth/registro-oferente";
    }
}