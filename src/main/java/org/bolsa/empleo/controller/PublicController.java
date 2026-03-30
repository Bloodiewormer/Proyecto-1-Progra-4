package org.bolsa.empleo.controller;

import org.bolsa.empleo.dto.PuestoFiltroDto;
import org.bolsa.empleo.model.Caracteristica;
import org.bolsa.empleo.model.Puesto;
import org.bolsa.empleo.repository.CaracteristicaRepository;
import org.bolsa.empleo.service.PuestoService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Controller
public class PublicController {

    private final PuestoService puestoService;
    private final CaracteristicaRepository caracteristicaRepository;

    public PublicController(PuestoService puestoService,
                            CaracteristicaRepository caracteristicaRepository) {
        this.puestoService = puestoService;
        this.caracteristicaRepository = caracteristicaRepository;
    }

    @GetMapping("/")
    public String mostrarInicio(Model model) {
        model.addAttribute("puestosRecientes", puestoService.listarRecientes());
        return "public/index";
    }

    @GetMapping("/buscar")
    public String mostrarBusqueda(Model model) {
        model.addAttribute("filtro", new PuestoFiltroDto());
        agregarCaracteristicasAlModelo(model);
        return "public/buscar";
    }

    @PostMapping("/buscar")
    public String buscarPuestos(@ModelAttribute("filtro") PuestoFiltroDto filtro, Model model) {
        List<Puesto> resultados = puestoService.buscarPorFiltros(filtro);
        model.addAttribute("resultados", resultados);
        model.addAttribute("filtro", filtro);
        agregarCaracteristicasAlModelo(model);
        return "public/buscar";
    }


    @GetMapping("/cv/{filename}")
    public ResponseEntity<Resource> verCV(@PathVariable String filename,
                                          @AuthenticationPrincipal UserDetails principal) {
        if (principal == null) {
            throw new ResponseStatusException(FORBIDDEN, "Debe iniciar sesión para ver CVs");
        }

        boolean tienePermiso = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().matches("ROLE_(OFERENTE|EMPRESA|ADMIN)"));

        if (!tienePermiso) {
            throw new ResponseStatusException(FORBIDDEN, "Acceso denegado al CV");
        }

        try {
            Path filePath = Paths.get("src/main/resources/static/uploads/cv/" + filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new ResponseStatusException(FORBIDDEN, "CV no encontrado");
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (Exception e) {
            throw new ResponseStatusException(FORBIDDEN, "Error al acceder al CV");
        }
    }

    private void agregarCaracteristicasAlModelo(Model model) {
        List<Caracteristica> todas = caracteristicaRepository.findAll();
        List<Caracteristica> raices = todas.stream()
                .filter(c -> c.getPadre() == null)
                .collect(Collectors.toList());
        model.addAttribute("todasCaracteristicas", todas);
        model.addAttribute("raices", raices);
    }
}