package com.leonardocampos.camelcountrysummary.service.weather;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leonardocampos.camelcountrysummary.domain.model.WeatherInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WeatherServiceTest {

    private WeatherService service;

    @BeforeEach
    void setUp() {
        service = new WeatherService(new ObjectMapper());
    }

    @Test
    void parseResponse_shouldExtractAllFields() throws Exception {
        String json = """
                {
                    "weather": [{"description": "clear sky"}],
                    "main": {"temp": 25.5, "feels_like": 24.0, "humidity": 60},
                    "wind": {"speed": 3.5}
                }
                """;

        WeatherInfo weather = service.parseResponse(json);

        assertEquals("clear sky", weather.getDescription());
        assertEquals(25.5, weather.getTemperature());
        assertEquals(24.0, weather.getFeelsLike());
        assertEquals(60, weather.getHumidity());
        assertEquals(3.5, weather.getWindSpeed());
    }

    @Test
    void parseResponse_shouldHandleMissingWeatherList() throws Exception {
        String json = """
                {
                    "main": {"temp": 25.5, "feels_like": 24.0, "humidity": 60},
                    "wind": {"speed": 3.5}
                }
                """;

        WeatherInfo weather = service.parseResponse(json);

        assertNull(weather.getDescription());
        assertEquals(25.5, weather.getTemperature());
    }

    @Test
    void parseResponse_shouldHandleEmptyWeatherList() throws Exception {
        String json = """
                {
                    "weather": [],
                    "main": {"temp": 20.0, "feels_like": 19.0, "humidity": 50},
                    "wind": {"speed": 2.0}
                }
                """;

        WeatherInfo weather = service.parseResponse(json);

        assertNull(weather.getDescription());
    }

    @Test
    void parseResponse_shouldHandleMissingMain() throws Exception {
        String json = """
                {
                    "weather": [{"description": "rain"}],
                    "wind": {"speed": 5.0}
                }
                """;

        WeatherInfo weather = service.parseResponse(json);

        assertEquals("rain", weather.getDescription());
        assertNull(weather.getTemperature());
        assertNull(weather.getFeelsLike());
        assertNull(weather.getHumidity());
        assertEquals(5.0, weather.getWindSpeed());
    }

    @Test
    void parseResponse_shouldHandleMissingWind() throws Exception {
        String json = """
                {
                    "weather": [{"description": "cloudy"}],
                    "main": {"temp": 15.0, "feels_like": 14.0, "humidity": 80}
                }
                """;

        WeatherInfo weather = service.parseResponse(json);

        assertNull(weather.getWindSpeed());
        assertEquals("cloudy", weather.getDescription());
    }

    @Test
    void parseResponse_shouldHandleMinimalResponse() throws Exception {
        String json = "{}";

        WeatherInfo weather = service.parseResponse(json);

        assertNull(weather.getDescription());
        assertNull(weather.getTemperature());
        assertNull(weather.getFeelsLike());
        assertNull(weather.getHumidity());
        assertNull(weather.getWindSpeed());
    }
}
