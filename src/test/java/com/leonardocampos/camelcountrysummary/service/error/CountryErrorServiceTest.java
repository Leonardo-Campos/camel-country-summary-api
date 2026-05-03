package com.leonardocampos.camelcountrysummary.service.error;

import com.leonardocampos.camelcountrysummary.domain.model.ErrorResponse;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CountryErrorServiceTest {

    private CountryErrorService service;

    @BeforeEach
    void setUp() {
        service = new CountryErrorService();
    }

    @Test
    void buildErrorResponse_shouldReturn404ForNotFound() {
        HttpOperationFailedException cause = new HttpOperationFailedException(
                "http://example.com", 404, "Not Found", null, null, "");

        ErrorResponse response = service.buildErrorResponse("Brazil", cause);

        assertEquals(404, response.getStatus());
        assertEquals("Not Found", response.getError());
        assertTrue(response.getMessage().contains("Brazil"));
    }

    @Test
    void buildErrorResponse_shouldReturn503ForServerError() {
        HttpOperationFailedException cause = new HttpOperationFailedException(
                "http://example.com", 500, "Server Error", null, null, "");

        ErrorResponse response = service.buildErrorResponse("Germany", cause);

        assertEquals(503, response.getStatus());
        assertEquals("Service Unavailable", response.getError());
        assertTrue(response.getMessage().contains("500"));
    }

    @Test
    void buildErrorResponse_shouldReturn503ForGenericException() {
        RuntimeException cause = new RuntimeException("Connection refused");

        ErrorResponse response = service.buildErrorResponse("France", cause);

        assertEquals(503, response.getStatus());
        assertEquals("Service Unavailable", response.getError());
    }

    @Test
    void buildErrorResponse_shouldReturn503ForNullCause() {
        ErrorResponse response = service.buildErrorResponse("Japan", null);

        assertEquals(503, response.getStatus());
    }

    @Test
    void resolveHttpStatus_shouldReturn404For404Exception() {
        HttpOperationFailedException cause = new HttpOperationFailedException(
                "http://example.com", 404, "Not Found", null, null, "");

        assertEquals(404, service.resolveHttpStatus(cause));
    }

    @Test
    void resolveHttpStatus_shouldReturn503For500Exception() {
        HttpOperationFailedException cause = new HttpOperationFailedException(
                "http://example.com", 500, "Server Error", null, null, "");

        assertEquals(503, service.resolveHttpStatus(cause));
    }

    @Test
    void resolveHttpStatus_shouldReturn503For502Exception() {
        HttpOperationFailedException cause = new HttpOperationFailedException(
                "http://example.com", 502, "Bad Gateway", null, null, "");

        assertEquals(503, service.resolveHttpStatus(cause));
    }

    @Test
    void resolveHttpStatus_shouldReturn503ForGenericException() {
        assertEquals(503, service.resolveHttpStatus(new RuntimeException()));
    }

    @Test
    void resolveHttpStatus_shouldReturn503ForNullCause() {
        assertEquals(503, service.resolveHttpStatus(null));
    }
}
