package com.bookit.bookit.controller.faktura;

import com.bookit.bookit.config.JwtService;
import com.bookit.bookit.dto.InvoiceIdRequest;
import com.bookit.bookit.entity.faktura.Faktura;
import com.bookit.bookit.repository.faktura.FakturaRepository;
import com.bookit.bookit.service.admin.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;



@RestController
@RequestMapping("/api/admin")
public class FakturaController {

    private final FakturaRepository fakturaRepository;
    private final JwtService jwtService;
    private final AdminService adminService;

    public FakturaController(FakturaRepository fakturaRepository, JwtService jwtService, AdminService adminService) {
        this.fakturaRepository = fakturaRepository;
        this.jwtService = jwtService;
        this.adminService = adminService;
    }


    // PDF filen som man kan ladda ner via downloadInvoice endpointen i FakturaController. Kan användas av både Admin och kund:
    @PostMapping("/invoices/download")
    public ResponseEntity<?> downloadInvoice(@RequestBody InvoiceIdRequest invoiceIdRequest, HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization").substring(7);
            Integer userId = jwtService.extractUserId(token);

            Faktura faktura = fakturaRepository.findById(invoiceIdRequest.getInvoiceId())
                    .orElseThrow(() -> new RuntimeException("Invoice not found"));

            if (!adminService.isAdmin(userId) && !faktura.getKund().getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
            }

            String filePath = faktura.getInvoiceFilePath();
            File file = new File(filePath);
            HttpHeaders header = new HttpHeaders();
            header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
            header.add("Cache-Control", "no-cache, no-store, must-revalidate");
            header.add("Pragma", "no-cache");
            header.add("Expires", "0");

            Path path = Paths.get(file.getAbsolutePath());
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

            return ResponseEntity.ok()
                    .headers(header)
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error downloading invoice.");
        }
    }
}
