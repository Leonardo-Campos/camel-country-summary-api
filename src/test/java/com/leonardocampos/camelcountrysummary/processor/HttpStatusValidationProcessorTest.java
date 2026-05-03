package com.leonardocampos.camelcountrysummary.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HttpStatusValidationProcessorTest {

    @Mock
    private Exchange exchange;

    @Mock
    private Message message;

    private HttpStatusValidationProcessor processor;

    @BeforeEach
    void setUp() throws Exception {
        processor = new HttpStatusValidationProcessor();
        Field urlField = HttpStatusValidationProcessor.class.getDeclaredField("restCountriesUrl");
        urlField.setAccessible(true);
        urlField.set(processor, "https://restcountries.com/v3.1");
    }

    @Test
    void process_shouldThrowFor500() {
        when(exchange.getIn()).thenReturn(message);
        when(message.getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class)).thenReturn(500);
        when(message.getBody(String.class)).thenReturn("error body");

        HttpOperationFailedException ex = assertThrows(
                HttpOperationFailedException.class, () -> processor.process(exchange));
        assertEquals(500, ex.getStatusCode());
    }

    @Test
    void process_shouldThrowFor502() {
        when(exchange.getIn()).thenReturn(message);
        when(message.getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class)).thenReturn(502);
        when(message.getBody(String.class)).thenReturn("bad gateway");

        assertThrows(HttpOperationFailedException.class, () -> processor.process(exchange));
    }

    @Test
    void process_shouldNotThrowFor200() throws Exception {
        when(exchange.getIn()).thenReturn(message);
        when(message.getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class)).thenReturn(200);

        assertDoesNotThrow(() -> processor.process(exchange));
    }

    @Test
    void process_shouldNotThrowFor404() throws Exception {
        when(exchange.getIn()).thenReturn(message);
        when(message.getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class)).thenReturn(404);

        assertDoesNotThrow(() -> processor.process(exchange));
    }

    @Test
    void process_shouldNotThrowForNullStatusCode() throws Exception {
        when(exchange.getIn()).thenReturn(message);
        when(message.getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class)).thenReturn(null);

        assertDoesNotThrow(() -> processor.process(exchange));
    }
}
