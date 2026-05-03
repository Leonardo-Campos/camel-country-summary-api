package com.leonardocampos.camelcountrysummary.processor.response;

import com.leonardocampos.camelcountrysummary.domain.model.WeatherInfo;
import com.leonardocampos.camelcountrysummary.service.weather.WeatherService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static com.leonardocampos.camelcountrysummary.config.RouteConstants.DATA_TYPE_WEATHER;
import static com.leonardocampos.camelcountrysummary.config.RouteConstants.HEADER_DATA_TYPE;

@Component
public class WeatherResponseProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(WeatherResponseProcessor.class);
    private final WeatherService weatherService;

    public WeatherResponseProcessor(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        try {
            String body = exchange.getIn().getBody(String.class);
            WeatherInfo weather = weatherService.parseResponse(body);

            exchange.getIn().setHeader(HEADER_DATA_TYPE, DATA_TYPE_WEATHER);
            exchange.getIn().setBody(weather);
        } catch (Exception e) {
            logger.error("Error processing Weather response", e);
            throw e;
        }
    }
}
