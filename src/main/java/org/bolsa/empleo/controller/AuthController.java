package org.bolsa.empleo.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.bolsa.empleo.dto.LoginRequestDto;
import org.bolsa.empleo.dto.LoginResponseDto;
import org.bolsa.empleo.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "presentation/Login/Login";
    }

    @PostMapping("/api/auth/login")
    @ResponseBody
    public ResponseEntity<LoginResponseDto> procesarLogin(@Valid @RequestBody LoginRequestDto dto, HttpSession session) {
        LoginResponseDto response = authService.login(dto);
        session.setAttribute("usuarioId", response.getIdUsuario());
        session.setAttribute("rol", response.getRol());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/auth/logout")
    @ResponseBody
    public ResponseEntity<Void> logout(HttpSession session) {
        authService.logout();
        session.invalidate();
        return ResponseEntity.noContent().build();
    }
}

