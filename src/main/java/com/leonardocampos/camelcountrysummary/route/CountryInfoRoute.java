package com.leonardocampos.camelcountrysummary.route;

import com.leonardocampos.camelcountrysummary.processor.CircuitBreakerFallbackProcessor;
import com.leonardocampos.camelcountrysummary.processor.CountryNotFoundThrowProcessor;
import com.leonardocampos.camelcountrysummary.processor.HttpStatusValidationProcessor;
import com.leonardocampos.camelcountrysummary.processor.RestCountriesResponseProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.leonardocampos.camelcountrysummary.config.RouteConstants.DIRECT_FETCH_COUNTRY;
import static com.leonardocampos.camelcountrysummary.config.RouteConstants.HEADER_COUNTRY_NAME;

@Component
public class CountryInfoRoute extends RouteBuilder {

    private final RestCountriesResponseProcessor restCountriesProcessor;
    private final HttpStatusValidationProcessor httpStatusValidationProcessor;
    private final CountryNotFoundThrowProcessor countryNotFoundThrowProcessor;

    @Value("${api.restcountries.url:https://restcountries.com/v3.1}")
    private String restCountriesUrl;

    public CountryInfoRoute(RestCountriesResponseProcessor restCountriesProcessor,
                            HttpStatusValidationProcessor httpStatusValidationProcessor,
                            CountryNotFoundThrowProcessor countryNotFoundThrowProcessor) {
        this.restCountriesProcessor = restCountriesProcessor;
        this.httpStatusValidationProcessor = httpStatusValidationProcessor;
        this.countryNotFoundThrowProcessor = countryNotFoundThrowProcessor;
    }

    @Override
    public void configure() throws Exception {

        from(DIRECT_FETCH_COUNTRY)
                .routeId("fetch-country-info")
                .log("Fetching country info for: ${header." + HEADER_COUNTRY_NAME + "}")
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
                            + "/name/${header." + HEADER_COUNTRY_NAME + "}"
                            + "?fields=name,capital,currencies,latlng"
                            + "&httpMethod=GET"
                            + "&throwExceptionOnFailureStatusCode=false")
                    .process(httpStatusValidationProcessor)
                .onFallback()
                    .log("Circuit breaker fallback triggered for REST Countries API")
                    .process(new CircuitBreakerFallbackProcessor(503, "Service Unavailable",
                            "The REST Countries API is temporarily unavailable. Please try again later."))
                    .marshal().json()
                .end()
                .choice()
                    .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(404))
                        .process(countryNotFoundThrowProcessor)
                    .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(503))
                        .stop()
                    .otherwise()
                        .process(restCountriesProcessor)
                .end();
    }
}
