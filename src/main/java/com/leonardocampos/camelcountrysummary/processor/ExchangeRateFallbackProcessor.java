package com.leonardocampos.camelcountrysummary.processor;

import com.leonardocampos.camelcountrysummary.model.ExchangeRateInfo;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import static com.leonardocampos.camelcountrysummary.config.RouteConstants.DATA_TYPE_EXCHANGE_RATE;
import static com.leonardocampos.camelcountrysummary.config.RouteConstants.HEADER_DATA_TYPE;

@Component
public class ExchangeRateFallbackProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {
        ExchangeRateInfo exchangeRate = new ExchangeRateInfo();
        exchangeRate.setError("Exchange rate data is temporarily unavailable");
        exchange.getIn().setHeader(HEADER_DATA_TYPE, DATA_TYPE_EXCHANGE_RATE);
        exchange.getIn().setBody(exchangeRate);
    }
}
