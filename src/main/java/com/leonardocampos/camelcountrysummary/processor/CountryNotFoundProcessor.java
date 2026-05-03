package com.leonardocampos.camelcountrysummary.processor;

import com.leonardocampos.camelcountrysummary.config.RouteConstants;
import com.leonardocampos.camelcountrysummary.model.ErrorResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.stereotype.Component;

@Component
public class CountryNotFoundProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        String countryName = exchange.getIn().getHeader(RouteConstants.HEADER_COUNTRY_NAME, String.class);
        Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);

        if (cause instanceof HttpOperationFailedException httpException) {
            int statusCode = httpException.getStatusCode();

            if (statusCode == 404) {
                exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
                exchange.getIn().setBody(new ErrorResponse(404, "Not Found",
                        "Country '" + countryName + "' was not found. Please check the spelling and try again."));
            } else {
                exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 503);
                exchange.getIn().setBody(new ErrorResponse(503, "Service Unavailable",
                        "The REST Countries API returned an error (HTTP " + statusCode + "). Please try again later."));
            }
        } else {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 503);
            exchange.getIn().setBody(new ErrorResponse(503, "Service Unavailable",
                    "The REST Countries API is currently unavailable. Please try again later."));
        }
    }
}
