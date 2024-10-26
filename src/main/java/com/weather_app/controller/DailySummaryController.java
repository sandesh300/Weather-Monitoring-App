package com.weather_app.controller;

import com.weather_app.dto.DailySummary;
import com.weather_app.dto.DailySummaryResponse;

import com.weather_app.service.DailySummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/summaries")
@RequiredArgsConstructor
public class DailySummaryController {
    private final DailySummaryService dailySummaryService;

    @GetMapping("/{city}")
    public ResponseEntity<List<DailySummary>> getCitySummaries(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<DailySummary> summaries = dailySummaryService.getDailySummaries(city, startDate, endDate);
        List<DailySummary> summaryDTOs = summaries.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(summaryDTOs);
    }

    @GetMapping("/user")
    public ResponseEntity<List<DailySummary>> getUserSummaries(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<DailySummary> summaries = dailySummaryService
                .getUserDailySummaries(userDetails.getUsername(), startDate, endDate);
        List<DailySummary> summaryDTOs = summaries.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(summaryDTOs);
    }

    @PostMapping("/generate")
    public ResponseEntity<Void> generateSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        dailySummaryService.generateSummaryForDate(date);
        return ResponseEntity.ok().build();
    }

    private DailySummary convertToDTO(DailySummary summary) {
        DailySummary dto = new DailySummary();
        dto.setCity(summary.getCity());
        dto.setDate(summary.getDate());
        dto.setAvgTemperature(summary.getAvgTemperature());
        dto.setMaxTemperature(summary.getMaxTemperature());
        dto.setMinTemperature(summary.getMinTemperature());
        dto.setDominantWeatherCondition(summary.getDominantWeatherCondition());
        dto.setAvgHumidity(summary.getAvgHumidity());
        dto.setAvgWindSpeed(summary.getAvgWindSpeed());
        dto.setAvgPressure(summary.getAvgPressure());
        dto.setWeatherConditionCounts(summary.getWeatherConditionCounts());
        return dto;
    }
}