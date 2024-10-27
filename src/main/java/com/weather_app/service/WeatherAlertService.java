package com.weather_app.service;


import com.weather_app.model.AlertConfiguration;
import com.weather_app.model.WeatherAlert;
import com.weather_app.model.WeatherData;
import com.weather_app.repository.AlertConfigurationRepository;
import com.weather_app.repository.WeatherAlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherAlertService {

    private final WeatherAlertRepository alertRepository;
    private final AlertConfigurationRepository configRepository;
    private final JavaMailSender emailSender;

    // Cache for tracking consecutive alerts
    private final Map<String, List<Double>> consecutiveReadings = new ConcurrentHashMap<>();

    public void processWeatherData(WeatherData weatherData) {
        List<AlertConfiguration> configurations = configRepository.findByCityAndEnabledTrue(weatherData.getCity());

        for (AlertConfiguration config : configurations) {
            checkAndCreateAlert(config, weatherData);
        }
    }

    private void checkAndCreateAlert(AlertConfiguration config, WeatherData weatherData) {
        double currentValue = getCurrentValueForParameter(config.getParameter(), weatherData);
        String cacheKey = config.getId() + "-" + config.getParameter();

        List<Double> readings = consecutiveReadings.computeIfAbsent(cacheKey, k -> new ArrayList<>());
        readings.add(currentValue);

        // Keep only the required number of consecutive readings
        while (readings.size() > config.getConsecutiveUpdates()) {
            readings.remove(0);
        }

        boolean thresholdBreached = checkThresholdBreach(readings, config);

        if (thresholdBreached && readings.size() >= config.getConsecutiveUpdates()) {
            createAndSendAlert(config, currentValue, weatherData);
            readings.clear(); // Reset after alert
        }
    }

    private double getCurrentValueForParameter(String parameter, WeatherData weatherData) {
        return switch (parameter) {
            case "temperature" -> weatherData.getTemperature();
            case "humidity" -> weatherData.getHumidity();
            case "windSpeed" -> weatherData.getWindSpeed();
            default -> throw new IllegalArgumentException("Unsupported parameter: " + parameter);
        };
    }

    private boolean checkThresholdBreach(List<Double> readings, AlertConfiguration config) {
        if (readings.isEmpty()) return false;

        boolean allReadingsBreachThreshold = readings.stream().allMatch(value ->
                switch (config.getCondition()) {
                    case "GREATER_THAN" -> value > config.getThreshold();
                    case "LESS_THAN" -> value < config.getThreshold();
                    case "EQUALS" -> Math.abs(value - config.getThreshold()) < 0.01;
                    default -> false;
                }
        );

        return allReadingsBreachThreshold;
    }

    private void createAndSendAlert(AlertConfiguration config, double currentValue, WeatherData weatherData) {
        WeatherAlert alert = new WeatherAlert();
        alert.setCity(config.getCity());
        alert.setAlertType(config.getParameter());
        alert.setMessage(generateAlertMessage(config, currentValue));
        alert.setThreshold(config.getThreshold());
        alert.setCurrentValue(currentValue);
        alert.setTimestamp(LocalDateTime.now());
        alert.setActive(true);

        alertRepository.save(alert);
        sendAlertEmail(config.getEmail(), alert);
    }

    private String generateAlertMessage(AlertConfiguration config, double currentValue) {
        return String.format("Weather Alert for %s: %s is %s threshold of %.2f (Current value: %.2f)",
                config.getCity(),
                config.getParameter(),
                config.getCondition().toLowerCase(),
                config.getThreshold(),
                currentValue
        );
    }

    private void sendAlertEmail(String email, WeatherAlert alert) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Weather Alert - " + alert.getCity());
        message.setText(alert.getMessage());

        try {
            emailSender.send(message);
            log.info("Alert email sent successfully to {}", email);
        } catch (Exception e) {
            log.error("Failed to send alert email: {}", e.getMessage());
        }
    }

    public List<WeatherAlert> getActiveAlerts(String city) {
        return alertRepository.findByCityAndActive(city, true);
    }

    public List<WeatherAlert> getAlertHistory(String city, LocalDateTime startTime, LocalDateTime endTime) {
        return alertRepository.findByCityAndTimestampBetween(city, startTime, endTime);
    }

    public void deactivateAlert(String alertId) {
        alertRepository.findById(alertId).ifPresent(alert -> {
            alert.setActive(false);
            alertRepository.save(alert);
        });
    }
}
