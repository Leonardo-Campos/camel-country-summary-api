package com.leonardocampos.camelcountrysummary.strategy;

import com.leonardocampos.camelcountrysummary.model.CountrySummary;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class CountrySummaryAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        CountrySummary summary;

        if (oldExchange == null) {
            summary = newExchange.getIn().getHeader("countrySummary", CountrySummary.class);
            if (summary == null) {
                summary = new CountrySummary();
            }
            applyBranchData(summary, newExchange);
            newExchange.getIn().setHeader("countrySummary", summary);
            newExchange.getIn().setBody(summary);
            return newExchange;
        }

        summary = oldExchange.getIn().getHeader("countrySummary", CountrySummary.class);
        if (summary == null) {
            summary = new CountrySummary();
        }

        applyBranchData(summary, newExchange);

        oldExchange.getIn().setHeader("countrySummary", summary);
        oldExchange.getIn().setBody(summary);
        return oldExchange;
    }

    private void applyBranchData(CountrySummary summary, Exchange exchange) {
        String dataType = exchange.getIn().getHeader("dataType", String.class);
        Object body = exchange.getIn().getBody();

        if ("weather".equals(dataType) && body instanceof CountrySummary.WeatherInfo weatherInfo) {
            summary.setWeather(weatherInfo);
        } else if ("exchangeRate".equals(dataType) && body instanceof CountrySummary.ExchangeRateInfo exchangeRateInfo) {
            summary.setExchangeRate(exchangeRateInfo);
        }
    }
}
