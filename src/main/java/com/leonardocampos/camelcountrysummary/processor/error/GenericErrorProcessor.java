package com.leonardocampos.camelcountrysummary.processor.error;

import com.leonardocampos.camelcountrysummary.domain.model.ErrorResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GenericErrorProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(GenericErrorProcessor.class);

    @Override
    public void process(Exchange exchange) {
        Throwable exception = exchange.getException();
        LOG.error("Generic error handler triggered", exception);

        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 500);
        exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
        exchange.getIn().setBody(new ErrorResponse(500, "Internal Server Error",
                "An unexpected error occurred while processing the request"));
    }
}
