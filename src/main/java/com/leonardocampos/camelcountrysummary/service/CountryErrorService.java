package com.leonardocampos.camelcountrysummary.service;

import com.leonardocampos.camelcountrysummary.model.ErrorResponse;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.stereotype.Service;

@Service
public class CountryErrorService {

    public ErrorResponse buildErrorResponse(String countryName, Exception cause) {
        if (cause instanceof HttpOperationFailedException httpException) {
            int statusCode = httpException.getStatusCode();

            if (statusCode == 404) {
                return new ErrorResponse(404, "Not Found",
                        "Country '" + countryName + "' was not found. Please check the spelling and try again.");
            }

            return new ErrorResponse(503, "Service Unavailable",
                    "The REST Countries API returned an error (HTTP " + statusCode + "). Please try again later.");
        }

        return new ErrorResponse(503, "Service Unavailable",
                "The REST Countries API is currently unavailable. Please try again later.");
    }

    public int resolveHttpStatus(Exception cause) {
        if (cause instanceof HttpOperationFailedException httpException) {
            return httpException.getStatusCode() == 404 ? 404 : 503;
        }
        return 503;
    }
}
