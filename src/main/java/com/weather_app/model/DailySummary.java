package com.weather_app.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.util.Map;

@Data
@Document(collection = "daily_summaries")
public class DailySummary {
    @Id
    private String id;
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
