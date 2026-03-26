package org.bolsa.empleo.controller;

import jakarta.servlet.http.HttpSession;
import org.bolsa.empleo.dto.PuestoFiltroDto;
import org.bolsa.empleo.model.Puesto;
import org.bolsa.empleo.service.PuestoService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import static org.springframework.http.HttpStatus.FORBIDDEN;


@Controller
public class PublicController {
    private final PuestoService puestoService;

    public PublicController(PuestoService puestoService) {
        this.puestoService = puestoService;
    }

    @GetMapping("/")
    public String mostrarInicio(Model model) {
       List<Puesto> recientes = puestoService.listarRecientes();
       model.addAttribute("puestosRecientes", recientes);
        return "public/index";
    }

    @GetMapping("/buscar")
    public String mostrarBusqueda(Model model) {
        model.addAttribute("filtro", new PuestoFiltroDto());
        return "public/buscar";
    }

    @PostMapping("/buscar")
    public String buscarPuestos(@ModelAttribute("filtro") PuestoFiltroDto filtro, Model model) {
        List<Puesto> resultados = puestoService.buscarPorFiltros(filtro);
        model.addAttribute("resultados", resultados);
        model.addAttribute("filtro", filtro); // para mantener los valores en el formulario
        return "public/buscar";
    }

    @GetMapping("/cv/{filename}")
    public ResponseEntity<Resource> verCV(@PathVariable String filename, HttpSession session) {

        Object rolObj = session.getAttribute("rol");
        if (rolObj == null) {
            throw new ResponseStatusException(FORBIDDEN, "Debe iniciar sesión para ver CVs");
        }
        String rol = rolObj.toString().toUpperCase();

        if ("OFERENTE".equals(rol)) {
        } else if (!"EMPRESA".equals(rol) && !"ADMIN".equals(rol)) {
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


}
