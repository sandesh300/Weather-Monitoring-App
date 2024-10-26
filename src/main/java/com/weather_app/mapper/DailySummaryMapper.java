package com.weather_app.mapper;


import com.weather_app.model.DailySummary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DailySummaryMapper {

    public DailySummary toDTO(DailySummary summary) {
        DailySummary dto = new DailySummary();
        dto.setCity(summary.getCity());
        dto.setDate(summary.getDate());
        dto.setAvgTemperature(summary.getAvgTemperature());
        dto.setMaxTemperature(summary.getMaxTemperature());
        dto.setMinTemperature(summary.getMinTemperature());
        dto.setDominantWeatherCondition(summary.getDominantWeatherCondition());
        dto.setAvgHumidity(summary.getAvgHumidity());
        dto.setAvgWindSpeed(summary.getAvgWindSpeed());
        dto.setAvgPressure(summary.getAvgPressure());
        dto.setWeatherConditionCounts(summary.getWeatherConditionCounts());
        return dto;
    }

    public List<DailySummary> toDTOList(List<DailySummary> summaries) {
        return summaries.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public DailySummary toEntity(DailySummary dto) {
        DailySummary summary = new DailySummary();
        summary.setCity(dto.getCity());
        summary.setDate(dto.getDate());
        summary.setAvgTemperature(dto.getAvgTemperature());
        summary.setMaxTemperature(dto.getMaxTemperature());
        summary.setMinTemperature(dto.getMinTemperature());
        summary.setDominantWeatherCondition(dto.getDominantWeatherCondition());
        summary.setAvgHumidity(dto.getAvgHumidity());
        summary.setAvgWindSpeed(dto.getAvgWindSpeed());
        summary.setAvgPressure(dto.getAvgPressure());
        summary.setWeatherConditionCounts(dto.getWeatherConditionCounts());
        return summary;
    }
}