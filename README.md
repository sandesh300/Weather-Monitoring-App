# Real-Time Weather Monitoring and Summarization System

A Java-based real-time data processing system for monitoring weather conditions across major metro cities in India, with summarized insights through daily rollups and configurable alerts. This system continuously fetches data from the OpenWeatherMap API and provides aggregate metrics and alert notifications based on user-defined thresholds.

## Features

- **Real-Time Weather Monitoring**: Continuously retrieves weather data at a configurable interval (default every 5 minutes) from the OpenWeatherMap API for Indian metro cities.
- **Temperature Conversion**: Converts temperature values from Kelvin to Celsius (default) or Fahrenheit based on user preference.
- **Daily Weather Summary**: Provides a daily summary, including:
  - Average, maximum, and minimum temperatures.
  - Dominant weather condition based on frequency (e.g., “Rain” if most updates report rain).
- **Configurable Alerts**: Alerts for weather conditions breaching user-defined thresholds (e.g., temperature exceeding 35°C).
- **Data Storage**: Stores daily summaries and historical data in MongoDB Atlas.
- **Visualizations**: Displays daily summaries, historical trends, and triggered alerts (React frontend).
- **Bonus**: Supports additional weather parameters (e.g., humidity, wind speed) in rollups.

---

## Tech Stack

- **Backend**: Java, Spring Boot
- **Frontend**: React
- **Database**: MongoDB Atlas
- **External API**: OpenWeatherMap API

---

## Setup Instructions

### 1. Clone Repository
```bash
git clone https://github.com/your-repo/weather-monitoring-system.git
cd weather-monitoring-system
```

### 2. API Key for OpenWeatherMap
Sign up for a free API key from [OpenWeatherMap](https://openweathermap.org/) and add it to your environment variables:
```bash
export OPENWEATHER_API_KEY=your_api_key
```

### 3. Database Setup
 Run the following to start MongoDB Atlas:
 - Add database configuration in application.properties file 
```# MongoDB Configuration
spring.data.mongodb.uri={Your MongoDB URL}
spring.data.mongodb.database=weather_monitoring
```

### 4. Run the Backend
Navigate to the backend directory and start the Spring Boot server:
```bash
cd backend
./mvnw spring-boot:run
```

### 5. Run the Frontend
Navigate to the frontend directory and start the React server:
```bash
cd frontend
npm install
npm start
```

---

## Configuration

### 1. Environment Variables
Configure the following in the `.env` file or directly in your environment:
- `OPENWEATHER_API_KEY`: API key from OpenWeatherMap.
- `DB_URL`: MongoDB Atlas connection URL.

### 2. Interval Configuration
In `application.properties`, set the interval for weather data retrieval (default: 5 minutes).
```properties
weather.poll.interval=300000  # milliseconds
```

---
# Weather API Documentation 
### API Documentation - https://walnut-wrist-9da.notion.site/Real-Time-Data-Processing-System-for-Weather-Monitoring-API-Documentation-12ba272bc3e58074b84cd1edd31295b9



## Weather Module
- **Get Current Weather**: `GET /api/weather/{city}`
- **Get Historical Weather Data**: `GET /api/weather/{city}/history`

---

## Alert Module
- **Create Alert**: `POST /api/alerts`
- **Get User Alerts**: `GET /api/alerts`
- **Delete Alert**: `DELETE /api/alerts/{id}`
- **Simulate Weather Event**: `POST /api/alerts/simulate`

---

## Daily Summary Module
- **Get City Daily Summaries**: `GET /api/summaries/{city}`
- **Get User Daily Summaries**: `GET /api/summaries/user`
- **Generate Daily Summary**: `POST /api/summaries/generate`


## Application Modules

### 1. Weather Monitoring
- **Retrieves data** at intervals from the OpenWeatherMap API.
- Parses and converts temperature data (Kelvin to Celsius/Fahrenheit).
  
### 2. Rollups and Aggregates
- **Daily Summaries**: Calculates daily averages, maximums, minimums, and dominant weather conditions based on frequency.
- **Alerts**: Configurable thresholds for temperature and weather conditions. Alerts triggered on console or as email notifications when thresholds are breached.

### 3. Visualization
Displays daily summaries, historical trends, and alerts using React charts and components.

---

## Testing
### Test Cases Documentation - https://walnut-wrist-9da.notion.site/Real-Time-Data-Processing-System-for-Weather-Monitoring-Test-Cases-Documentation-12ba272bc3e5808aba3ae74cd9033592
### Test Cases

1. **System Setup**: Validate connection to OpenWeatherMap API.
2. **Data Retrieval**: Simulate API calls at configurable intervals and parse responses.
3. **Temperature Conversion**: Validate Kelvin to Celsius/Fahrenheit conversion.
4. **Daily Summary Calculation**: Test rollup and aggregation functions for accuracy.
5. **Alerting Thresholds**: Test alerts for temperature/weather condition breaches.

### Running Tests
Run the test suite:
```bash
./mvnw test
```

---

## Dependencies

- **Backend**:
  - Java 11 or above
  - Spring Boot
  - MongoDB Atlas
- **Frontend**:
  - Node.js and npm
  - React.js
- **Database**:
  - MongoDB Atlas account

---

## Design Choices

- **Real-Time Processing**: Chose Spring Boot for efficient background data fetching and processing.
- **Storage**: MongoDB Atlas for its scalability and simplicity in handling JSON-like data (weather data is unstructured).
- **Frontend**: React.js provides a dynamic interface for displaying real-time weather data and historical trends.
  
---

