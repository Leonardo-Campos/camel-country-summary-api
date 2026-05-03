package com.leonardocampos.camelcountrysummary.strategy;

import com.leonardocampos.camelcountrysummary.model.CountrySummary;
import com.leonardocampos.camelcountrysummary.model.ExchangeRateInfo;
import com.leonardocampos.camelcountrysummary.model.WeatherInfo;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import static com.leonardocampos.camelcountrysummary.config.RouteConstants.*;

@Component
public class CountrySummaryAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        CountrySummary summary;

        if (oldExchange == null) {
            summary = newExchange.getIn().getHeader(HEADER_COUNTRY_SUMMARY, CountrySummary.class);
            if (summary == null) {
                summary = new CountrySummary();
            }
            applyBranchData(summary, newExchange);
            newExchange.getIn().setHeader(HEADER_COUNTRY_SUMMARY, summary);
            newExchange.getIn().setBody(summary);
            return newExchange;
        }

        summary = oldExchange.getIn().getHeader(HEADER_COUNTRY_SUMMARY, CountrySummary.class);
        if (summary == null) {
            summary = new CountrySummary();
        }

        applyBranchData(summary, newExchange);

        oldExchange.getIn().setHeader(HEADER_COUNTRY_SUMMARY, summary);
        oldExchange.getIn().setBody(summary);
        return oldExchange;
    }

    private void applyBranchData(CountrySummary summary, Exchange exchange) {
        String dataType = exchange.getIn().getHeader(HEADER_DATA_TYPE, String.class);
        Object body = exchange.getIn().getBody();

        if (DATA_TYPE_WEATHER.equals(dataType) && body instanceof WeatherInfo weatherInfo) {
            summary.setWeather(weatherInfo);
        } else if (DATA_TYPE_EXCHANGE_RATE.equals(dataType) && body instanceof ExchangeRateInfo exchangeRateInfo) {
            summary.setExchangeRate(exchangeRateInfo);
        }
    }
}
