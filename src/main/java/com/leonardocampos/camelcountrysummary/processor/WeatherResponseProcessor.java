package com.leonardocampos.camelcountrysummary.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leonardocampos.camelcountrysummary.model.CountrySummary;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import static com.leonardocampos.camelcountrysummary.config.RouteConstants.DATA_TYPE_WEATHER;
import static com.leonardocampos.camelcountrysummary.config.RouteConstants.HEADER_DATA_TYPE;

@Component
public class WeatherResponseProcessor implements Processor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void process(Exchange exchange) throws Exception {
        String body = exchange.getIn().getBody(String.class);
        JsonNode root = objectMapper.readTree(body);

        CountrySummary.WeatherInfo weather = new CountrySummary.WeatherInfo();

        JsonNode weatherArray = root.path("weather");
        if (weatherArray.isArray() && !weatherArray.isEmpty()) {
            weather.setDescription(weatherArray.get(0).path("description").asText());
        }

        JsonNode main = root.path("main");
        if (!main.isMissingNode()) {
            weather.setTemperature(main.path("temp").asDouble());
            weather.setFeelsLike(main.path("feels_like").asDouble());
            weather.setHumidity(main.path("humidity").asInt());
        }

        JsonNode wind = root.path("wind");
        if (!wind.isMissingNode()) {
            weather.setWindSpeed(wind.path("speed").asDouble());
        }

        exchange.getIn().setHeader(HEADER_DATA_TYPE, DATA_TYPE_WEATHER);
        exchange.getIn().setBody(weather);
    }
}
