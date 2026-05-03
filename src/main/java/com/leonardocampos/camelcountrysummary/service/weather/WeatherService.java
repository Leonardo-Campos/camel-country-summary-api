package com.leonardocampos.camelcountrysummary.service.weather;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leonardocampos.camelcountrysummary.domain.model.WeatherInfo;
import com.leonardocampos.camelcountrysummary.integration.dto.weather.WeatherApiResponse;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    private final ObjectMapper objectMapper;

    public WeatherService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public WeatherInfo parseResponse(String json) throws Exception {
        WeatherApiResponse response = objectMapper.readValue(json, WeatherApiResponse.class);
        return mapToWeatherInfo(response);
    }

    private WeatherInfo mapToWeatherInfo(WeatherApiResponse response) {
        WeatherInfo weather = new WeatherInfo();

        if (response.getWeather() != null && !response.getWeather().isEmpty()) {
            weather.setDescription(response.getWeather().get(0).getDescription());
        }

        if (response.getMain() != null) {
            weather.setTemperature(response.getMain().getTemp());
            weather.setFeelsLike(response.getMain().getFeelsLike());
            weather.setHumidity(response.getMain().getHumidity());
        }

        if (response.getWind() != null) {
            weather.setWindSpeed(response.getWind().getSpeed());
        }

        return weather;
    }
}
