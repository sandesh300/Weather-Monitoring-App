package com.weather_app.repository;


import com.weather_app.model.WeatherAlert;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WeatherAlertRepository extends MongoRepository<WeatherAlert, String> {
    List<WeatherAlert> findByCityAndActive(String city, boolean active);
    List<WeatherAlert> findByActiveAndTimestampAfter(boolean active, LocalDateTime timestamp);
    List<WeatherAlert> findByCityAndTimestampBetween(String city, LocalDateTime start, LocalDateTime end);
}
