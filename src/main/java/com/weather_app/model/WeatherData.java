package com.weather_app.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data
@Document(collection = "weather_data")
public class WeatherData {
    @Id
    private String id;
    private String city;
    private String mainCondition;
    private double temperature;
    private double feelsLike;
    private double humidity;
    private double windSpeed;
    private double pressure;
    private Instant timestamp;
}