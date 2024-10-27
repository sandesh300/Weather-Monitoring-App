package com.weather_app.controller;


import com.weather_app.dto.AlertConfigurationDTO;
import com.weather_app.model.AlertConfiguration;
import com.weather_app.model.WeatherAlert;
import com.weather_app.service.AlertConfigurationService;
import com.weather_app.service.WeatherAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class WeatherAlertController {

    private final WeatherAlertService alertService;
    private final AlertConfigurationService configService;

    @GetMapping("/active/{city}")
    public ResponseEntity<List<WeatherAlert>> getActiveAlerts(@PathVariable String city) {
        return ResponseEntity.ok(alertService.getActiveAlerts(city));
    }

    @GetMapping("/history/{city}")
    public ResponseEntity<List<WeatherAlert>> getAlertHistory(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(alertService.getAlertHistory(city, startTime, endTime));
    }

    @PutMapping("/{alertId}/deactivate")
    public ResponseEntity<Void> deactivateAlert(@PathVariable String alertId) {
        alertService.deactivateAlert(alertId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/config")
    public ResponseEntity<AlertConfiguration> createAlertConfig(@RequestBody AlertConfigurationDTO configDTO) {
        return ResponseEntity.ok(configService.createAlertConfiguration(configDTO));
    }

    @PutMapping("/config/{id}")
    public ResponseEntity<AlertConfiguration> updateAlertConfig(
            @PathVariable String id,
            @RequestBody AlertConfigurationDTO configDTO) {
        return ResponseEntity.ok(configService.updateAlertConfiguration(id, configDTO));
    }

    @GetMapping("/config")
    public ResponseEntity<List<AlertConfiguration>> getAlertConfigs(
            @RequestParam(required = false) String city) {
        return ResponseEntity.ok(configService.getAlertConfigurations(city));
    }

    @DeleteMapping("/config/{id}")
    public ResponseEntity<Void> deleteAlertConfig(@PathVariable String id) {
        configService.deleteAlertConfiguration(id);
        return ResponseEntity.ok().build();
    }
}