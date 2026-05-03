package com.leonardocampos.camelcountrysummary.route;

import com.leonardocampos.camelcountrysummary.model.ErrorResponse;
import com.leonardocampos.camelcountrysummary.processor.RestCountriesResponseProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.leonardocampos.camelcountrysummary.config.RouteConstants.DIRECT_FETCH_COUNTRY;
import static com.leonardocampos.camelcountrysummary.config.RouteConstants.HEADER_COUNTRY_NAME;

@Component
public class CountryInfoRoute extends RouteBuilder {

    private final RestCountriesResponseProcessor restCountriesProcessor;

    @Value("${api.restcountries.url:https://restcountries.com/v3.1}")
    private String restCountriesUrl;

    public CountryInfoRoute(RestCountriesResponseProcessor restCountriesProcessor) {
        this.restCountriesProcessor = restCountriesProcessor;
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
                    .process(exchange -> {
                        Integer statusCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
                        if (statusCode != null && statusCode >= 500) {
                            throw new HttpOperationFailedException(
                                    restCountriesUrl, statusCode, "Server Error",
                                    null, null, exchange.getIn().getBody(String.class));
                        }
                    })
                .onFallback()
                    .log("Circuit breaker fallback triggered for REST Countries API")
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(503))
                    .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                    .process(exchange -> exchange.getIn().setBody(
                            new ErrorResponse(503, "Service Unavailable",
                                    "The REST Countries API is temporarily unavailable. Please try again later.")))
                    .marshal().json()
                .end()
                .choice()
                    .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(404))
                        .process(exchange -> {
                            String countryName = exchange.getIn().getHeader(HEADER_COUNTRY_NAME, String.class);
                            throw new HttpOperationFailedException(
                                    restCountriesUrl + "/name/" + countryName,
                                    404, "Not Found", null, null,
                                    exchange.getIn().getBody(String.class));
                        })
                    .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(503))
                        .stop()
                    .otherwise()
                        .process(restCountriesProcessor)
                .end();
    }
}
