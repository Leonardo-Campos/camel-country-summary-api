package com.leonardocampos.camelcountrysummary.processor;

import com.leonardocampos.camelcountrysummary.model.ErrorResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class CircuitBreakerFallbackProcessor implements Processor {

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
        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, statusCode);
        exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
        exchange.getIn().setBody(new ErrorResponse(statusCode, error, message));
    }
}
