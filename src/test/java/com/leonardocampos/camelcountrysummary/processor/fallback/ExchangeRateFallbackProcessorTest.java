package com.leonardocampos.camelcountrysummary.processor.fallback;

import com.leonardocampos.camelcountrysummary.domain.model.ExchangeRateInfo;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.leonardocampos.camelcountrysummary.config.RouteConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateFallbackProcessorTest {

    @Mock
    private Exchange exchange;

    @Mock
    private Message message;

    @InjectMocks
    private ExchangeRateFallbackProcessor processor;

    @Test
    void process_shouldSetExchangeRateInfoWithError() {
        when(exchange.getIn()).thenReturn(message);

        processor.process(exchange);

        verify(message).setHeader(HEADER_DATA_TYPE, DATA_TYPE_EXCHANGE_RATE);

        ArgumentCaptor<ExchangeRateInfo> captor = ArgumentCaptor.forClass(ExchangeRateInfo.class);
        verify(message).setBody(captor.capture());

        ExchangeRateInfo info = captor.getValue();
        assertNotNull(info.getError());
        assertTrue(info.getError().contains("unavailable"));
        assertNull(info.getBaseCurrency());
        assertNull(info.getRates());
    }
}
