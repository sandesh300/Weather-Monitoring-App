package com.weather_app.repository;

import com.weather_app.model.WeatherData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WeatherDataRepository extends MongoRepository<WeatherData, String> {
    List<WeatherData> findByCity(String city);
    List<WeatherData> findByCityAndTimestampBetween(String city, LocalDateTime start, LocalDateTime end);

    @Query("{'city': ?0, 'timestamp': {'$gte': ?1, '$lte': ?2}}")
    List<WeatherData> findWeatherDataForDailySummary(String city, LocalDateTime startOfDay, LocalDateTime endOfDay);

    List<WeatherData> findTop2ByCityOrderByTimestampDesc(String city);
}