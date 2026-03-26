package org.bolsa.empleo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity          // habilita @PreAuthorize si se necesita a futuro
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // ─────────────────────────────────────────────
    // 1. Encoder de contraseñas
    // ─────────────────────────────────────────────
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ─────────────────────────────────────────────
    // 2. Proveedor de autenticación
    // ─────────────────────────────────────────────
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // ─────────────────────────────────────────────
    // 3. AuthenticationManager (usado en AuthService)
    // ─────────────────────────────────────────────
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ─────────────────────────────────────────────
    // 4. Cadena de filtros HTTP (reglas de acceso)
    // ─────────────────────────────────────────────
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authenticationProvider(authenticationProvider())

                // ── Reglas de autorización por URL ──
                .authorizeHttpRequests(auth -> auth
                        // Recursos públicos sin autenticación
                        .requestMatchers(
                                "/",
                                "/buscar",
                                "/login",
                                "/registro/empresa",
                                "/registro/oferente",
                                "/api/publico/**",
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/webjars/**",
                                "/prueba_login.html"
                        ).permitAll()

                        // Solo ADMIN
                        .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")

                        // Solo EMPRESA
                        .requestMatchers("/empresa/**", "/api/empresa/**").hasRole("EMPRESA")

                        // Solo OFERENTE
                        .requestMatchers("/oferente/**", "/api/oferente/**").hasRole("OFERENTE")

                        // Cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated()
                )

                // ── Formulario de login ──
                .formLogin(form -> form
                        .loginPage("/login")                        // vista Thymeleaf existente
                        .loginProcessingUrl("/api/auth/login")      // POST que procesa credenciales
                        .usernameParameter("credencial")            // nombre del campo en el form
                        .passwordParameter("clave")
                        .successHandler(roleBasedSuccessHandler())  // redirige según rol
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                // ── Logout ──
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

        // ── CSRF: habilitado por defecto (Bootstrap usa forms normales) ──
        // Si necesitas deshabilitarlo para endpoints REST puros descomenta:
        // .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
        ;

        return http.build();
    }

    // ─────────────────────────────────────────────
    // 5. Redireccion post-login según rol
    // ─────────────────────────────────────────────
    @Bean
    public AuthenticationSuccessHandler roleBasedSuccessHandler() {
        return (request, response, authentication) -> {
            boolean isAdmin   = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isEmpresa = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_EMPRESA"));

            if (isAdmin) {
                response.sendRedirect("/admin/dashboard");
            } else if (isEmpresa) {
                response.sendRedirect("/empresa/dashboard");
            } else {
                response.sendRedirect("/oferente/dashboard");
            }
        };
    }
}