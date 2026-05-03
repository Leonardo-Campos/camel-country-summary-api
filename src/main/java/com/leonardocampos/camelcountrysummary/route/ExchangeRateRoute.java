package com.leonardocampos.camelcountrysummary.route;

import com.leonardocampos.camelcountrysummary.model.CountrySummary;
import com.leonardocampos.camelcountrysummary.processor.ExchangeRateResponseProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.leonardocampos.camelcountrysummary.config.RouteConstants.*;

@Component
public class ExchangeRateRoute extends RouteBuilder {

    private final ExchangeRateResponseProcessor exchangeRateProcessor;

    @Value("${api.exchangerate.url:https://api.exchangerate.host}")
    private String exchangeRateUrl;

    @Value("${api.exchangerate.key:}")
    private String exchangeRateApiKey;

    public ExchangeRateRoute(ExchangeRateResponseProcessor exchangeRateProcessor) {
        this.exchangeRateProcessor = exchangeRateProcessor;
    }

    @Override
    public void configure() throws Exception {

        from(DIRECT_FETCH_EXCHANGE_RATE)
                .routeId("fetch-exchange-rate")
                .log("Fetching exchange rates for currency=${header." + HEADER_CURRENCY_CODE + "}")
                .circuitBreaker()
                    .resilience4jConfiguration()
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(20)
                        .slidingWindowSize(5)
                        .minimumNumberOfCalls(3)
                    .end()
                    .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                    .setBody(constant(null))
                    .toD(exchangeRateUrl
                            + "/latest?base=${header." + HEADER_CURRENCY_CODE + "}"
                            + "&symbols=USD,EUR,GBP,JPY,BRL"
                            + "&access_key=" + exchangeRateApiKey
                            + "&httpMethod=GET")
                    .process(exchangeRateProcessor)
                .onFallback()
                    .log("Circuit breaker fallback triggered for Exchange Rate API")
                    .process(exchange -> {
                        CountrySummary.ExchangeRateInfo exchangeRate = new CountrySummary.ExchangeRateInfo();
                        exchangeRate.setError("Exchange rate data is temporarily unavailable");
                        exchange.getIn().setHeader(HEADER_DATA_TYPE, DATA_TYPE_EXCHANGE_RATE);
                        exchange.getIn().setBody(exchangeRate);
                    })
                .end();
    }
}
