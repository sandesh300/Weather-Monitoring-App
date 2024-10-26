package com.weather_app.dto;

import lombok.Data;
import java.util.List;

@Data
public class WeatherResponse {
    private String city;
    private Current current;
    private List<Forecast> forecast;

    @Data
    public static class Current {
        private double temp;
        private double feelsLike;
        private int humidity;
        private double windSpeed;
        private int pressure;
        private String condition;
    }

    @Data
    public static class Forecast {
        private String date;
        private double tempMax;
        private double tempMin;
        private String condition;
    }
}