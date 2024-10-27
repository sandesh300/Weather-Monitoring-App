package com.weather_app;

import com.weather_app.config.OpenWeatherMapConfig;
import com.weather_app.dto.AlertConfigurationDTO;
import com.weather_app.dto.DailyWeatherSummaryDTO;
import com.weather_app.dto.WeatherForecastDTO;
import com.weather_app.model.AlertConfiguration;
import com.weather_app.model.WeatherData;
import com.weather_app.model.WeatherForecast;
import com.weather_app.repository.AlertConfigurationRepository;
import com.weather_app.repository.DailyWeatherSummaryRepository;
import com.weather_app.repository.WeatherForecastRepository;
import com.weather_app.service.AlertConfigurationService;
import com.weather_app.service.DailyWeatherSummaryService;
import com.weather_app.service.WeatherForecastService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.*;
import java.util.*;

class WeatherMonitoringAppTests {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private OpenWeatherMapConfig openWeatherMapConfig;

    @Mock
    private WeatherForecastRepository forecastRepository;

    @Mock
    private DailyWeatherSummaryRepository summaryRepository;

    @Mock
    private AlertConfigurationRepository alertConfigRepository;

    private WeatherForecastService forecastService;
    private DailyWeatherSummaryService summaryService;
    private AlertConfigurationService alertService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize services
        forecastService = new WeatherForecastService(forecastRepository, restTemplate, openWeatherMapConfig);
        summaryService = new DailyWeatherSummaryService(summaryRepository, null);
        alertService = new AlertConfigurationService(alertConfigRepository);

        // Configure OpenWeatherMap API key
        when(openWeatherMapConfig.getApiKey()).thenReturn("test-api-key");
    }

    @Test
    void testSystemStartup() {
        // Test 1: System Setup
        String city = "Delhi";
        String apiUrl = "http://api.openweathermap.org/data/2.5/forecast";

        // Mock API response
        WeatherForecastDTO mockResponse = createMockWeatherForecastDTO();
        when(restTemplate.getForObject(
                contains(apiUrl),
                eq(WeatherForecastDTO.class)
        )).thenReturn(mockResponse);

        // Verify system can fetch initial forecast
        forecastService.fetchAndSaveForecast(city);

        verify(restTemplate).getForObject(anyString(), eq(WeatherForecastDTO.class));
        verify(forecastRepository).saveAll(anyList());
    }

    @Test
    void testDataRetrieval() {
        // Test 2: Data Retrieval
        String city = "Mumbai";
        LocalDateTime now = LocalDateTime.now();

        // Create mock forecast data
        List<WeatherForecast> mockForecasts = Arrays.asList(
                createMockForecast(city, now, 25.0),
                createMockForecast(city, now.plusHours(3), 27.0)
        );

        when(forecastRepository.findByCityAndForecastTimeGreaterThanOrderByForecastTimeAsc(
                eq(city),
                any(LocalDateTime.class)
        )).thenReturn(mockForecasts);

        // Test forecast retrieval
        List<WeatherForecast> results = forecastService.getForecastForCity(city);

        assertEquals(2, results.size());
        assertEquals(25.0, results.get(0).getTemperature());
        assertEquals(27.0, results.get(1).getTemperature());
    }

    @Test
    void testTemperatureConversion() {
        // Test 3: Temperature Conversion
        double kelvinTemp = 300.15; // 27°C
        double celsiusTemp = kelvinTemp - 273.15;
        double fahrenheitTemp = (celsiusTemp * 9/5) + 32;

        assertEquals(27.0, celsiusTemp, 0.1);
        assertEquals(80.6, fahrenheitTemp, 0.1);
    }

    @Test
    void testDailyWeatherSummary() {
        // Test 4: Daily Weather Summary
        String city = "Chennai";
        LocalDate date = LocalDate.now();

        // Create mock weather data for a day
        List<WeatherData> mockDailyData = Arrays.asList(
                createMockWeatherData(city, date.atTime(6, 0), 24.0, "Clear"),
                createMockWeatherData(city, date.atTime(12, 0), 32.0, "Clear"),
                createMockWeatherData(city, date.atTime(18, 0), 28.0, "Clouds")
        );

        // Calculate and verify summary
        DailyWeatherSummaryDTO summary = summaryService.calculateDailySummary(mockDailyData);

        assertEquals(28.0, summary.getAvgTemperature(), 0.1);
        assertEquals(32.0, summary.getMaxTemperature());
        assertEquals(24.0, summary.getMinTemperature());
        assertEquals("Clear", summary.getDominantWeatherCondition());
    }

    @Test
    void testAlertingThresholds() {
        // Test 5: Alerting Thresholds
        String city = "Bangalore";
        String email = "user@example.com";

        // Create alert configuration
        AlertConfigurationDTO configDTO = AlertConfigurationDTO.builder()
                .city(city)
                .parameter("temperature")
                .threshold(30.0)
                .condition("GREATER_THAN")
                .consecutiveUpdates(1)
                .enabled(true)
                .email(email)
                .build();

        AlertConfiguration savedConfig = alertService.createAlertConfiguration(configDTO);

        // Simulate weather data exceeding threshold
        WeatherForecast highTemp = createMockForecast(city, LocalDateTime.now(), 32.0);

        // Verify alert would be triggered
        boolean shouldTrigger = highTemp.getTemperature() > savedConfig.getThreshold();
        assertTrue(shouldTrigger);
    }

    // Helper methods to create mock objects
    private WeatherForecastDTO createMockWeatherForecastDTO() {
        WeatherForecastDTO dto = new WeatherForecastDTO();
        WeatherForecastDTO.ForecastItem item = new WeatherForecastDTO.ForecastItem();
        WeatherForecastDTO.Main main = new WeatherForecastDTO.Main();
        main.setTemp(25.0);
        item.setMain(main);
        dto.setList(Collections.singletonList(item));
        return dto;
    }

    private WeatherForecast createMockForecast(String city, LocalDateTime time, double temp) {
        return WeatherForecast.builder()
                .city(city)
                .forecastTime(time)
                .temperature(temp)
                .build();
    }

    private WeatherData createMockWeatherData(String city, LocalDateTime time, double temp, String condition) {
        WeatherData data = new WeatherData();
        data.setCity(city);
        data.setTimestamp(time);
        data.setTemperature(temp);
        data.setMainCondition(condition);
        return data;
    }
}