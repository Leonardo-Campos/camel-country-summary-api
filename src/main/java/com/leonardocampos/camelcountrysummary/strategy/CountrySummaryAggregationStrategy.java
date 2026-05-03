package com.leonardocampos.camelcountrysummary.strategy;

import com.leonardocampos.camelcountrysummary.model.CountrySummary;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class CountrySummaryAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            return newExchange;
        }

        CountrySummary summary = oldExchange.getIn().getHeader("countrySummary", CountrySummary.class);
        if (summary == null) {
            summary = oldExchange.getIn().getBody(CountrySummary.class);
        }

        if (summary == null) {
            summary = new CountrySummary();
        }

        String dataType = newExchange.getIn().getHeader("dataType", String.class);
        Object body = newExchange.getIn().getBody();

        if ("weather".equals(dataType) && body instanceof CountrySummary.WeatherInfo weatherInfo) {
            summary.setWeather(weatherInfo);
        } else if ("exchangeRate".equals(dataType) && body instanceof CountrySummary.ExchangeRateInfo exchangeRateInfo) {
            summary.setExchangeRate(exchangeRateInfo);
        }

        oldExchange.getIn().setHeader("countrySummary", summary);
        oldExchange.getIn().setBody(summary);
        return oldExchange;
    }
}
