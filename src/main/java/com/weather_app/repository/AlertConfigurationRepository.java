package com.weather_app.repository;


import com.weather_app.model.AlertConfiguration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertConfigurationRepository extends MongoRepository<AlertConfiguration, String> {
    List<AlertConfiguration> findByEnabledTrue();
    List<AlertConfiguration> findByCityAndEnabledTrue(String city);
}
