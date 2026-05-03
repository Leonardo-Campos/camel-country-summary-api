package com.leonardocampos.camelcountrysummary.processor.error;

import com.leonardocampos.camelcountrysummary.domain.model.ErrorResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CircuitBreakerFallbackProcessorTest {

    @Mock
    private Exchange exchange;

    @Mock
    private Message message;

    @Test
    void process_shouldSetStatusAndErrorResponse() {
        CircuitBreakerFallbackProcessor processor = new CircuitBreakerFallbackProcessor(
                503, "Service Unavailable", "API is down");

        when(exchange.getIn()).thenReturn(message);

        processor.process(exchange);

        verify(message).setHeader(Exchange.HTTP_RESPONSE_CODE, 503);
        verify(message).setHeader(Exchange.CONTENT_TYPE, "application/json");

        ArgumentCaptor<ErrorResponse> captor = ArgumentCaptor.forClass(ErrorResponse.class);
        verify(message).setBody(captor.capture());

        ErrorResponse response = captor.getValue();
        assertEquals(503, response.getStatus());
        assertEquals("Service Unavailable", response.getError());
        assertEquals("API is down", response.getMessage());
    }

    @Test
    void process_shouldWorkWith500Status() {
        CircuitBreakerFallbackProcessor processor = new CircuitBreakerFallbackProcessor(
                500, "Internal Server Error", "Unexpected error");

        when(exchange.getIn()).thenReturn(message);

        processor.process(exchange);

        verify(message).setHeader(Exchange.HTTP_RESPONSE_CODE, 500);

        ArgumentCaptor<ErrorResponse> captor = ArgumentCaptor.forClass(ErrorResponse.class);
        verify(message).setBody(captor.capture());
        assertEquals(500, captor.getValue().getStatus());
    }
}
