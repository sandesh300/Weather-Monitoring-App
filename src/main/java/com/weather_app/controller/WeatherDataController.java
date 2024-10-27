package com.weather_app.controller;

import com.weather_app.dto.OpenWeatherResponse;
import com.weather_app.model.WeatherData;
import com.weather_app.service.WeatherDataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = "*")
public class WeatherDataController {

    @Autowired
    private WeatherDataService weatherDataService;

    @GetMapping("/current")
    public ResponseEntity<List<WeatherData>> getCurrentWeatherAllCities() {
        List<WeatherData> weatherDataList = weatherDataService.getAllCitiesLatestWeather();
        if (weatherDataList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(weatherDataList);
        }
        return ResponseEntity.ok(weatherDataList);
    }


    @GetMapping("/current/{city}")
    public ResponseEntity<WeatherData> getCurrentWeatherForCity(@PathVariable String city) {
        List<WeatherData> latestData = weatherDataService.getLatestWeatherDataForCity(city);
        return latestData.isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(latestData.get(0));
    }

    @GetMapping("/history/{city}")
    public ResponseEntity<List<WeatherData>> getWeatherHistory(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(weatherDataService.getRecentWeatherData(city, startTime, endTime));
    }

    @GetMapping("/refresh/{city}")
    public ResponseEntity<WeatherData> refreshWeatherData(@PathVariable String city) {
        try {
            WeatherData weatherData = weatherDataService.fetchWeatherDataForCity(city);
            return ResponseEntity.ok(weatherData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
