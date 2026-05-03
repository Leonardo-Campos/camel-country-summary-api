package com.leonardocampos.camelcountrysummary.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HttpStatusValidationProcessor implements Processor {

    @Value("${api.restcountries.url:https://restcountries.com/v3.1}")
    private String restCountriesUrl;

    @Override
    public void process(Exchange exchange) throws Exception {
        Integer statusCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
        if (statusCode != null && statusCode >= 500) {
            throw new HttpOperationFailedException(
                    restCountriesUrl, statusCode, "Server Error",
                    null, null, exchange.getIn().getBody(String.class));
        }
    }
}
