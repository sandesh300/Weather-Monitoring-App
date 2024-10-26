package com.weather_app.controller;

import com.weather_app.model.AlertConfig;
import com.weather_app.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {
    private final AlertService alertService;

    @PostMapping
    public ResponseEntity<AlertConfig> createAlertConfig(@RequestBody AlertConfig config) {
        return ResponseEntity.ok(alertService.saveAlertConfig(config));
    }

    @GetMapping("/{city}")
    public ResponseEntity<List<AlertConfig>> getCityAlerts(@PathVariable String city) {
        return ResponseEntity.ok(alertService.getCityAlerts(city));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAlert(@PathVariable String id) {
        alertService.deleteAlert(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/simulate")
    public ResponseEntity<?> simulateWeatherEvent(
            @RequestParam String city,
            @RequestParam double temp,
            @RequestParam double humidity,
            @RequestParam double windSpeed
    ) {
        alertService.simulateWeatherData(city, temp, humidity, windSpeed);
        return ResponseEntity.ok("Simulation completed. Check your alerts.");
    }
}