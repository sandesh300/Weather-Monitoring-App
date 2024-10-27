package com.weather_app.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "alert_configurations")
public class AlertConfiguration {
    @Id
    private String id;
    private String city;
    private String parameter; // temperature, humidity, wind_speed
    private double threshold;
    private String condition; // GREATER_THAN, LESS_THAN, EQUALS
    private int consecutiveUpdates;
    private boolean enabled;
    private String email;
}
