package com.leonardocampos.camelcountrysummary.route;

import com.leonardocampos.camelcountrysummary.strategy.CountrySummaryAggregationStrategy;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import static com.leonardocampos.camelcountrysummary.config.RouteConstants.*;

@Component
public class CountrySummaryRoute extends RouteBuilder {

    private final CountrySummaryAggregationStrategy aggregationStrategy;

    public CountrySummaryRoute(CountrySummaryAggregationStrategy aggregationStrategy) {
        this.aggregationStrategy = aggregationStrategy;
    }

    @Override
    public void configure() throws Exception {

        from(DIRECT_COUNTRY_SUMMARY)
                .routeId("country-summary-main")
                .setHeader(HEADER_COUNTRY_NAME, header("name"))
                .to(DIRECT_FETCH_COUNTRY)
                .to(DIRECT_ENRICH_DATA);

        from(DIRECT_ENRICH_DATA)
                .routeId("enrich-external-data")
                .process(exchange -> {
                    Object body = exchange.getIn().getBody();
                    exchange.getIn().setHeader(HEADER_COUNTRY_SUMMARY, body);
                })
                .multicast(aggregationStrategy)
                    .parallelProcessing()
                    .timeout(10000)
                    .stopOnException()
                    .to(DIRECT_FETCH_WEATHER, DIRECT_FETCH_EXCHANGE_RATE)
                .end();
    }
}
