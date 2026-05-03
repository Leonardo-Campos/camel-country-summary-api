package com.leonardocampos.camelcountrysummary.processor.cache;

import com.leonardocampos.camelcountrysummary.domain.model.CountrySummary;
import com.leonardocampos.camelcountrysummary.service.cache.CountrySummaryCacheService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.leonardocampos.camelcountrysummary.config.RouteConstants.HEADER_CACHE_HIT;
import static com.leonardocampos.camelcountrysummary.config.RouteConstants.HEADER_COUNTRY_NAME;

@Component
public class CountrySummaryCacheLookupProcessor implements Processor {

    private final CountrySummaryCacheService cacheService;

    public CountrySummaryCacheLookupProcessor(CountrySummaryCacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Override
    public void process(Exchange exchange) {
        String countryName = exchange.getIn().getHeader(HEADER_COUNTRY_NAME, String.class);
        Optional<CountrySummary> cachedSummary = cacheService.get(countryName);

        if (cachedSummary.isPresent()) {
            exchange.getIn().setHeader(HEADER_CACHE_HIT, true);
            exchange.getIn().setBody(cachedSummary.get());
            return;
        }

        exchange.getIn().setHeader(HEADER_CACHE_HIT, false);
    }
}
