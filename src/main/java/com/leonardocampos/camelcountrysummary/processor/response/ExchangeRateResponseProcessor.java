package com.leonardocampos.camelcountrysummary.processor.response;

import com.leonardocampos.camelcountrysummary.domain.model.ExchangeRateInfo;
import com.leonardocampos.camelcountrysummary.service.exchangerate.IExchangeRateService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static com.leonardocampos.camelcountrysummary.config.RouteConstants.DATA_TYPE_EXCHANGE_RATE;
import static com.leonardocampos.camelcountrysummary.config.RouteConstants.HEADER_CURRENCY_CODE;
import static com.leonardocampos.camelcountrysummary.config.RouteConstants.HEADER_DATA_TYPE;

@Component
public class ExchangeRateResponseProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateResponseProcessor.class);
    private final IExchangeRateService exchangeRateService;

    public ExchangeRateResponseProcessor(IExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        try {
            String body = exchange.getIn().getBody(String.class);
            String countryCurrencyCode = exchange.getIn().getHeader(HEADER_CURRENCY_CODE, String.class);
            ExchangeRateInfo exchangeRate = exchangeRateService.parseResponse(body, countryCurrencyCode);

            exchange.getIn().setHeader(HEADER_DATA_TYPE, DATA_TYPE_EXCHANGE_RATE);
            exchange.getIn().setBody(exchangeRate);
        } catch (Exception e) {
            logger.error("Error processing Exchange Rate response", e);
            throw e;
        }
    }
}
