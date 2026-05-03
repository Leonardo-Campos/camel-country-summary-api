package com.leonardocampos.camelcountrysummary.processor.error;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static com.leonardocampos.camelcountrysummary.config.RouteConstants.HEADER_COUNTRY_NAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryNotFoundThrowProcessorTest {

    @Mock
    private Exchange exchange;

    @Mock
    private Message message;

    private CountryNotFoundThrowProcessor processor;

    @BeforeEach
    void setUp() throws Exception {
        processor = new CountryNotFoundThrowProcessor();
        Field urlField = CountryNotFoundThrowProcessor.class.getDeclaredField("restCountriesUrl");
        urlField.setAccessible(true);
        urlField.set(processor, "https://restcountries.com/v3.1");
    }

    @Test
    void process_shouldThrow404WithCountryName() {
        when(exchange.getIn()).thenReturn(message);
        when(message.getHeader(HEADER_COUNTRY_NAME, String.class)).thenReturn("InvalidCountry");
        when(message.getBody(String.class)).thenReturn("Not Found");

        HttpOperationFailedException ex = assertThrows(
                HttpOperationFailedException.class, () -> processor.process(exchange));

        assertEquals(404, ex.getStatusCode());
        assertTrue(ex.getUri().contains("InvalidCountry"));
    }
}
