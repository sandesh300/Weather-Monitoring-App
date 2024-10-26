package com.weather_app.service;

import com.weather_app.dto.OpenWeatherResponse;
import com.weather_app.dto.WeatherResponse;
import com.weather_app.model.WeatherData;
import com.weather_app.repository.WeatherDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {
    private final WeatherDataRepository weatherDataRepository;
    private final RestTemplate restTemplate;


    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.base-url}")
    private String baseUrl;

    @Scheduled(fixedRateString = "${weather.scheduler.interval}")
    public void fetchWeatherData() {
        log.info("Fetching weather data for all cities...");
        // Implementation will follow in next part
    }

    public WeatherData fetchAndSaveWeatherData(String city, String userId) {
        String url = String.format("%s/weather?q=%s&appid=%s&units=metric", baseUrl, city, apiKey);

        ResponseEntity<OpenWeatherResponse> response =
                restTemplate.getForEntity(url, OpenWeatherResponse.class);

        if (response.getBody() != null) {
            OpenWeatherResponse weatherResponse = response.getBody();
            WeatherData weatherData = convertToWeatherData(weatherResponse, userId);
            return weatherDataRepository.save(weatherData);
        }

        return null;
    }

    private WeatherData convertToWeatherData(OpenWeatherResponse response, String userId) {
        WeatherData weatherData = new WeatherData();
        weatherData.setCity(response.getName());
        weatherData.setMainCondition(response.getWeather().get(0).getMain());
        weatherData.setTemperature(response.getMain().getTemp());
        weatherData.setFeelsLike(response.getMain().getFeels_like());
        weatherData.setHumidity(response.getMain().getHumidity());
        weatherData.setWindSpeed(response.getWind().getSpeed());
        weatherData.setPressure(response.getMain().getPressure());
        weatherData.setTimestamp(Instant.now());
        weatherData.setUserId(userId);
        return weatherData;
    }

    public List<OpenWeatherResponse.WeatherDataDTO> getWeatherData(String city, Instant startTime, Instant endTime) {
        List<WeatherData> weatherDataList =
                weatherDataRepository.findByCityAndTimestampBetween(city, startTime, endTime);

        return weatherDataList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private OpenWeatherResponse.WeatherDataDTO convertToDTO(WeatherData weatherData) {
        OpenWeatherResponse.WeatherDataDTO dto = new OpenWeatherResponse.WeatherDataDTO();
        dto.setCity(weatherData.getCity());
        dto.setMainCondition(weatherData.getMainCondition());
        dto.setTemperature(weatherData.getTemperature());
        dto.setFeelsLike(weatherData.getFeelsLike());
        dto.setHumidity(weatherData.getHumidity());
        dto.setWindSpeed(weatherData.getWindSpeed());
        dto.setPressure(weatherData.getPressure());
        dto.setTimestamp(weatherData.getTimestamp().toString());  // Ensure timestamp is properly formatted as a string
        return dto;
    }

}