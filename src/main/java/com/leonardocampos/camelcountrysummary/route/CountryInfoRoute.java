package com.leonardocampos.camelcountrysummary.route;

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
                            + "&httpMethod=GET")
                    .process(restCountriesProcessor)
                .end();
    }
}
