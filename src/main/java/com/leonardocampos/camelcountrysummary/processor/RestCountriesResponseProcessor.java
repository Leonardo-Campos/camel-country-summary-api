package com.leonardocampos.camelcountrysummary.processor;

import com.leonardocampos.camelcountrysummary.model.CountrySummary;
import com.leonardocampos.camelcountrysummary.service.RestCountriesService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import static com.leonardocampos.camelcountrysummary.config.RouteConstants.*;

@Component
public class RestCountriesResponseProcessor implements Processor {

    private final RestCountriesService restCountriesService;

    public RestCountriesResponseProcessor(RestCountriesService restCountriesService) {
        this.restCountriesService = restCountriesService;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String body = exchange.getIn().getBody(String.class);
        CountrySummary summary = restCountriesService.parseResponse(body);

        exchange.getIn().setHeader(HEADER_CURRENCY_CODE, summary.getCurrencyCode());
        exchange.getIn().setHeader(HEADER_LATITUDE, summary.getLatitude());
        exchange.getIn().setHeader(HEADER_LONGITUDE, summary.getLongitude());
        exchange.getIn().setBody(summary);
    }
}
