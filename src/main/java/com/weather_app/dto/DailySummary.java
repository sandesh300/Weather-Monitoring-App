package com.weather_app.dto;


import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class DailySummary {
    private String city;
    private LocalDate date;
    private double avgTemperature;
    private double maxTemperature;
    private double minTemperature;
    private String dominantWeatherCondition;
    private double avgHumidity;
    private double avgWindSpeed;
    private double avgPressure;
    private Map<String, Integer> weatherConditionCounts;
}
