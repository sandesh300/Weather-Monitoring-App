package com.weather_app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "weather_forecasts")
public class WeatherForecast {
    @Id
    private String id;
    private String city;
    private LocalDateTime forecastTime;
    private double temperature;
    private double feelsLike;
    private String mainCondition;
    private String description;
    private double humidity;
    private double windSpeed;
    private int cloudCover;
    private double rainProbability;
    private LocalDateTime retrievalTime;
}
