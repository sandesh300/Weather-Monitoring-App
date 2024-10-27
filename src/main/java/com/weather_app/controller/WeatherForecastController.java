package com.weather_app.controller;

import com.weather_app.model.WeatherForecast;
import com.weather_app.service.WeatherForecastService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/forecast")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class WeatherForecastController {

    private final WeatherForecastService forecastService;

    @GetMapping("/current")
    public ResponseEntity<List<WeatherForecast>> getCurrentForecastAllCities() {
        return ResponseEntity.ok(forecastService.getAllCitiesLatestForecast());
    }

    @GetMapping("/{city}")
    public ResponseEntity<List<WeatherForecast>> getCityForecast(@PathVariable String city) {
        return ResponseEntity.ok(forecastService.getForecastForCity(city));
    }

    @GetMapping("/{city}/range")
    public ResponseEntity<List<WeatherForecast>> getCityForecastRange(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(forecastService.getForecastForCityInRange(city, startTime, endTime));
    }

    @PostMapping("/refresh/{city}")
    public ResponseEntity<Void> refreshForecast(@PathVariable String city) {
        forecastService.fetchAndSaveForecast(city);
        return ResponseEntity.ok().build();
    }
}