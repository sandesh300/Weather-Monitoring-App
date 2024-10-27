package com.weather_app.service;

import com.weather_app.dto.DailyWeatherSummaryDTO;
import com.weather_app.model.DailyWeatherSummary;
import com.weather_app.model.WeatherData;

import com.weather_app.repository.DailyWeatherSummaryRepository;
import com.weather_app.repository.WeatherDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DailyWeatherSummaryService {

    private final DailyWeatherSummaryRepository summaryRepository;
    private final WeatherDataRepository weatherDataRepository;

    private final List<String> MONITORED_CITIES = List.of(
            "Delhi", "Mumbai", "Chennai", "Bangalore", "Kolkata", "Hyderabad"
    );

    @Scheduled(cron = "0 5 0 * * *") // Run at 00:05 AM every day
    public void generateDailySummariesForPreviousDay() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        MONITORED_CITIES.forEach(city -> generateDailySummary(city, yesterday));
    }

    public DailyWeatherSummary generateDailySummary(String city, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<WeatherData> dailyData = weatherDataRepository
                .findByCityAndTimestampBetween(city, startOfDay, endOfDay);

        if (dailyData.isEmpty()) {
            log.warn("No weather data found for {} on {}", city, date);
            return null;
        }

        DailyWeatherSummaryDTO summaryDTO = calculateDailySummary(dailyData);
        return saveDailySummary(summaryDTO);
    }

    public DailyWeatherSummaryDTO calculateDailySummary(List<WeatherData> dailyData) {
        DoubleSummaryStatistics tempStats = dailyData.stream()
                .mapToDouble(WeatherData::getTemperature)
                .summaryStatistics();

        DoubleSummaryStatistics humidityStats = dailyData.stream()
                .mapToDouble(WeatherData::getHumidity)
                .summaryStatistics();

        DoubleSummaryStatistics windStats = dailyData.stream()
                .mapToDouble(WeatherData::getWindSpeed)
                .summaryStatistics();

        // Calculate weather condition frequencies
        Map<String, Integer> weatherConditionCounts = dailyData.stream()
                .collect(Collectors.groupingBy(
                        WeatherData::getMainCondition,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));

        // Determine dominant weather condition
        Map.Entry<String, Integer> dominantWeather = weatherConditionCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        String dominantCondition = dominantWeather != null ? dominantWeather.getKey() : "Unknown";
        String dominantReason = String.format(
                "Occurred %d times out of %d readings (%d%%)",
                dominantWeather != null ? dominantWeather.getValue() : 0,
                dailyData.size(),
                dominantWeather != null ? (dominantWeather.getValue() * 100 / dailyData.size()) : 0
        );

        return DailyWeatherSummaryDTO.builder()
                .city(dailyData.get(0).getCity())
                .date(dailyData.get(0).getTimestamp().toLocalDate())
                .avgTemperature(tempStats.getAverage())
                .maxTemperature(tempStats.getMax())
                .minTemperature(tempStats.getMin())
                .dominantWeatherCondition(dominantCondition)
                .dominantWeatherReason(dominantReason)
                .avgHumidity(humidityStats.getAverage())
                .avgWindSpeed(windStats.getAverage())
                .weatherConditionCounts(weatherConditionCounts)
                .totalReadings(dailyData.size())
                .build();
    }

    private DailyWeatherSummary saveDailySummary(DailyWeatherSummaryDTO summaryDTO) {
        DailyWeatherSummary summary = new DailyWeatherSummary();
        summary.setCity(summaryDTO.getCity());
        summary.setDate(summaryDTO.getDate());
        summary.setAvgTemperature(summaryDTO.getAvgTemperature());
        summary.setMaxTemperature(summaryDTO.getMaxTemperature());
        summary.setMinTemperature(summaryDTO.getMinTemperature());
        summary.setDominantWeatherCondition(summaryDTO.getDominantWeatherCondition());
        summary.setAvgHumidity(summaryDTO.getAvgHumidity());
        summary.setAvgWindSpeed(summaryDTO.getAvgWindSpeed());
        summary.setWeatherConditionCounts(summaryDTO.getWeatherConditionCounts());

        return summaryRepository.save(summary);
    }

    public List<DailyWeatherSummary> getCityWeatherHistory(String city, LocalDate startDate, LocalDate endDate) {
        return summaryRepository.findByCityAndDateBetween(city, startDate, endDate);
    }

    public Map<String, DailyWeatherSummary> getAllCitiesLatestSummary() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Map<String, DailyWeatherSummary> summaries = new HashMap<>();

        MONITORED_CITIES.forEach(city -> {
            summaryRepository.findByCityAndDate(city, yesterday)
                    .ifPresent(summary -> summaries.put(city, summary));
        });

        return summaries;
    }

    public List<DailyWeatherSummary> getHottestCitiesByDate(LocalDate date) {
        return summaryRepository.findByDateOrderByMaxTemperatureDesc(date);
    }
}
