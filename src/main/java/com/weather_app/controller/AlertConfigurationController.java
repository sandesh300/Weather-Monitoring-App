package com.weather_app.controller;

import com.weather_app.dto.AlertConfigurationDTO;
import com.weather_app.model.AlertConfiguration;
import com.weather_app.service.AlertConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alert-configurations")
@RequiredArgsConstructor
public class AlertConfigurationController {

    private final AlertConfigurationService configService;

    @PostMapping
    public ResponseEntity<AlertConfiguration> createAlertConfiguration(@RequestBody AlertConfigurationDTO configDTO) {
        AlertConfiguration createdConfig = configService.createAlertConfiguration(configDTO);
        return new ResponseEntity<>(createdConfig, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlertConfiguration> updateAlertConfiguration(
            @PathVariable String id,
            @RequestBody AlertConfigurationDTO configDTO) {
        try {
            AlertConfiguration updatedConfig = configService.updateAlertConfiguration(id, configDTO);
            return new ResponseEntity<>(updatedConfig, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<AlertConfiguration>> getAlertConfigurations(
            @RequestParam(required = false) String city) {
        List<AlertConfiguration> configurations = configService.getAlertConfigurations(city);
        return new ResponseEntity<>(configurations, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlertConfiguration(@PathVariable String id) {
        configService.deleteAlertConfiguration(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
