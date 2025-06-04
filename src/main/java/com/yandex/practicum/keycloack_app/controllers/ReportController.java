package com.yandex.practicum.keycloack_app.controllers;

import com.yandex.practicum.keycloack_app.annotation.ManagerOnly;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api")
public class ReportController {

    @GetMapping("/reports")
    @ManagerOnly
    public ResponseEntity<Map<String, Object>> getReport() {
        Map<String, Object> report = new HashMap<>();
        report.put("date", LocalDate.now().toString());
        report.put("totalSales", new Random().nextInt(10000));
        report.put("newUsers", new Random().nextInt(100));
        report.put("conversionRate", String.format("%.2f%%", new Random().nextDouble() * 10));
        return ResponseEntity.ok(report);
    }
}