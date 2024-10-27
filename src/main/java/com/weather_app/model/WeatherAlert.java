package com.weather_app.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "weather_alerts")
public class WeatherAlert {
    @Id
    private String id;
    private String city;
    private String alertType;
    private String message;
    private double threshold;
    private double currentValue;
    private LocalDateTime timestamp;
    private boolean active;
}
