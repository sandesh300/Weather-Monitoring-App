package com.weather_app.repository;

import com.weather_app.model.WeatherForecast;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WeatherForecastRepository extends MongoRepository<WeatherForecast, String> {
    List<WeatherForecast> findByCityAndForecastTimeBetween(String city, LocalDateTime start, LocalDateTime end);
    void deleteByCityAndForecastTimeBefore(String city, LocalDateTime time);
    List<WeatherForecast> findByCityAndForecastTimeGreaterThanOrderByForecastTimeAsc(String city, LocalDateTime time);
}
