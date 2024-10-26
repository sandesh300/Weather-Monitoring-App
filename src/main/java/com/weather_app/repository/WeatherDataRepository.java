package com.weather_app.repository;

import com.weather_app.model.WeatherData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;

@Repository
public interface WeatherDataRepository extends MongoRepository<WeatherData, String> {
    List<WeatherData> findByCityAndTimestampBetween(String city, Instant start, Instant end);
    List<WeatherData> findByTimestampBetween(Instant start, Instant end);
}
