package com.leonardocampos.camelcountrysummary.route;

import com.leonardocampos.camelcountrysummary.processor.WeatherFallbackProcessor;
import com.leonardocampos.camelcountrysummary.processor.WeatherResponseProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.leonardocampos.camelcountrysummary.config.RouteConstants.*;

@Component
public class WeatherRoute extends RouteBuilder {

    private final WeatherResponseProcessor weatherProcessor;
    private final WeatherFallbackProcessor weatherFallbackProcessor;

    @Value("${api.openweathermap.key:}")
    private String weatherApiKey;

    @Value("${api.openweathermap.url:https://api.openweathermap.org/data/2.5}")
    private String weatherApiUrl;

    public WeatherRoute(WeatherResponseProcessor weatherProcessor,
                        WeatherFallbackProcessor weatherFallbackProcessor) {
        this.weatherProcessor = weatherProcessor;
        this.weatherFallbackProcessor = weatherFallbackProcessor;
    }

    @Override
    public void configure() throws Exception {

        from(DIRECT_FETCH_WEATHER)
                .routeId("fetch-weather")
                .log("Fetching weather for lat=${header." + HEADER_LATITUDE + "}, lon=${header." + HEADER_LONGITUDE + "}")
                .circuitBreaker()
                    .resilience4jConfiguration()
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(20)
                        .slidingWindowSize(5)
                        .minimumNumberOfCalls(3)
                    .end()
                    .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                    .setBody(constant(null))
                    .toD(weatherApiUrl
                            + "/weather?lat=${header." + HEADER_LATITUDE + "}"
                            + "&lon=${header." + HEADER_LONGITUDE + "}"
                            + "&appid=" + weatherApiKey
                            + "&units=metric"
                            + "&httpMethod=GET")
                    .process(weatherProcessor)
                .onFallback()
                    .log("Circuit breaker fallback triggered for Weather API")
                    .process(weatherFallbackProcessor)
                .end();
    }
}
