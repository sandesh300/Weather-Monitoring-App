package com.weather_app.repository;

import com.weather_app.model.DailySummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailySummaryRepository extends MongoRepository<DailySummary, String> {
    Optional<DailySummary> findByCityAndDate(String city, LocalDate date);
    List<com.weather_app.dto.DailySummary> findByCityAndDateBetween(String city, LocalDate startDate, LocalDate endDate);
    List<com.weather_app.dto.DailySummary> findByUserIdAndDateBetween(String userId, LocalDate startDate, LocalDate endDate);
    List<DailySummary> findByDateBetween(LocalDate startDate, LocalDate endDate);
}