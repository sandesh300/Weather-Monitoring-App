package com.weather_app.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class WeatherStatistics {
    private double avgTemperature;
    private double maxTemperature;
    private double minTemperature;
    private double avgHumidity;
    private double avgWindSpeed;
    private Map<String, Integer> conditionFrequency;
    private LocalDate startDate;
    private LocalDate endDate;
}