package org.bolsa.empleo.controller;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.bolsa.empleo.model.Caracteristica;
import org.bolsa.empleo.model.Empresa;
import org.bolsa.empleo.model.Oferente;
import org.bolsa.empleo.model.Puesto;
import org.bolsa.empleo.repository.CaracteristicaRepository;
import org.bolsa.empleo.repository.EmpresaRepository;
import org.bolsa.empleo.repository.OferenteRepository;
import org.bolsa.empleo.service.PuestoService;
import org.bolsa.empleo.model.Usuario;
import org.bolsa.empleo.service.UsuarioService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UsuarioService usuarioService;
    private final CaracteristicaRepository caracteristicaRepository;
    private final PuestoService puestoService;
    private final EmpresaRepository empresaRepository;
    private final OferenteRepository oferenteRepository;

    public AdminController(UsuarioService usuarioService,
                           CaracteristicaRepository caracteristicaRepository,
                           PuestoService puestoService,
                           EmpresaRepository empresaRepository,
                           OferenteRepository oferenteRepository) {
        this.usuarioService = usuarioService;
        this.caracteristicaRepository = caracteristicaRepository;
        this.puestoService = puestoService;
        this.empresaRepository = empresaRepository;
        this.oferenteRepository = oferenteRepository;
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("empresasPendientes",   usuarioService.listarEmpresasPendientes().size());
        model.addAttribute("oferentesPendientes",  usuarioService.listarOferentesPendientes().size());
        model.addAttribute("totalCaracteristicas", caracteristicaRepository.count());
        model.addAttribute("totalPuestos",         puestoService.obtenerTodosLosPuestos().size());
        return "admin/dashboard";
    }

    @GetMapping("/admin/empresas-pendientes")
    public String empresasPendientes(Model model) {
        List<Empresa> empresas = usuarioService.listarEmpresasPendientes().stream()
                .map(u -> empresaRepository.findByUsuarioId(u.getId()).orElse(null))
                .filter(Objects::nonNull)
                .toList();
        model.addAttribute("empresas", empresas);
        return "admin/empresas-pendientes";
    }


    @GetMapping("/admin/oferentes-pendientes")
    public String oferentesPendientes(Model model) {
        List<Oferente> oferentes = usuarioService.listarOferentesPendientes().stream()
                .map(u -> oferenteRepository.findByUsuarioId(u.getId()).orElse(null))
                .filter(Objects::nonNull)
                .toList();
        model.addAttribute("oferentes", oferentes);
        return "admin/oferentes-pendientes";
    }


    @PostMapping("/admin/usuarios/{idUsuario}/aprobar")
    public String aprobarUsuario(@PathVariable Integer idUsuario,
                                 @RequestParam(required = false) String origen) {
        usuarioService.aprobarUsuario(idUsuario);
        return "oferente".equals(origen)
                ? "redirect:/admin/oferentes-pendientes"
                : "redirect:/admin/empresas-pendientes";
    }


    @GetMapping("/admin/caracteristicas")
    public String gestionarCaracteristicas(Model model) {
        model.addAttribute("todasCaracteristicas", caracteristicaRepository.findAll());
        return "admin/caracteristicas";
    }

    @PostMapping("/admin/caracteristicas")
    public String guardarCaracteristica(@RequestParam String nombre,
                                        @RequestParam(required = false) Integer idPadre) {
        Caracteristica nueva = new Caracteristica();
        nueva.setNombre(nombre);
        if (idPadre != null && idPadre > 0) {
            caracteristicaRepository.findById(idPadre).ifPresent(nueva::setPadre);
        }
        caracteristicaRepository.save(nueva);
        return "redirect:/admin/caracteristicas";
    }


    @GetMapping("/admin/reporte")
    public String generarReporte(Model model) {
        List<Puesto> puestos = puestoService.obtenerTodosLosPuestos();

        model.addAttribute("puestos",          puestos);
        model.addAttribute("puestosActivos",   puestos.stream().filter(p -> "ACTIVO".equals(p.getEstado())).count());
        model.addAttribute("puestosInactivos", puestos.stream().filter(p -> "INACTIVO".equals(p.getEstado())).count());
        model.addAttribute("totalEmpresas",    empresaRepository.count());
        model.addAttribute("totalOferentes",   oferenteRepository.count());
        return "admin/reporte";
    }


    @GetMapping("/admin/reporte/pdf")
    @ResponseBody
    public ResponseEntity<byte[]> generarReportePdf() {
        List<Puesto> puestos = puestoService.obtenerTodosLosPuestos();


        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, List<Puesto>> porMes = puestos.stream()
                .filter(p -> p.getFechaPublicacion() != null)
                .collect(Collectors.groupingBy(p ->
                                p.getFechaPublicacion()
                                        .atZone(ZoneId.systemDefault())
                                        .format(fmt),
                        TreeMap::new,
                        Collectors.toList()));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 40, 40, 60, 40);

        try {
            PdfWriter.getInstance(doc, baos);
            doc.open();


            Font fTitulo  = FontFactory.getFont(FontFactory.HELVETICA_BOLD,  16, Color.decode("#1f232a"));
            Font fSub     = FontFactory.getFont(FontFactory.HELVETICA,        9, Color.DARK_GRAY);
            Font fMes     = FontFactory.getFont(FontFactory.HELVETICA_BOLD,  12, Color.decode("#0d6efd"));
            Font fHeader  = FontFactory.getFont(FontFactory.HELVETICA_BOLD,   9, Color.WHITE);
            Font fCelda   = FontFactory.getFont(FontFactory.HELVETICA,        9, Color.BLACK);


            Paragraph titulo = new Paragraph("Bolsa de Empleo — Reporte de Puestos por Mes", fTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(titulo);

            Paragraph fecha = new Paragraph(
                    "Generado el " + java.time.LocalDate.now()
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    fSub);
            fecha.setAlignment(Element.ALIGN_CENTER);
            doc.add(fecha);
            doc.add(Chunk.NEWLINE);


            PdfPTable resumen = new PdfPTable(4);
            resumen.setWidthPercentage(100);
            resumen.setSpacingAfter(14);
            float[] anchosResumen = {25f, 25f, 25f, 25f};
            resumen.setWidths(anchosResumen);
            Color azulClaro = Color.decode("#e9f2ff");

            agregarCeldaResumen(resumen, "Total puestos",   String.valueOf(puestos.size()),           azulClaro, fHeader, fCelda);
            agregarCeldaResumen(resumen, "Activos",
                    String.valueOf(puestos.stream().filter(p -> "ACTIVO".equals(p.getEstado())).count()),   azulClaro, fHeader, fCelda);
            agregarCeldaResumen(resumen, "Inactivos",
                    String.valueOf(puestos.stream().filter(p -> "INACTIVO".equals(p.getEstado())).count()), azulClaro, fHeader, fCelda);
            agregarCeldaResumen(resumen, "Empresas",       String.valueOf(empresaRepository.count()),  azulClaro, fHeader, fCelda);
            doc.add(resumen);

            if (porMes.isEmpty()) {
                doc.add(new Paragraph("No hay puestos registrados.", fCelda));
            }


            for (Map.Entry<String, List<Puesto>> entry : porMes.entrySet()) {
                Paragraph encabezadoMes = new Paragraph("Mes: " + entry.getKey() +
                        "  (" + entry.getValue().size() + " puesto(s))", fMes);
                encabezadoMes.setSpacingBefore(10);
                encabezadoMes.setSpacingAfter(4);
                doc.add(encabezadoMes);

                PdfPTable tabla = new PdfPTable(5);
                tabla.setWidthPercentage(100);
                tabla.setWidths(new float[]{8f, 26f, 24f, 16f, 14f});
                tabla.setSpacingAfter(6);

                Color azulOscuro = Color.decode("#1f232a");
                agregarCeldaEncabezado(tabla, "#",           azulOscuro, fHeader);
                agregarCeldaEncabezado(tabla, "Empresa",     azulOscuro, fHeader);
                agregarCeldaEncabezado(tabla, "Puesto",      azulOscuro, fHeader);
                agregarCeldaEncabezado(tabla, "Salario",     azulOscuro, fHeader);
                agregarCeldaEncabezado(tabla, "Estado",      azulOscuro, fHeader);

                int idx = 1;
                for (Puesto p : entry.getValue()) {
                    Color fondo = (idx % 2 == 0) ? Color.decode("#f8f9fa") : Color.WHITE;
                    agregarCeldaDato(tabla, String.valueOf(idx++),               fondo, fCelda, Element.ALIGN_CENTER);
                    agregarCeldaDato(tabla, p.getEmpresa().getNombre(),          fondo, fCelda, Element.ALIGN_LEFT);
                    agregarCeldaDato(tabla, p.getTitulo(),                        fondo, fCelda, Element.ALIGN_LEFT);
                    agregarCeldaDato(tabla, "₡ " + p.getSalario().toPlainString(), fondo, fCelda, Element.ALIGN_RIGHT);
                    agregarCeldaDato(tabla, p.getEstado(),                        fondo, fCelda, Element.ALIGN_CENTER);
                }
                doc.add(tabla);
            }

            doc.close();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"reporte-puestos-por-mes.pdf\"")
                .body(baos.toByteArray());
    }


    private void agregarCeldaEncabezado(PdfPTable tabla, String texto, Color fondo, Font fuente) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
        celda.setBackgroundColor(fondo);
        celda.setPadding(5);
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        tabla.addCell(celda);
    }

    private void agregarCeldaDato(PdfPTable tabla, String texto, Color fondo, Font fuente, int alineacion) {
        PdfPCell celda = new PdfPCell(new Phrase(texto != null ? texto : "", fuente));
        celda.setBackgroundColor(fondo);
        celda.setPadding(4);
        celda.setHorizontalAlignment(alineacion);
        tabla.addCell(celda);
    }

    private void agregarCeldaResumen(PdfPTable tabla, String etiqueta, String valor,
                                     Color fondo, Font fHeader, Font fCelda) {
        PdfPCell cEtiqueta = new PdfPCell(new Phrase(etiqueta, fHeader));
        cEtiqueta.setBackgroundColor(Color.decode("#1f232a"));
        cEtiqueta.setPadding(5);
        cEtiqueta.setHorizontalAlignment(Element.ALIGN_CENTER);
        tabla.addCell(cEtiqueta);

        PdfPCell cValor = new PdfPCell(new Phrase(valor,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        cValor.setBackgroundColor(fondo);
        cValor.setPadding(6);
        cValor.setHorizontalAlignment(Element.ALIGN_CENTER);
        tabla.addCell(cValor);
    }
}