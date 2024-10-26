package com.weather_app.controller;

import com.weather_app.dto.OpenWeatherResponse;
import com.weather_app.model.DailySummary;
import com.weather_app.model.WeatherData;
import com.weather_app.service.DailySummaryService;
import com.weather_app.service.WeatherService;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {
    private final WeatherService weatherService;

    @GetMapping("/{city}")
    public ResponseEntity<OpenWeatherResponse.WeatherDataDTO> getCurrentWeather(
            @PathVariable String city,
            @AuthenticationPrincipal UserDetails userDetails) {
        WeatherData weatherData = weatherService.fetchAndSaveWeatherData(city, userDetails.getUsername());
        return ResponseEntity.ok(convertToDTO(weatherData));
    }

    private OpenWeatherResponse.WeatherDataDTO convertToDTO(WeatherData weatherData) {
        OpenWeatherResponse.WeatherDataDTO dto = new OpenWeatherResponse.WeatherDataDTO();
        dto.setCity(weatherData.getCity());
        dto.setMainCondition(weatherData.getMainCondition());
        dto.setTemperature(weatherData.getTemperature());
        dto.setFeelsLike(weatherData.getFeelsLike());
        dto.setHumidity(weatherData.getHumidity());
        dto.setWindSpeed(weatherData.getWindSpeed());
        dto.setPressure(weatherData.getPressure());
        dto.setTimestamp(weatherData.getTimestamp().toString());  // Ensure timestamp is properly formatted
        return dto;
    }


    @GetMapping("/{city}/history")
    public ResponseEntity<List<OpenWeatherResponse.WeatherDataDTO>> getWeatherHistory(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime) {
        return ResponseEntity.ok(weatherService.getWeatherData(city, startTime, endTime));
    }
}