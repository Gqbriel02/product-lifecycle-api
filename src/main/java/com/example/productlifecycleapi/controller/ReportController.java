package com.example.productlifecycleapi.controller;

import com.example.productlifecycleapi.service.ReportService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api/products")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/{productId}/report")
    public ResponseEntity<InputStreamResource> generateProductReport(@PathVariable Long productId) {
        try {
            ByteArrayInputStream reportStream = reportService.generateProductReport(productId);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=product_report.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(reportStream));

        } catch (JRException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
