package com.leonardocampos.camelcountrysummary.processor;

import com.leonardocampos.camelcountrysummary.model.ErrorResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class GenericErrorProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {
        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 500);
        exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
        exchange.getIn().setBody(new ErrorResponse(500, "Internal Server Error",
                "An unexpected error occurred while processing the request"));
    }
}
