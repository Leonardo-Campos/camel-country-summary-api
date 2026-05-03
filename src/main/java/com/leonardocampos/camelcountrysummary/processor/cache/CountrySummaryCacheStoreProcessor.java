package com.leonardocampos.camelcountrysummary.processor.cache;

import com.leonardocampos.camelcountrysummary.domain.model.CountrySummary;
import com.leonardocampos.camelcountrysummary.service.cache.CountrySummaryCacheService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import static com.leonardocampos.camelcountrysummary.config.RouteConstants.HEADER_COUNTRY_NAME;

@Component
public class CountrySummaryCacheStoreProcessor implements Processor {

    private final CountrySummaryCacheService cacheService;

    public CountrySummaryCacheStoreProcessor(CountrySummaryCacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Override
    public void process(Exchange exchange) {
        String countryName = exchange.getIn().getHeader(HEADER_COUNTRY_NAME, String.class);
        CountrySummary summary = exchange.getIn().getBody(CountrySummary.class);

        if (summary == null) {
            return;
        }

        cacheService.put(countryName, summary);
    }
}
