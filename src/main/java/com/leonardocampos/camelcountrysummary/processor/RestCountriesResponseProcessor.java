package com.leonardocampos.camelcountrysummary.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leonardocampos.camelcountrysummary.model.CountrySummary;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import static com.leonardocampos.camelcountrysummary.config.RouteConstants.*;

@Component
public class RestCountriesResponseProcessor implements Processor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void process(Exchange exchange) throws Exception {
        String body = exchange.getIn().getBody(String.class);
        JsonNode root = objectMapper.readTree(body);

        JsonNode country = root.isArray() && !root.isEmpty() ? root.get(0) : root;
        CountrySummary summary = new CountrySummary();

        summary.setCountry(country.path("name").path("common").asText());

        JsonNode capitals = country.path("capital");
        if (capitals.isArray() && !capitals.isEmpty()) {
            summary.setCapital(capitals.get(0).asText());
        }

        JsonNode currencies = country.path("currencies");
        if (currencies.isObject() && currencies.size() > 0) {
            String currencyCode = currencies.fieldNames().next();
            summary.setCurrencyCode(currencyCode);
            summary.setCurrency(currencies.path(currencyCode).path("name").asText());
        }

        JsonNode latlng = country.path("latlng");
        if (latlng.isArray() && latlng.size() >= 2) {
            summary.setLatitude(latlng.get(0).asDouble());
            summary.setLongitude(latlng.get(1).asDouble());
        }

        exchange.getIn().setHeader(HEADER_CURRENCY_CODE, summary.getCurrencyCode());
        exchange.getIn().setHeader(HEADER_LATITUDE, summary.getLatitude());
        exchange.getIn().setHeader(HEADER_LONGITUDE, summary.getLongitude());
        exchange.getIn().setBody(summary);
    }
}
