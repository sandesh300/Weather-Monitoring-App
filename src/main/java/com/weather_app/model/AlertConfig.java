package com.weather_app.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "alert_configs")
public class AlertConfig {
    @Id
    private String id;
    private String city;
    private double maxTempThreshold;
    private double minTempThreshold;
    private double maxHumidityThreshold;
    private int consecutiveReadings;
    private boolean emailEnabled;
    private String email;
}
