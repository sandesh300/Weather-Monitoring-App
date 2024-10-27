package com.weather_app.repository;

import com.weather_app.model.DailyWeatherSummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyWeatherSummaryRepository extends MongoRepository<DailyWeatherSummary, String> {
    Optional<DailyWeatherSummary> findByCityAndDate(String city, LocalDate date);
    List<DailyWeatherSummary> findByCityAndDateBetween(String city, LocalDate startDate, LocalDate endDate);
    List<DailyWeatherSummary> findByDateOrderByMaxTemperatureDesc(LocalDate date);
}
