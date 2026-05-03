package com.leonardocampos.camelcountrysummary.processor.error;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HttpStatusValidationProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(HttpStatusValidationProcessor.class);

    @Value("${api.restcountries.url:https://restcountries.com/v3.1}")
    private String restCountriesUrl;

    @Override
    public void process(Exchange exchange) throws Exception {
        Integer statusCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
        String responseBody = exchange.getIn().getBody(String.class);

        LOG.debug("HTTP Response - Status Code: {}, Body: {}", statusCode, responseBody);

        if (statusCode != null && statusCode >= 500) {
            LOG.error("Server error from REST Countries API - Status Code: {}", statusCode);
            throw new HttpOperationFailedException(
                    restCountriesUrl, statusCode, "Server Error",
                    null, null, responseBody);
        }
    }
}
