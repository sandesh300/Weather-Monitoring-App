package com.weather_app.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
public class DailyWeatherSummaryDTO {
    private String city;
    private LocalDate date;
    private double avgTemperature;
    private double maxTemperature;
    private double minTemperature;
    private String dominantWeatherCondition;
    private String dominantWeatherReason;
    private double avgHumidity;
    private double avgWindSpeed;
    private Map<String, Integer> weatherConditionCounts;
    private int totalReadings;
}