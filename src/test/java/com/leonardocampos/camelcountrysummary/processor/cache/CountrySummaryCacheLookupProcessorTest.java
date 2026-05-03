package com.leonardocampos.camelcountrysummary.processor.cache;

import com.leonardocampos.camelcountrysummary.domain.model.CountrySummary;
import com.leonardocampos.camelcountrysummary.service.cache.CountrySummaryCacheService;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.leonardocampos.camelcountrysummary.config.RouteConstants.HEADER_CACHE_HIT;
import static com.leonardocampos.camelcountrysummary.config.RouteConstants.HEADER_COUNTRY_NAME;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountrySummaryCacheLookupProcessorTest {

    @Mock
    private CountrySummaryCacheService cacheService;

    @Mock
    private Exchange exchange;

    @Mock
    private Message message;

    @InjectMocks
    private CountrySummaryCacheLookupProcessor processor;

    @Test
    void process_shouldSetBodyWhenCacheHits() {
        CountrySummary summary = new CountrySummary();
        summary.setCountry("Brazil");

        when(exchange.getIn()).thenReturn(message);
        when(message.getHeader(HEADER_COUNTRY_NAME, String.class)).thenReturn("Brazil");
        when(cacheService.get("Brazil")).thenReturn(Optional.of(summary));

        processor.process(exchange);

        verify(message).setHeader(HEADER_CACHE_HIT, true);
        verify(message).setBody(summary);
    }

    @Test
    void process_shouldMarkMissWhenCacheIsEmpty() {
        when(exchange.getIn()).thenReturn(message);
        when(message.getHeader(HEADER_COUNTRY_NAME, String.class)).thenReturn("Japan");
        when(cacheService.get("Japan")).thenReturn(Optional.empty());

        processor.process(exchange);

        verify(message).setHeader(HEADER_CACHE_HIT, false);
    }
}
