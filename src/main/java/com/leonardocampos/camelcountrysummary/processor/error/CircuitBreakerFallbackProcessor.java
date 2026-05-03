package com.leonardocampos.camelcountrysummary.processor.error;

import com.leonardocampos.camelcountrysummary.domain.model.ErrorResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CircuitBreakerFallbackProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(CircuitBreakerFallbackProcessor.class);

    private final int statusCode;
    private final String error;
    private final String message;

    public CircuitBreakerFallbackProcessor(int statusCode, String error, String message) {
        this.statusCode = statusCode;
        this.error = error;
        this.message = message;
    }

    @Override
    public void process(Exchange exchange) {
        Throwable exception = exchange.getException();
        LOG.error("Circuit breaker fallback - Status: {}, Error: {}, Message: {}, Exception: {}",
                statusCode, error, message, exception != null ? exception.getMessage() : "None");

        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, statusCode);
        exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
        exchange.getIn().setBody(new ErrorResponse(statusCode, error, message));
    }
}
