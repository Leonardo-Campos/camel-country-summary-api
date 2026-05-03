package com.leonardocampos.camelcountrysummary.processor.error;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.leonardocampos.camelcountrysummary.config.RouteConstants.HEADER_COUNTRY_NAME;

@Component
public class CountryNotFoundThrowProcessor implements Processor {

    @Value("${api.restcountries.url:https://restcountries.com/v3.1}")
    private String restCountriesUrl;

    @Override
    public void process(Exchange exchange) throws Exception {
        String countryName = exchange.getIn().getHeader(HEADER_COUNTRY_NAME, String.class);
        throw new HttpOperationFailedException(
                restCountriesUrl + "/name/" + countryName,
                404, "Not Found", null, null,
                exchange.getIn().getBody(String.class));
    }
}
