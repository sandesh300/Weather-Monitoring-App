package com.weather_app.repository;

import com.weather_app.model.AlertConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertConfigRepository extends MongoRepository<AlertConfig, String> {
    List<AlertConfig> findByUserId(String userId);
    List<AlertConfig> findByCity(String city);
}