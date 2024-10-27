package com.weather_app.service;

import com.weather_app.config.OpenWeatherMapConfig;
import com.weather_app.dto.WeatherForecastDTO;
import com.weather_app.model.WeatherForecast;
import com.weather_app.repository.WeatherForecastRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherForecastService {

    private final WeatherForecastRepository forecastRepository;
    private final RestTemplate restTemplate;
    private final OpenWeatherMapConfig openWeatherMapConfig;

    @Value("${openweathermap.forecast.url}")
    private String forecastUrl;

    private final List<String> MONITORED_CITIES = List.of(
            "Delhi", "Mumbai", "Chennai", "Bangalore", "Kolkata", "Hyderabad"
    );

    @Scheduled(fixedRate = 3600000) // Update forecasts every hour
    public void updateForecasts() {
        MONITORED_CITIES.forEach(this::fetchAndSaveForecast);
        cleanupOldForecasts();
    }

    public void fetchAndSaveForecast(String city) {
        try {
            String url = String.format("%s?q=%s&appid=%s&units=metric",
                    forecastUrl,
                    city,
                    openWeatherMapConfig.getApiKey()
            );

            WeatherForecastDTO forecast = restTemplate.getForObject(url, WeatherForecastDTO.class);
            if (forecast != null && forecast.getList() != null) {
                List<WeatherForecast> forecasts = convertToWeatherForecasts(forecast, city);
                forecastRepository.saveAll(forecasts);
                log.info("Updated forecast for {}, {} entries saved", city, forecasts.size());
            }
        } catch (Exception e) {
            log.error("Error fetching forecast for {}: {}", city, e.getMessage());
        }
    }

    private List<WeatherForecast> convertToWeatherForecasts(WeatherForecastDTO forecastDTO, String city) {
        LocalDateTime now = LocalDateTime.now();
        return forecastDTO.getList().stream()
                .map(item -> WeatherForecast.builder()
                        .city(city)
                        .forecastTime(LocalDateTime.ofInstant(
                                Instant.ofEpochSecond(item.getDt()),
                                ZoneId.systemDefault()))
                        .temperature(item.getMain().getTemp())
                        .feelsLike(item.getMain().getFeels_like())
                        .mainCondition(item.getWeather().get(0).getMain())
                        .description(item.getWeather().get(0).getDescription())
                        .humidity(item.getMain().getHumidity())
                        .windSpeed(item.getWind().getSpeed())
                        .cloudCover(item.getClouds().getAll())
                        .rainProbability(item.getPop() * 100)
                        .retrievalTime(now)
                        .build())
                .collect(Collectors.toList());
    }

    @Scheduled(cron = "0 0 0 * * *") // Run at midnight
    public void cleanupOldForecasts() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(1);
        MONITORED_CITIES.forEach(city ->
                forecastRepository.deleteByCityAndForecastTimeBefore(city, cutoff)
        );
    }

    public List<WeatherForecast> getForecastForCity(String city) {
        return forecastRepository.findByCityAndForecastTimeGreaterThanOrderByForecastTimeAsc(
                city,
                LocalDateTime.now()
        );
    }

    public List<WeatherForecast> getForecastForCityInRange(
            String city,
            LocalDateTime startTime,
            LocalDateTime endTime) {
        return forecastRepository.findByCityAndForecastTimeBetween(city, startTime, endTime);
    }

    public List<WeatherForecast> getAllCitiesLatestForecast() {
        List<WeatherForecast> forecasts = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        MONITORED_CITIES.forEach(city -> {
            List<WeatherForecast> cityForecasts = getForecastForCity(city);
            if (!cityForecasts.isEmpty()) {
                forecasts.add(cityForecasts.get(0));
            }
        });

        return forecasts;
    }
}