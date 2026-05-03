package com.leonardocampos.camelcountrysummary.processor;

import com.leonardocampos.camelcountrysummary.config.RouteConstants;
import com.leonardocampos.camelcountrysummary.model.ErrorResponse;
import com.leonardocampos.camelcountrysummary.service.CountryErrorService;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryNotFoundProcessorTest {

    @Mock
    private CountryErrorService countryErrorService;

    @Mock
    private Exchange exchange;

    @Mock
    private Message message;

    @InjectMocks
    private CountryNotFoundProcessor processor;

    @Test
    void process_shouldDelegateToErrorService() throws Exception {
        HttpOperationFailedException cause = new HttpOperationFailedException(
                "http://example.com", 404, "Not Found", null, null, "");
        ErrorResponse errorResponse = new ErrorResponse(404, "Not Found", "Country 'XYZ' was not found.");

        when(exchange.getIn()).thenReturn(message);
        when(message.getHeader(RouteConstants.HEADER_COUNTRY_NAME, String.class)).thenReturn("XYZ");
        when(exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class)).thenReturn(cause);
        when(countryErrorService.buildErrorResponse("XYZ", cause)).thenReturn(errorResponse);
        when(countryErrorService.resolveHttpStatus(cause)).thenReturn(404);

        processor.process(exchange);

        verify(message).setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
        verify(message).setBody(errorResponse);
    }

    @Test
    void process_shouldHandle503ForServerError() throws Exception {
        HttpOperationFailedException cause = new HttpOperationFailedException(
                "http://example.com", 500, "Server Error", null, null, "");
        ErrorResponse errorResponse = new ErrorResponse(503, "Service Unavailable", "API error");

        when(exchange.getIn()).thenReturn(message);
        when(message.getHeader(RouteConstants.HEADER_COUNTRY_NAME, String.class)).thenReturn("Test");
        when(exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class)).thenReturn(cause);
        when(countryErrorService.buildErrorResponse("Test", cause)).thenReturn(errorResponse);
        when(countryErrorService.resolveHttpStatus(cause)).thenReturn(503);

        processor.process(exchange);

        verify(message).setHeader(Exchange.HTTP_RESPONSE_CODE, 503);
        verify(message).setBody(errorResponse);
    }
}
