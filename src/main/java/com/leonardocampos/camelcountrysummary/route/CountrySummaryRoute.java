package com.leonardocampos.camelcountrysummary.route;

import com.leonardocampos.camelcountrysummary.model.CountrySummary;
import com.leonardocampos.camelcountrysummary.model.ErrorResponse;
import com.leonardocampos.camelcountrysummary.processor.ExchangeRateResponseProcessor;
import com.leonardocampos.camelcountrysummary.processor.RestCountriesResponseProcessor;
import com.leonardocampos.camelcountrysummary.processor.WeatherResponseProcessor;
import com.leonardocampos.camelcountrysummary.strategy.CountrySummaryAggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CountrySummaryRoute extends RouteBuilder {

    private final RestCountriesResponseProcessor restCountriesProcessor;
    private final WeatherResponseProcessor weatherProcessor;
    private final ExchangeRateResponseProcessor exchangeRateProcessor;
    private final CountrySummaryAggregationStrategy aggregationStrategy;

    @Value("${api.openweathermap.key:}")
    private String weatherApiKey;

    @Value("${api.openweathermap.url:https://api.openweathermap.org/data/2.5}")
    private String weatherApiUrl;

    @Value("${api.restcountries.url:https://restcountries.com/v3.1}")
    private String restCountriesUrl;

    @Value("${api.exchangerate.url:https://api.exchangerate.host}")
    private String exchangeRateUrl;

    @Value("${api.exchangerate.key:}")
    private String exchangeRateApiKey;

    public CountrySummaryRoute(RestCountriesResponseProcessor restCountriesProcessor,
                               WeatherResponseProcessor weatherProcessor,
                               ExchangeRateResponseProcessor exchangeRateProcessor,
                               CountrySummaryAggregationStrategy aggregationStrategy) {
        this.restCountriesProcessor = restCountriesProcessor;
        this.weatherProcessor = weatherProcessor;
        this.exchangeRateProcessor = exchangeRateProcessor;
        this.aggregationStrategy = aggregationStrategy;
    }

    @Override
    public void configure() throws Exception {

        // Global exception handling
        onException(Exception.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .process(exchange -> exchange.getIn().setBody(
                        new ErrorResponse(500, "Internal Server Error",
                                "An unexpected error occurred while processing the request")))
                .marshal().json();

        // REST DSL configuration
        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")
                .contextPath("/api")
                .apiContextPath("/api-doc");

        // REST endpoint definition
        rest("/country-summary")
                .description("Country Summary API")
                .get()
                .description("Get an intelligent summary for a country")
                .param().name("name").type(RestParamType.query).required(true)
                    .description("Country name to search for").endParam()
                .produces("application/json")
                .to("direct:getCountrySummary");

        // Main orchestration route
        from("direct:getCountrySummary")
                .routeId("country-summary-main")
                .log("Received request for country: ${header.name}")
                .setHeader("countryName", header("name"))

                // Step 1: Call REST Countries API with Circuit Breaker
                .to("direct:fetchCountryInfo")

                // Step 2: Content-Based Router — check if country was found
                .choice()
                    .when(header("countryFound").isEqualTo(false))
                        .to("direct:countryNotFound")
                    .otherwise()
                        // Step 3: Multicast to Weather and Exchange Rate APIs in parallel
                        .to("direct:enrichWithExternalData")
                .end();

        // Route: Fetch country info from REST Countries API
        from("direct:fetchCountryInfo")
                .routeId("fetch-country-info")
                .circuitBreaker()
                    .resilience4jConfiguration()
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(30)
                        .slidingWindowSize(10)
                        .minimumNumberOfCalls(5)
                    .end()
                    .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                    .setBody(constant(null))
                    .toD(restCountriesUrl
                            + "/name/${header.countryName}?fields=name,capital,currencies,latlng"
                            + "&httpMethod=GET")
                    .process(restCountriesProcessor)
                .onFallback()
                    .setHeader("countryFound", constant(false))
                    .setHeader("fallbackReason", constant("REST Countries API is unavailable"))
                    .log("Circuit breaker fallback triggered for REST Countries API")
                .end();

        // Route: Country not found — Content-Based Router error handling
        from("direct:countryNotFound")
                .routeId("country-not-found")
                .log("Country not found: ${header.countryName}")
                .choice()
                    .when(header("fallbackReason").isNotNull())
                        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(503))
                        .process(exchange -> exchange.getIn().setBody(
                                new ErrorResponse(503, "Service Unavailable",
                                        "The REST Countries API is currently unavailable. Please try again later.")))
                    .otherwise()
                        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
                        .process(exchange -> exchange.getIn().setBody(
                                new ErrorResponse(404, "Not Found",
                                        "Country '" + exchange.getIn().getHeader("countryName", String.class)
                                                + "' was not found. Please check the spelling and try again.")))
                .end()
                .marshal().json();

        // Route: Enrich with external data using Multicast (parallel)
        from("direct:enrichWithExternalData")
                .routeId("enrich-external-data")
                .process(exchange -> exchange.getIn().setHeader("countrySummary",
                        exchange.getIn().getBody(CountrySummary.class)))
                .multicast(aggregationStrategy)
                    .parallelProcessing()
                    .timeout(10000)
                    .to("direct:fetchWeather", "direct:fetchExchangeRate")
                .end()
                .marshal().json();

        // Route: Fetch weather data with Circuit Breaker + Retry
        from("direct:fetchWeather")
                .routeId("fetch-weather")
                .log("Fetching weather for lat=${header.latitude}, lon=${header.longitude}")
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
                            + "/weather?lat=${header.latitude}&lon=${header.longitude}"
                            + "&appid=" + weatherApiKey
                            + "&units=metric"
                            + "&httpMethod=GET")
                    .process(weatherProcessor)
                .onFallback()
                    .log("Circuit breaker fallback triggered for Weather API")
                    .process(exchange -> {
                        CountrySummary.WeatherInfo weather = new CountrySummary.WeatherInfo();
                        weather.setError("Weather data is temporarily unavailable");
                        exchange.getIn().setHeader("dataType", "weather");
                        exchange.getIn().setBody(weather);
                    })
                .end();

        // Route: Fetch exchange rate data with Circuit Breaker + Retry
        from("direct:fetchExchangeRate")
                .routeId("fetch-exchange-rate")
                .log("Fetching exchange rates for currency=${header.currencyCode}")
                .circuitBreaker()
                    .resilience4jConfiguration()
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(20)
                        .slidingWindowSize(5)
                        .minimumNumberOfCalls(3)
                    .end()
                    .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                    .setBody(constant(null))
                    .toD(exchangeRateUrl
                            + "/latest?base=${header.currencyCode}"
                            + "&symbols=USD,EUR,GBP,JPY,BRL"
                            + "&access_key=" + exchangeRateApiKey
                            + "&httpMethod=GET")
                    .process(exchangeRateProcessor)
                .onFallback()
                    .log("Circuit breaker fallback triggered for Exchange Rate API")
                    .process(exchange -> {
                        CountrySummary.ExchangeRateInfo exchangeRate = new CountrySummary.ExchangeRateInfo();
                        exchangeRate.setError("Exchange rate data is temporarily unavailable");
                        exchange.getIn().setHeader("dataType", "exchangeRate");
                        exchange.getIn().setBody(exchangeRate);
                    })
                .end();
    }
}
