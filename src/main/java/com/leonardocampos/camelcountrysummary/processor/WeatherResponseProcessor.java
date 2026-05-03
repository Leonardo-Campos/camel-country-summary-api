package com.leonardocampos.camelcountrysummary.processor;

import com.leonardocampos.camelcountrysummary.model.WeatherInfo;
import com.leonardocampos.camelcountrysummary.service.WeatherService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import static com.leonardocampos.camelcountrysummary.config.RouteConstants.DATA_TYPE_WEATHER;
import static com.leonardocampos.camelcountrysummary.config.RouteConstants.HEADER_DATA_TYPE;

@Component
public class WeatherResponseProcessor implements Processor {

    private final WeatherService weatherService;

    public WeatherResponseProcessor(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String body = exchange.getIn().getBody(String.class);
        WeatherInfo weather = weatherService.parseResponse(body);

        exchange.getIn().setHeader(HEADER_DATA_TYPE, DATA_TYPE_WEATHER);
        exchange.getIn().setBody(weather);
    }
}
