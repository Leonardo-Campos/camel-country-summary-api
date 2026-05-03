package com.leonardocampos.camelcountrysummary.processor.response;

import com.leonardocampos.camelcountrysummary.domain.model.CountrySummary;
import com.leonardocampos.camelcountrysummary.service.country.RestCountriesService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static com.leonardocampos.camelcountrysummary.config.RouteConstants.*;

@Component
public class RestCountriesResponseProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(RestCountriesResponseProcessor.class);
    private final RestCountriesService restCountriesService;

    public RestCountriesResponseProcessor(RestCountriesService restCountriesService) {
        this.restCountriesService = restCountriesService;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        try {
            String body = exchange.getIn().getBody(String.class);
            CountrySummary summary = restCountriesService.parseResponse(body);

            exchange.getIn().setHeader(HEADER_CURRENCY_CODE, summary.getCurrencyCode());
            exchange.getIn().setHeader(HEADER_LATITUDE, summary.getLatitude());
            exchange.getIn().setHeader(HEADER_LONGITUDE, summary.getLongitude());
            exchange.getIn().setBody(summary);
        } catch (Exception e) {
            logger.error("Error processing REST Countries response", e);
            throw e;
        }
    }
}
