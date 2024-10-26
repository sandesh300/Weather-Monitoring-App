package com.weather_app;

import com.weather_app.dto.OpenWeatherResponse;
import com.weather_app.model.AlertConfig;
import com.weather_app.model.WeatherData;
import com.weather_app.service.AlertService;
import com.weather_app.repository.DailySummaryRepository;
import com.weather_app.repository.AlertConfigRepository;
import com.weather_app.repository.WeatherDataRepository;
import com.weather_app.service.DailySummaryService;
import com.weather_app.service.EmailService;
import com.weather_app.service.WeatherService;
import lombok.Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static javax.management.Query.eq;

import static jdk.internal.org.objectweb.asm.util.CheckClassAdapter.verify;
import static jdk.jfr.internal.jfc.model.Constraint.any;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherMonitoringAppTests {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private WeatherDataRepository weatherDataRepository;

    @Mock
    private DailySummaryRepository dailySummaryRepository;

    @Mock
    private AlertConfigRepository alertConfigRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private WeatherService weatherService;

    @InjectMocks
    private DailySummaryService dailySummaryService;

    @InjectMocks
    private AlertService alertService;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.base-url}")
    private String baseUrl;

    private static final String TEST_CITY = "London";
    private static final String TEST_USER_ID = "test-user";

    @Test
    @DisplayName("1. System Setup - Verify API Connection")
    void testSystemStartupAndApiConnection() {
        // Arrange
        String url = String.format("%s/weather?q=%s&appid=%s&units=metric", baseUrl, TEST_CITY, apiKey);
        OpenWeatherResponse mockResponse = createMockWeatherResponse();
        when(restTemplate.getForEntity(url, OpenWeatherResponse.class))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        // Act
        WeatherData result = weatherService.fetchAndSaveWeatherData(TEST_CITY, TEST_USER_ID);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_CITY, result.getCity());
        verify(weatherDataRepository).save(any(WeatherData.class));
    }

    @Test
    @DisplayName("2. Data Retrieval - Test Scheduled Data Fetching")
     void testScheduledDataRetrieval() {
        // Arrange
        String url = String.format("%s/weather?q=%s&appid=%s&units=metric", baseUrl, TEST_CITY, apiKey);
        OpenWeatherResponse mockResponse = createMockWeatherResponse();
        when(restTemplate.getForEntity(url, OpenWeatherResponse.class))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        // Act
        weatherService.fetchWeatherData();

        // Assert
        verify(restTemplate).getForEntity(contains("/weather"), eq(OpenWeatherResponse.class));
        verify(weatherDataRepository).save(any(WeatherData.class));
    }

    @Test
    @DisplayName("3. Temperature Conversion - Test Kelvin to Celsius Conversion")
    void testTemperatureConversion() {
        // Arrange
        double kelvinTemp = 295.15; // 22°C
        OpenWeatherResponse mockResponse = createMockWeatherResponse();
        mockResponse.getMain().setTemp(kelvinTemp);

        // Act
        WeatherData weatherData = weatherService.convertToWeatherData(mockResponse, TEST_USER_ID);

        // Assert
        assertEquals(22.0, weatherData.getTemperature(), 0.1);
    }

    @Test
    @DisplayName("4. Daily Summary - Test Summary Generation")
    void testDailySummaryGeneration() {
        // Arrange
        LocalDate testDate = LocalDate.now();
        List<WeatherData> mockWeatherData = createMockWeatherDataList();
        when(weatherDataRepository.findByTimestampBetween(any(Instant.class), any(Instant.class)))
                .thenReturn(mockWeatherData);

        // Act
        dailySummaryService.generateSummaryForDate(testDate);

        // Assert
        verify(dailySummaryRepository).save(argThat(summary -> {
            assertEquals(TEST_CITY, summary.getCity());
            assertEquals(25.0, summary.getMaxTemperature(), 0.1);
            assertEquals(20.0, summary.getMinTemperature(), 0.1);
            assertEquals(22.5, summary.getAvgTemperature(), 0.1);
            assertEquals("Sunny", summary.getDominantWeatherCondition());
            return true;
        }));
    }

    @Test
    @DisplayName("5. Alerting Thresholds - Test Alert Triggering")
    void testAlertThresholds() {
        // Arrange
        WeatherData weatherData = createHighTemperatureWeatherData();
        AlertConfig alertConfig = createAlertConfig();
        when(alertConfigRepository.findByCity(TEST_CITY))
                .thenReturn(Collections.singletonList(alertConfig));

        // Act
        alertService.processWeatherData(weatherData);

        // Assert
        verify(emailService).sendWeatherAlert(
                eq(alertConfig),
                eq(weatherData),
                eq("HIGH_TEMPERATURE")
        );
    }

    @Test
    @DisplayName("5.1 Alerting Thresholds - Test No Alert When Within Threshold")
    void testNoAlertWithinThreshold() {
        // Arrange
        WeatherData weatherData = createNormalTemperatureWeatherData();
        AlertConfig alertConfig = createAlertConfig();
        when(alertConfigRepository.findByCity(TEST_CITY))
                .thenReturn(Collections.singletonList(alertConfig));

        // Act
        alertService.processWeatherData(weatherData);

        // Assert
        verify(emailService, never()).sendWeatherAlert(any(), any(), any());
    }

    // Helper methods to create mock data
    private OpenWeatherResponse createMockWeatherResponse() {
        OpenWeatherResponse response = new OpenWeatherResponse();
        OpenWeatherResponse.Main main = new OpenWeatherResponse.Main();
        main.setTemp(22.0);
        main.setFeels_like(23.0);
        main.setHumidity(65.0);
        main.setPressure(1013.0);

        OpenWeatherResponse.Weather weather = new OpenWeatherResponse.Weather();
        weather.setMain("Sunny");

        OpenWeatherResponse.Wind wind = new OpenWeatherResponse.Wind();
        wind.setSpeed(5.0);

        response.setMain(main);
        response.setWeather(Collections.singletonList(weather));
        response.setWind(wind);
        response.setName(TEST_CITY);

        return response;
    }

    private List<WeatherData> createMockWeatherDataList() {
        List<WeatherData> weatherDataList = new ArrayList<>();
        double[] temperatures = {20.0, 22.0, 23.0, 25.0, 24.0};

        for (double temp : temperatures) {
            WeatherData data = new WeatherData();
            data.setCity(TEST_CITY);
            data.setTemperature(temp);
            data.setMainCondition("Sunny");
            data.setHumidity(65.0);
            data.setWindSpeed(5.0);
            data.setPressure(1013.0);
            data.setTimestamp(Instant.now());
            data.setUserId(TEST_USER_ID);
            weatherDataList.add(data);
        }

        return weatherDataList;
    }

    private WeatherData createHighTemperatureWeatherData() {
        WeatherData data = new WeatherData();
        data.setCity(TEST_CITY);
        data.setTemperature(35.0); // High temperature
        data.setMainCondition("Sunny");
        data.setHumidity(65.0);
        data.setWindSpeed(5.0);
        data.setPressure(1013.0);
        data.setTimestamp(Instant.now());
        data.setUserId(TEST_USER_ID);
        return data;
    }

    private WeatherData createNormalTemperatureWeatherData() {
        WeatherData data = new WeatherData();
        data.setCity(TEST_CITY);
        data.setTemperature(22.0); // Normal temperature
        data.setMainCondition("Sunny");
        data.setHumidity(65.0);
        data.setWindSpeed(5.0);
        data.setPressure(1013.0);
        data.setTimestamp(Instant.now());
        data.setUserId(TEST_USER_ID);
        return data;
    }

    private AlertConfig createAlertConfig() {
        AlertConfig config = new AlertConfig();
        config.setId("test-alert-config");
        config.setUserId(TEST_USER_ID);
        config.setCity(TEST_CITY);
        config.setMaxTempThreshold(30.0);
        config.setMinTempThreshold(10.0);
        config.setMaxHumidityThreshold(80.0);
        config.setConsecutiveReadings(1);
        config.setEmailEnabled(true);
        config.setEmail("test@example.com");
        return config;
    }
}