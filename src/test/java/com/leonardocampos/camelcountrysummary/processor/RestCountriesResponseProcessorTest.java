package com.leonardocampos.camelcountrysummary.processor;

import com.leonardocampos.camelcountrysummary.model.CountrySummary;
import com.leonardocampos.camelcountrysummary.service.RestCountriesService;
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
class RestCountriesResponseProcessorTest {

    @Mock
    private RestCountriesService restCountriesService;

    @Mock
    private Exchange exchange;

    @Mock
    private Message message;

    @InjectMocks
    private RestCountriesResponseProcessor processor;

    @Test
    void process_shouldSetHeadersAndBodyFromService() throws Exception {
        String body = "[{\"name\":{\"common\":\"Brazil\"}}]";
        CountrySummary summary = new CountrySummary();
        summary.setCurrencyCode("BRL");
        summary.setLatitude(-10.0);
        summary.setLongitude(-55.0);

        when(exchange.getIn()).thenReturn(message);
        when(message.getBody(String.class)).thenReturn(body);
        when(restCountriesService.parseResponse(body)).thenReturn(summary);

        processor.process(exchange);

        verify(message).setHeader(HEADER_CURRENCY_CODE, "BRL");
        verify(message).setHeader(HEADER_LATITUDE, -10.0);
        verify(message).setHeader(HEADER_LONGITUDE, -55.0);
        verify(message).setBody(summary);
    }
}
