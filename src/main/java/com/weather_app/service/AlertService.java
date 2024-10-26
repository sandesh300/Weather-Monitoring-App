package com.weather_app.service;

import com.weather_app.model.AlertConfig;
import com.weather_app.model.WeatherData;
import com.weather_app.repository.AlertConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {
    private final AlertConfigRepository alertConfigRepository;
    private final EmailService emailService;
    private final Map<String, Map<String, Integer>> consecutiveReadingsCache = new ConcurrentHashMap<>();

    public AlertConfig saveAlertConfig(AlertConfig config) {
        return alertConfigRepository.save(config);
    }

    public List<AlertConfig> getCityAlerts(String city) {
        return alertConfigRepository.findByCity(city);
    }

    public void deleteAlert(String id) {
        alertConfigRepository.deleteById(id);
    }

    public void processWeatherData(WeatherData weatherData) {
        alertConfigRepository.findByCity(weatherData.getCity())
                .forEach(alertConfig -> checkThresholds(alertConfig, weatherData));
    }

    private void checkThresholds(AlertConfig config, WeatherData weatherData) {
        String cacheKey = config.getId() + "_" + config.getCity();
        Map<String, Integer> consecutiveCounts = consecutiveReadingsCache
                .computeIfAbsent(cacheKey, k -> new HashMap<>());

        // Check high temperature
        if (weatherData.getTemperature() > config.getMaxTempThreshold()) {
            handleConsecutiveReading(config, weatherData, "HIGH_TEMPERATURE", consecutiveCounts);
        } else {
            consecutiveCounts.remove("HIGH_TEMPERATURE");
        }

        // Check low temperature
        if (weatherData.getTemperature() < config.getMinTempThreshold()) {
            handleConsecutiveReading(config, weatherData, "LOW_TEMPERATURE", consecutiveCounts);
        } else {
            consecutiveCounts.remove("LOW_TEMPERATURE");
        }

        // Check humidity
        if (weatherData.getHumidity() > config.getMaxHumidityThreshold()) {
            handleConsecutiveReading(config, weatherData, "HIGH_HUMIDITY", consecutiveCounts);
        } else {
            consecutiveCounts.remove("HIGH_HUMIDITY");
        }
    }

    private void handleConsecutiveReading(
            AlertConfig config,
            WeatherData weatherData,
            String alertType,
            Map<String, Integer> consecutiveCounts) {

        int count = consecutiveCounts.getOrDefault(alertType, 0) + 1;
        consecutiveCounts.put(alertType, count);

        if (count >= config.getConsecutiveReadings() && config.isEmailEnabled()) {
            emailService.sendWeatherAlert(config, weatherData, alertType);
            consecutiveCounts.put(alertType, 0);
        }
    }

    public void simulateWeatherData(String city, double temp, double humidity, double windSpeed) {
        WeatherData weatherData = new WeatherData();
        weatherData.setCity(city);
        weatherData.setTemperature(temp);
        weatherData.setHumidity(humidity);
        weatherData.setWindSpeed(windSpeed);
        weatherData.setTimestamp(Instant.now());
        processWeatherData(weatherData);
    }
}
