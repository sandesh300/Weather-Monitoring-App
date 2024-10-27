package com.weather_app.service;


import com.weather_app.dto.AlertConfigurationDTO;
import com.weather_app.model.AlertConfiguration;
import com.weather_app.repository.AlertConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlertConfigurationService {

    private final AlertConfigurationRepository configRepository;

    public AlertConfiguration createAlertConfiguration(AlertConfigurationDTO configDTO) {
        AlertConfiguration config = new AlertConfiguration();
        config.setCity(configDTO.getCity());
        config.setParameter(configDTO.getParameter());
        config.setThreshold(configDTO.getThreshold());
        config.setCondition(configDTO.getCondition());
        config.setConsecutiveUpdates(configDTO.getConsecutiveUpdates());
        config.setEnabled(configDTO.isEnabled());
        config.setEmail(configDTO.getEmail());

        return configRepository.save(config);
    }

    public AlertConfiguration updateAlertConfiguration(String id, AlertConfigurationDTO configDTO) {
        return configRepository.findById(id).map(config -> {
            config.setParameter(configDTO.getParameter());
            config.setThreshold(configDTO.getThreshold());
            config.setCondition(configDTO.getCondition());
            config.setConsecutiveUpdates(configDTO.getConsecutiveUpdates());
            config.setEnabled(configDTO.isEnabled());
            config.setEmail(configDTO.getEmail());
            return configRepository.save(config);
        }).orElseThrow(() -> new IllegalArgumentException("Alert configuration not found"));
    }

    public List<AlertConfiguration> getAlertConfigurations(String city) {
        return city != null ?
                configRepository.findByCityAndEnabledTrue(city) :
                configRepository.findByEnabledTrue();
    }

    public void deleteAlertConfiguration(String id) {
        configRepository.deleteById(id);
    }
}