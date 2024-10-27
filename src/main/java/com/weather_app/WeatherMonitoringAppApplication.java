package com.weather_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WeatherMonitoringAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherMonitoringAppApplication.class, args);
	}

}
