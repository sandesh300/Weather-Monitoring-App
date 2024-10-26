package com.weather_app.dto;

import lombok.Data;

import java.util.List;

@Data
public class DailySummaryResponse {
    private DailySummary currentDay;
    private List<DailySummary> historicalData;
    private WeatherTrends trends;

    @Data
    public static class WeatherTrends {
        private double temperatureTrend; // Positive means increasing, negative means decreasing
        private double humidityTrend;
        private double windSpeedTrend;
        private List<String> commonConditions;
    }
}