package com.leonardocampos.camelcountrysummary.processor;

import com.leonardocampos.camelcountrysummary.model.ErrorResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenericErrorProcessorTest {

    @Mock
    private Exchange exchange;

    @Mock
    private Message message;

    @InjectMocks
    private GenericErrorProcessor processor;

    @Test
    void process_shouldSet500ErrorResponse() {
        when(exchange.getIn()).thenReturn(message);

        processor.process(exchange);

        verify(message).setHeader(Exchange.HTTP_RESPONSE_CODE, 500);
        verify(message).setHeader(Exchange.CONTENT_TYPE, "application/json");

        ArgumentCaptor<ErrorResponse> captor = ArgumentCaptor.forClass(ErrorResponse.class);
        verify(message).setBody(captor.capture());

        ErrorResponse response = captor.getValue();
        assertEquals(500, response.getStatus());
        assertEquals("Internal Server Error", response.getError());
        assertNotNull(response.getMessage());
    }
}
