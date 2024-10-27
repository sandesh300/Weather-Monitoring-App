package com.weather_app.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class AlertConfigurationDTO {
    private String city;
    private String parameter;
    private double threshold;
    private String condition;
    private int consecutiveUpdates;
    private boolean enabled;
    private String email;
}