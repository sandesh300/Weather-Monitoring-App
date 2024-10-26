package com.weather_app.service;

import com.weather_app.model.DailySummary;
import com.weather_app.model.WeatherData;
import com.weather_app.repository.DailySummaryRepository;
import com.weather_app.repository.WeatherDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DailySummaryService {
    private final DailySummaryRepository dailySummaryRepository;
    private final WeatherDataRepository weatherDataRepository;

    // Run at midnight every day
    @Scheduled(cron = "0 0 0 * * *")
    public void generateDailySummaries() {
        log.info("Generating daily summaries...");
        LocalDate yesterday = LocalDate.now().minusDays(1);
        generateSummaryForDate(yesterday);
    }

    public void generateSummaryForDate(LocalDate date) {
        // Get start and end of the day in Instant
        Instant startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        // Get all weather data for the day
        List<WeatherData> weatherDataList = weatherDataRepository
                .findByTimestampBetween(startOfDay, endOfDay);

        // Group weather data by city
        Map<String, List<WeatherData>> weatherByCity = weatherDataList.stream()
                .collect(Collectors.groupingBy(WeatherData::getCity));

        // Generate summary for each city
        weatherByCity.forEach((city, cityWeatherData) -> {
            generateCitySummary(city, cityWeatherData, date);
        });
    }

    private void generateCitySummary(String city, List<WeatherData> weatherDataList, LocalDate date) {
        if (weatherDataList.isEmpty()) {
            return;
        }

        // Calculate temperature statistics
        DoubleSummaryStatistics tempStats = weatherDataList.stream()
                .mapToDouble(WeatherData::getTemperature)
                .summaryStatistics();

        // Calculate humidity statistics
        DoubleSummaryStatistics humidityStats = weatherDataList.stream()
                .mapToDouble(WeatherData::getHumidity)
                .summaryStatistics();

        // Calculate wind speed statistics
        DoubleSummaryStatistics windStats = weatherDataList.stream()
                .mapToDouble(WeatherData::getWindSpeed)
                .summaryStatistics();

        // Calculate pressure statistics
        DoubleSummaryStatistics pressureStats = weatherDataList.stream()
                .mapToDouble(WeatherData::getPressure)
                .summaryStatistics();

        // Count weather conditions
        Map<String, Integer> conditionCounts = weatherDataList.stream()
                .collect(Collectors.groupingBy(
                        WeatherData::getMainCondition,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));

        // Find dominant weather condition
        String dominantCondition = conditionCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown");

        // Create or update daily summary
        DailySummary summary = dailySummaryRepository
                .findByCityAndDate(city, date)
                .orElse(new DailySummary());

        // Set summary data
        summary.setCity(city);
        summary.setDate(date);
        summary.setAvgTemperature(tempStats.getAverage());
        summary.setMaxTemperature(tempStats.getMax());
        summary.setMinTemperature(tempStats.getMin());
        summary.setDominantWeatherCondition(dominantCondition);
        summary.setAvgHumidity(humidityStats.getAverage());
        summary.setAvgWindSpeed(windStats.getAverage());
        summary.setAvgPressure(pressureStats.getAverage());
        summary.setWeatherConditionCounts(conditionCounts);
        summary.setUserId(weatherDataList.get(0).getUserId());

        // Save summary
        dailySummaryRepository.save(summary);
        log.info("Generated daily summary for {} on {}", city, date);
    }

    public List<com.weather_app.dto.DailySummary> getDailySummaries(String city, LocalDate startDate, LocalDate endDate) {
        return dailySummaryRepository.findByCityAndDateBetween(city, startDate, endDate);
    }

    public List<com.weather_app.dto.DailySummary> getUserDailySummaries(String userId, LocalDate startDate, LocalDate endDate) {
        return dailySummaryRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
    }
}