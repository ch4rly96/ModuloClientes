package com.example.demo.controller;

import com.example.demo.model.Cliente;
import com.example.demo.model.Reclamo;
import com.example.demo.model.Direcciones;
import com.example.demo.service.ClienteService;
import com.example.demo.service.DireccionesService;
import com.example.demo.service.ReclamoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Controller
@RequestMapping("/reportes")
public class ReportesWebController {

    private final ClienteService clienteService;
    private final ReclamoService reclamoService;
    private final DireccionesService direccionesService;

    public ReportesWebController(ClienteService clienteService, ReclamoService reclamoService, DireccionesService direccionesService) {
        this.clienteService = clienteService;
        this.reclamoService = reclamoService;
        this.direccionesService = direccionesService;
    }

    @GetMapping
    public String listar(Model model, @RequestParam(required = false) Long clienteId) {
        List<Cliente> clientes = clienteService.listarClientes();
        model.addAttribute("clientes", clientes);

        if (clienteId != null) {
            Cliente cliente = clienteService.obtenerPorId(clienteId)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
            List<Reclamo> reclamos = reclamoService.listarPorCliente(clienteId);
            model.addAttribute("clienteSeleccionado", cliente);
            model.addAttribute("reclamosSeleccionados", reclamos);
            model.addAttribute("direccionesSeleccionadas", direccionesService.listarPorCliente(clienteId));
        }

        return "reportes/list";
    }

    @GetMapping("/{idCliente}")
    public String verCliente(@PathVariable Long idCliente, Model model) {
        Cliente cliente = clienteService.obtenerPorId(idCliente).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        List<Reclamo> reclamos = reclamoService.listarPorCliente(idCliente);
        model.addAttribute("cliente", cliente);
        model.addAttribute("reclamos", reclamos);
        model.addAttribute("direcciones", direccionesService.listarPorCliente(idCliente));
        return "reportes/view";
    }

    @GetMapping("/pdf/{idCliente}")
    public ResponseEntity<byte[]> generarPDF(@PathVariable Long idCliente) {
        try {
            Cliente cliente = clienteService.obtenerPorId(idCliente)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
            List<Reclamo> reclamos = reclamoService.listarPorCliente(idCliente);
            List<Direcciones> direcciones = direccionesService.listarPorCliente(idCliente);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            // Título
            String titulo = cliente.getTipoCliente().equals("persona")
                    ? cliente.getNombres() + " " + cliente.getApellidos()
                    : cliente.getRazonSocial();

            Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph pTitulo = new Paragraph("REPORTE DE CLIENTE", fontTitulo);
            pTitulo.setAlignment(Element.ALIGN_CENTER);
            document.add(pTitulo);

            Font fontSubtitulo = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            document.add(new Paragraph(titulo, fontSubtitulo));
            document.add(new Paragraph(" "));

            // Datos del cliente
            Font fontSeccion = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            document.add(new Paragraph("DATOS PERSONALES", fontSeccion));
            document.add(new Paragraph("Documento: " + cliente.getTipoDocumento() + " " + cliente.getDocumentoIdentidad()));
            document.add(new Paragraph("Email: " + (cliente.getEmail() != null ? cliente.getEmail() : "No registrado")));
            document.add(new Paragraph("Teléfono: " + (cliente.getTelefono() != null ? cliente.getTelefono() : "No registrado")));
            document.add(new Paragraph("Dirección: " + (cliente.getDireccionPrincipal() != null ? cliente.getDireccionPrincipal() : "No registrada")));
            document.add(new Paragraph("Deuda: S/ " + cliente.getDeudaActual()));
            document.add(new Paragraph("Estado: " + (cliente.getEstado() ? "Activo" : "Inactivo")));
            document.add(new Paragraph(" "));

            // Direcciones
            if (!direcciones.isEmpty()) {
                document.add(new Paragraph("DIRECCIONES", fontSeccion));
                for (Direcciones d : direcciones) {
                    document.add(new Paragraph("• " + d.getTipoDireccion() + " - " + d.getDireccion()));
                }
                document.add(new Paragraph(" "));
            }

            // Reclamos
            if (!reclamos.isEmpty()) {
                document.add(new Paragraph("RECLAMOS", fontSeccion));
                PdfPTable table = new PdfPTable(4);
                table.setWidthPercentage(100);

                Font fontHeader = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

                PdfPCell cell1 = new PdfPCell(new Phrase("Nº Reclamo", fontHeader));
                PdfPCell cell2 = new PdfPCell(new Phrase("Motivo", fontHeader));
                PdfPCell cell3 = new PdfPCell(new Phrase("Estado", fontHeader));
                PdfPCell cell4 = new PdfPCell(new Phrase("Fecha", fontHeader));

                table.addCell(cell1);
                table.addCell(cell2);
                table.addCell(cell3);
                table.addCell(cell4);

                for (Reclamo r : reclamos) {
                    table.addCell(r.getNumeroReclamo());
                    table.addCell(r.getMotivo());
                    table.addCell(r.getEstado());
                    table.addCell(r.getFechaReclamo().toString());
                }
                document.add(table);
            } else {
                document.add(new Paragraph("No hay reclamos registrados."));
            }

            document.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "reporte_cliente_" + cliente.getIdCliente() + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF: " + e.getMessage(), e);
        }
    }
}
