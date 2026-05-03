package com.leonardocampos.camelcountrysummary.processor;

import com.leonardocampos.camelcountrysummary.config.RouteConstants;
import com.leonardocampos.camelcountrysummary.model.ErrorResponse;
import com.leonardocampos.camelcountrysummary.service.CountryErrorService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class CountryNotFoundProcessor implements Processor {

    private final CountryErrorService countryErrorService;

    public CountryNotFoundProcessor(CountryErrorService countryErrorService) {
        this.countryErrorService = countryErrorService;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String countryName = exchange.getIn().getHeader(RouteConstants.HEADER_COUNTRY_NAME, String.class);
        Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);

        ErrorResponse errorResponse = countryErrorService.buildErrorResponse(countryName, cause);
        int httpStatus = countryErrorService.resolveHttpStatus(cause);

        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, httpStatus);
        exchange.getIn().setBody(errorResponse);
    }
}
