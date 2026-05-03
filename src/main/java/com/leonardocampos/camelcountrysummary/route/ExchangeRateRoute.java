package com.leonardocampos.camelcountrysummary.route;

import com.leonardocampos.camelcountrysummary.config.ExchangeRateConfig;
import com.leonardocampos.camelcountrysummary.processor.fallback.ExchangeRateFallbackProcessor;
import com.leonardocampos.camelcountrysummary.processor.response.ExchangeRateResponseProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.leonardocampos.camelcountrysummary.config.RouteConstants.*;

@Component
public class ExchangeRateRoute extends RouteBuilder {

    private final ExchangeRateResponseProcessor exchangeRateProcessor;
    private final ExchangeRateFallbackProcessor exchangeRateFallbackProcessor;

    @Value("${api.exchangerate.url:https://api.exchangerate.host}")
    private String exchangeRateUrl;

    @Value("${api.exchangerate.key:}")
    private String exchangeRateApiKey;

    public ExchangeRateRoute(ExchangeRateResponseProcessor exchangeRateProcessor,
                             ExchangeRateFallbackProcessor exchangeRateFallbackProcessor) {
        this.exchangeRateProcessor = exchangeRateProcessor;
        this.exchangeRateFallbackProcessor = exchangeRateFallbackProcessor;
    }

    @Override
    public void configure() throws Exception {
        String supportedCurrencies = String.join(",", ExchangeRateConfig.supportedCurrencies());

        from(DIRECT_FETCH_EXCHANGE_RATE)
                .routeId("fetch-exchange-rate")
                .circuitBreaker()
                    .resilience4jConfiguration()
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(20)
                        .slidingWindowSize(5)
                        .minimumNumberOfCalls(3)
                    .end()
                    .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                    .setHeader("Accept", constant("application/json"))
                    .setHeader("Accept-Encoding", constant("deflate"))
                    .setBody(constant(null))
                    .toD(exchangeRateUrl
                            + "/live?access_key=" + exchangeRateApiKey
                            + "&currencies=" + supportedCurrencies
                            + "&bridgeEndpoint=true")
                    .process(exchangeRateProcessor)
                .onFallback()
                    .process(exchangeRateFallbackProcessor)
                .end();
    }
}
