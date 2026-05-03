package com.leonardocampos.camelcountrysummary.processor;

import com.leonardocampos.camelcountrysummary.model.WeatherInfo;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import static com.leonardocampos.camelcountrysummary.config.RouteConstants.DATA_TYPE_WEATHER;
import static com.leonardocampos.camelcountrysummary.config.RouteConstants.HEADER_DATA_TYPE;

@Component
public class WeatherFallbackProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {
        WeatherInfo weather = new WeatherInfo();
        weather.setError("Weather data is temporarily unavailable");
        exchange.getIn().setHeader(HEADER_DATA_TYPE, DATA_TYPE_WEATHER);
        exchange.getIn().setBody(weather);
    }
}
