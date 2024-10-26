package com.weather_app.dto;

import lombok.Data;
import java.util.List;

@Data
public class OpenWeatherResponse {

    private WeatherMain main;
    private List<Weather> weather;
    private Wind wind;
    private long dt;
    private String name;

    // Nested static class for WeatherMain
    @Data
    public static class WeatherMain {
        private double temp;
        private double feels_like;
        private double pressure;
        private double humidity;
    }

    // Nested static class for Weather
    @Data
    public static class Weather {
        private String main;
        private String description;
    }

    // Nested static class for Wind
    @Data
    public static class Wind {
        private double speed;
    }

    @Data
    public static class WeatherDataDTO {
        private String city;
        private String mainCondition;
        private double temperature;
        private double feelsLike;
        private double humidity;
        private double windSpeed;
        private double pressure;
        private String timestamp;
    }
}
