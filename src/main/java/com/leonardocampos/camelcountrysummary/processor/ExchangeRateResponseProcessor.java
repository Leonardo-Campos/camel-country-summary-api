package com.leonardocampos.camelcountrysummary.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leonardocampos.camelcountrysummary.model.CountrySummary;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.leonardocampos.camelcountrysummary.config.RouteConstants.DATA_TYPE_EXCHANGE_RATE;
import static com.leonardocampos.camelcountrysummary.config.RouteConstants.HEADER_DATA_TYPE;

@Component
public class ExchangeRateResponseProcessor implements Processor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void process(Exchange exchange) throws Exception {
        String body = exchange.getIn().getBody(String.class);
        JsonNode root = objectMapper.readTree(body);

        CountrySummary.ExchangeRateInfo exchangeRate = new CountrySummary.ExchangeRateInfo();
        exchangeRate.setBaseCurrency(root.path("base").asText(null));

        JsonNode ratesNode = root.path("rates");
        if (ratesNode.isObject()) {
            Map<String, Double> rates = new HashMap<>();
            Iterator<Map.Entry<String, JsonNode>> fields = ratesNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                rates.put(entry.getKey(), entry.getValue().asDouble());
            }
            exchangeRate.setRates(rates);
        }

        exchange.getIn().setHeader(HEADER_DATA_TYPE, DATA_TYPE_EXCHANGE_RATE);
        exchange.getIn().setBody(exchangeRate);
    }
}
