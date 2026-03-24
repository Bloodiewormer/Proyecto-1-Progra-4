package org.bolsa.empleo.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.bolsa.empleo.dto.LoginRequestDto;
import org.bolsa.empleo.dto.LoginResponseDto;
import org.bolsa.empleo.dto.RegistroEmpresaDto;   // NUEVO
import org.bolsa.empleo.dto.RegistroOferenteDto;
import org.bolsa.empleo.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "auth/login";
    }

    @PostMapping("/api/auth/login")
    @ResponseBody
    public ResponseEntity<LoginResponseDto> procesarLogin(@Valid @RequestBody LoginRequestDto dto, HttpSession session) {
        LoginResponseDto response = authService.login(dto);
        session.setAttribute("usuarioId", response.getIdUsuario());
        session.setAttribute("rol", response.getRol());
        return ResponseEntity.ok(response);
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
            model.addAttribute("empresaDto", new RegistroEmpresaDto()); // limpia form
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
    public String registrarOferente(@Valid @ModelAttribute("oferenteDto") RegistroOferenteDto dto, // NUEVO
                                    BindingResult bindingResult,                               // NUEVO
                                    Model model) {                                             // NUEVO
        if (bindingResult.hasErrors()) {
            return "auth/registro-oferente";
        }
        try {
            authService.registrarOferente(dto);          // NUEVO - llama al service
            model.addAttribute("success", true);
            model.addAttribute("oferenteDto", new RegistroOferenteDto()); // limpia form
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar oferente: " + e.getMessage());
        }
        return "auth/registro-oferente";
    }

    @PostMapping("/api/auth/logout")
    @ResponseBody
    public ResponseEntity<Void> logout(HttpSession session) {
        authService.logout();
        session.invalidate();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public String logoutDesdeVista(HttpSession session) {
        authService.logout();
        session.invalidate();
        return "redirect:/login";
    }

}

