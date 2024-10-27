package com.weather_app.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class WeatherAlertDTO {
    private String city;
    private String alertType;
    private String message;
    private double threshold;
    private double currentValue;
    private LocalDateTime timestamp;
    private String email;
}
