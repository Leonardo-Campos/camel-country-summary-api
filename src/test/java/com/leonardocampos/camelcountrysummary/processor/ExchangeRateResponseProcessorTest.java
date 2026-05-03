package com.leonardocampos.camelcountrysummary.processor;

import com.leonardocampos.camelcountrysummary.model.ExchangeRateInfo;
import com.leonardocampos.camelcountrysummary.service.ExchangeRateService;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.leonardocampos.camelcountrysummary.config.RouteConstants.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateResponseProcessorTest {

    @Mock
    private ExchangeRateService exchangeRateService;

    @Mock
    private Exchange exchange;

    @Mock
    private Message message;

    @InjectMocks
    private ExchangeRateResponseProcessor processor;

    @Test
    void process_shouldSetDataTypeHeaderAndBody() throws Exception {
        String body = "{\"base\":\"USD\",\"rates\":{}}";
        ExchangeRateInfo info = new ExchangeRateInfo();
        info.setBaseCurrency("USD");

        when(exchange.getIn()).thenReturn(message);
        when(message.getBody(String.class)).thenReturn(body);
        when(exchangeRateService.parseResponse(body)).thenReturn(info);

        processor.process(exchange);

        verify(message).setHeader(HEADER_DATA_TYPE, DATA_TYPE_EXCHANGE_RATE);
        verify(message).setBody(info);
    }
}
