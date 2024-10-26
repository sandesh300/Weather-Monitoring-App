package com.weather_app.service;

import com.weather_app.model.AlertConfig;
import com.weather_app.model.WeatherData;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void sendWeatherAlert(AlertConfig alertConfig, WeatherData weatherData, String alertType) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariable("city", weatherData.getCity());
            context.setVariable("temperature", String.format("%.1f°C", weatherData.getTemperature()));
            context.setVariable("humidity", String.format("%.1f%%", weatherData.getHumidity()));
            context.setVariable("windSpeed", String.format("%.1f m/s", weatherData.getWindSpeed()));
            context.setVariable("condition", weatherData.getMainCondition());
            context.setVariable("alertType", alertType);
            context.setVariable("threshold", getThresholdValue(alertConfig, alertType));

            String emailContent = templateEngine.process("weather-alert", context);

            helper.setTo(alertConfig.getEmail());
            helper.setSubject(String.format("Weather Alert for %s - %s", weatherData.getCity(), alertType));
            helper.setText(emailContent, true);

            mailSender.send(message);
            log.info("Weather alert email sent to {} for {}", alertConfig.getEmail(), weatherData.getCity());

        } catch (MessagingException e) {
            log.error("Failed to send weather alert email", e);
        }
    }

    @Async
    public void sendDailySummary(String email, String city, String summaryHtml) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Daily Weather Summary for " + city);
            helper.setText(summaryHtml, true);

            mailSender.send(message);
            log.info("Daily summary email sent to {} for {}", email, city);

        } catch (MessagingException e) {
            log.error("Failed to send daily summary email", e);
        }
    }

    private String getThresholdValue(AlertConfig config, String alertType) {
        return switch (alertType) {
            case "HIGH_TEMPERATURE" -> String.format("%.1f°C", config.getMaxTempThreshold());
            case "LOW_TEMPERATURE" -> String.format("%.1f°C", config.getMinTempThreshold());
            case "HIGH_HUMIDITY" -> String.format("%.1f%%", config.getMaxHumidityThreshold());
            default -> "N/A";
        };
    }
}