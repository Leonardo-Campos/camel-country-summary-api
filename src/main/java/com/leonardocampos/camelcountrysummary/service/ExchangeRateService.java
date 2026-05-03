package com.leonardocampos.camelcountrysummary.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leonardocampos.camelcountrysummary.model.ExchangeRateInfo;
import com.leonardocampos.camelcountrysummary.model.api.ExchangeRateApiResponse;
import org.springframework.stereotype.Service;

@Service
public class ExchangeRateService {

    private final ObjectMapper objectMapper;

    public ExchangeRateService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ExchangeRateInfo parseResponse(String json) throws Exception {
        ExchangeRateApiResponse response = objectMapper.readValue(json, ExchangeRateApiResponse.class);
        return mapToExchangeRateInfo(response);
    }

    private ExchangeRateInfo mapToExchangeRateInfo(ExchangeRateApiResponse response) {
        ExchangeRateInfo exchangeRate = new ExchangeRateInfo();
        exchangeRate.setBaseCurrency(response.getBase());

        if (response.getRates() != null) {
            exchangeRate.setRates(response.getRates());
        }

        return exchangeRate;
    }
}
