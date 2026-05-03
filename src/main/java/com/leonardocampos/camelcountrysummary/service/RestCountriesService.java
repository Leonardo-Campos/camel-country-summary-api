package com.leonardocampos.camelcountrysummary.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leonardocampos.camelcountrysummary.model.CountrySummary;
import com.leonardocampos.camelcountrysummary.model.api.RestCountryResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RestCountriesService {

    private final ObjectMapper objectMapper;

    public RestCountriesService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public CountrySummary parseResponse(String json) throws Exception {
        List<RestCountryResponse> countries = objectMapper.readValue(json,
                new TypeReference<List<RestCountryResponse>>() {});

        RestCountryResponse country = countries.get(0);
        return mapToSummary(country);
    }

    private CountrySummary mapToSummary(RestCountryResponse country) {
        CountrySummary summary = new CountrySummary();

        if (country.getName() != null) {
            summary.setCountry(country.getName().getCommon());
        }

        List<String> capitals = country.getCapital();
        if (capitals != null && !capitals.isEmpty()) {
            summary.setCapital(capitals.get(0));
        }

        Map<String, RestCountryResponse.CurrencyInfo> currencies = country.getCurrencies();
        if (currencies != null && !currencies.isEmpty()) {
            Map.Entry<String, RestCountryResponse.CurrencyInfo> firstCurrency =
                    currencies.entrySet().iterator().next();
            summary.setCurrencyCode(firstCurrency.getKey());
            summary.setCurrency(firstCurrency.getValue().getName());
        }

        List<Double> latlng = country.getLatlng();
        if (latlng != null && latlng.size() >= 2) {
            summary.setLatitude(latlng.get(0));
            summary.setLongitude(latlng.get(1));
        }

        return summary;
    }
}
