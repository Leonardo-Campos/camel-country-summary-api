package com.leonardocampos.camelcountrysummary.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leonardocampos.camelcountrysummary.model.CountrySummary;
import com.leonardocampos.camelcountrysummary.model.api.WeatherApiResponse;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    private final ObjectMapper objectMapper;

    public WeatherService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public CountrySummary.WeatherInfo parseResponse(String json) throws Exception {
        WeatherApiResponse response = objectMapper.readValue(json, WeatherApiResponse.class);
        return mapToWeatherInfo(response);
    }

    private CountrySummary.WeatherInfo mapToWeatherInfo(WeatherApiResponse response) {
        CountrySummary.WeatherInfo weather = new CountrySummary.WeatherInfo();

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
