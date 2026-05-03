package com.leonardocampos.camelcountrysummary.processor;

import com.leonardocampos.camelcountrysummary.model.ExchangeRateInfo;
import com.leonardocampos.camelcountrysummary.service.ExchangeRateService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import static com.leonardocampos.camelcountrysummary.config.RouteConstants.DATA_TYPE_EXCHANGE_RATE;
import static com.leonardocampos.camelcountrysummary.config.RouteConstants.HEADER_DATA_TYPE;

@Component
public class ExchangeRateResponseProcessor implements Processor {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateResponseProcessor(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String body = exchange.getIn().getBody(String.class);
        ExchangeRateInfo exchangeRate = exchangeRateService.parseResponse(body);

        exchange.getIn().setHeader(HEADER_DATA_TYPE, DATA_TYPE_EXCHANGE_RATE);
        exchange.getIn().setBody(exchangeRate);
    }
}
