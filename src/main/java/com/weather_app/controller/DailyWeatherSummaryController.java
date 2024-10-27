package com.weather_app.controller;

import com.weather_app.model.DailyWeatherSummary;
import com.weather_app.service.DailyWeatherSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/summary")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DailyWeatherSummaryController {

    private final DailyWeatherSummaryService summaryService;

    @GetMapping("/latest")
    public ResponseEntity<Map<String, DailyWeatherSummary>> getLatestSummaries() {
        return ResponseEntity.ok(summaryService.getAllCitiesLatestSummary());
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<DailyWeatherSummary>> getCityHistory(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(summaryService.getCityWeatherHistory(city, startDate, endDate));
    }

    @GetMapping("/hottest")
    public ResponseEntity<List<DailyWeatherSummary>> getHottestCities(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(summaryService.getHottestCitiesByDate(date));
    }

    @PostMapping("/generate/{city}")
    public ResponseEntity<DailyWeatherSummary> generateSummary(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        DailyWeatherSummary summary = summaryService.generateDailySummary(city, date);
        return summary != null ?
                ResponseEntity.ok(summary) :
                ResponseEntity.notFound().build();
    }
}
