package com.leonardocampos.camelcountrysummary.route;

import com.leonardocampos.camelcountrysummary.config.RouteConstants;
import com.leonardocampos.camelcountrysummary.domain.model.CountrySummary;
import com.leonardocampos.camelcountrysummary.processor.error.CountryNotFoundProcessor;
import com.leonardocampos.camelcountrysummary.processor.error.GenericErrorProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.stereotype.Component;

@Component
public class MainRoute extends RouteBuilder {

    private final CountryNotFoundProcessor countryNotFoundProcessor;
    private final GenericErrorProcessor genericErrorProcessor;

    public MainRoute(CountryNotFoundProcessor countryNotFoundProcessor,
                     GenericErrorProcessor genericErrorProcessor) {
        this.countryNotFoundProcessor = countryNotFoundProcessor;
        this.genericErrorProcessor = genericErrorProcessor;
    }

    @Override
    public void configure() throws Exception {

        onException(HttpOperationFailedException.class)
                .handled(true)
                .process(countryNotFoundProcessor)
                .marshal().json();

        onException(Exception.class)
                .handled(true)
                .process(genericErrorProcessor)
                .marshal().json();

        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")
                .contextPath("/api");

        rest("/country-summary")
                .description("Country Summary API")
                .get()
                    .description("Get an intelligent summary for a country")
                    .param().name("name").type(RestParamType.query).required(true)
                        .description("Country name to search for").endParam()
                    .produces("application/json")
                    .outType(CountrySummary.class)
                    .to(RouteConstants.DIRECT_COUNTRY_SUMMARY);
    }
}
