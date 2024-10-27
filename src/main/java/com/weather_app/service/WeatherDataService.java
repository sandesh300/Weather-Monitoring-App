package com.weather_app.service;

import com.weather_app.config.OpenWeatherMapConfig;
import com.weather_app.dto.OpenWeatherMapResponse;
import com.weather_app.model.WeatherData;
import com.weather_app.repository.WeatherDataRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class WeatherDataService {

    @Autowired
    private WeatherDataRepository weatherDataRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OpenWeatherMapConfig openWeatherMapConfig;

    private final List<String> MONITORED_CITIES = List.of(
            "Delhi", "Mumbai", "Chennai", "Bangalore", "Kolkata", "Hyderabad"
    );

    @Scheduled(fixedRateString = "${weather.update.interval}")
    public void fetchWeatherDataForAllCities() {
        for (String city : MONITORED_CITIES) {
            try {
                WeatherData weatherData = fetchWeatherDataForCity(city);
                weatherDataRepository.save(weatherData);
            } catch (Exception e) {
                // Log error and continue with next city
                System.err.println("Error fetching weather data for " + city + ": " + e.getMessage());
            }
        }
    }

    public WeatherData fetchWeatherDataForCity(String city) {
        String url = String.format("%s?q=%s&appid=%s&units=metric",
                openWeatherMapConfig.getApiUrl(),
                city,
                openWeatherMapConfig.getApiKey()
        );

        OpenWeatherMapResponse response = restTemplate.getForObject(url, OpenWeatherMapResponse.class);

        // Log the response for debugging
        System.out.println("Weather API response for " + city + ": " + response);

        return convertToWeatherData(response, city);
    }




    private WeatherData convertToWeatherData(OpenWeatherMapResponse response, String city) {
        WeatherData weatherData = new WeatherData();
        weatherData.setCity(city);
        weatherData.setTimestamp(LocalDateTime.now());
        weatherData.setTemperature(response.getMain().getTemp());
        weatherData.setFeelsLike(response.getMain().getFeelsLike());
        weatherData.setHumidity(response.getMain().getHumidity());
        weatherData.setWindSpeed(response.getWind().getSpeed());
        weatherData.setMainCondition(response.getWeather().get(0).getMain());
        return weatherData;
    }

    public List<WeatherData> getRecentWeatherData(String city, LocalDateTime startTime, LocalDateTime endTime) {
        return weatherDataRepository.findByCityAndTimestampBetween(city, startTime, endTime);
    }

    public List<WeatherData> getLatestWeatherDataForCity(String city) {
        return weatherDataRepository.findTop2ByCityOrderByTimestampDesc(city);
    }

    public List<WeatherData> getAllCitiesLatestWeather() {
        List<WeatherData> latestWeatherList = new ArrayList<>();
        for (String city : MONITORED_CITIES) {
            weatherDataRepository.findTop2ByCityOrderByTimestampDesc(city)
                    .stream()
                    .findFirst()
                    .ifPresent(latestWeatherList::add);
        }
        return latestWeatherList;
    }
}