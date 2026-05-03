package com.leonardocampos.camelcountrysummary.service.exchangerate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leonardocampos.camelcountrysummary.config.ExchangeRateConfig;
import com.leonardocampos.camelcountrysummary.domain.model.ExchangeRateInfo;
import com.leonardocampos.camelcountrysummary.integration.dto.exchangerate.ExchangeRateApiResponse;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Service for parsing and filtering exchange rate data from external API.
 *
 * Responsibility: Transform API response to domain model with filtered currencies.
 * Follows Single Responsibility Principle and Clean Code practices.
 */
@Service
public class ExchangeRateService implements IExchangeRateService {

    private final ObjectMapper objectMapper;
    private final Set<String> supportedCurrencies;

    public ExchangeRateService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.supportedCurrencies = ExchangeRateConfig.supportedCurrencies();
    }

    /**
     * Parse exchange rate response and filter to configured currencies.
     *
     * @param json the API response JSON
     * @return ExchangeRateInfo with only configured currencies
     * @throws Exception if JSON parsing fails
     */
    @Override
    public ExchangeRateInfo parseResponse(String json, String countryCurrencyCode) throws Exception {
        ExchangeRateApiResponse response = objectMapper.readValue(json, ExchangeRateApiResponse.class);
        return mapToExchangeRateInfo(response, countryCurrencyCode);
    }

    /**
     * Map API response to domain model with currency filtering.
     *
     * @param response the parsed API response
     * @return filtered ExchangeRateInfo
     */
    private ExchangeRateInfo mapToExchangeRateInfo(ExchangeRateApiResponse response, String countryCurrencyCode) {
        ExchangeRateInfo exchangeRate = new ExchangeRateInfo();
        String normalizedCurrencyCode = normalizeCurrencyCode(countryCurrencyCode);
        exchangeRate.setBaseCurrency(resolveOutputBaseCurrency(response, normalizedCurrencyCode));
        exchangeRate.setRates(extractFilteredRates(response, normalizedCurrencyCode));

        handleApiError(response, exchangeRate);

        return exchangeRate;
    }

    /**
     * Extract only supported currencies from API quotes.
     *
     * @param response the API response containing all quotes
     * @return map with only supported currencies
     */
    private Map<String, Double> extractFilteredRates(ExchangeRateApiResponse response, String countryCurrencyCode) {
        Map<String, Double> rawRates = resolveRates(response);
        if (rawRates.isEmpty() && !hasRatePayload(response)) {
            return null;
        }

        if (countryCurrencyCode == null || countryCurrencyCode.isBlank()) {
            return extractUsdBasedRates(response, rawRates);
        }

        Double usdToBaseCurrencyRate = findUsdToBaseCurrencyRate(response, rawRates, countryCurrencyCode);
        if (usdToBaseCurrencyRate == null || usdToBaseCurrencyRate == 0d) {
            return extractUsdBasedRates(response, rawRates);
        }

        Map<String, Double> filteredRates = new LinkedHashMap<>();
        String baseCurrency = resolveBaseCurrency(response);

        for (Map.Entry<String, Double> entry : rawRates.entrySet()) {
            String targetCurrency = extractCurrencyCode(entry.getKey(), baseCurrency, response);

            if (isSupported(targetCurrency) && !targetCurrency.equals(countryCurrencyCode)) {
                filteredRates.put(targetCurrency, entry.getValue() / usdToBaseCurrencyRate);
            }
        }

        return filteredRates;
    }

    private Map<String, Double> extractUsdBasedRates(ExchangeRateApiResponse response, Map<String, Double> rawRates) {
        Map<String, Double> filteredRates = new HashMap<>();
        String baseCurrency = resolveBaseCurrency(response);

        for (Map.Entry<String, Double> entry : rawRates.entrySet()) {
            String targetCurrency = extractCurrencyCode(entry.getKey(), baseCurrency, response);
            if (isSupported(targetCurrency)) {
                filteredRates.put(targetCurrency, entry.getValue());
            }
        }

        return filteredRates;
    }

    private boolean hasRatePayload(ExchangeRateApiResponse response) {
        return response.getRates() != null || response.getQuotes() != null;
    }

    private Double findUsdToBaseCurrencyRate(
            ExchangeRateApiResponse response,
            Map<String, Double> rawRates,
            String countryCurrencyCode
    ) {
        if ("USD".equals(countryCurrencyCode)) {
            return 1d;
        }

        String apiBaseCurrency = resolveBaseCurrency(response);
        for (Map.Entry<String, Double> entry : rawRates.entrySet()) {
            String targetCurrency = extractCurrencyCode(entry.getKey(), apiBaseCurrency, response);
            if (countryCurrencyCode.equals(targetCurrency)) {
                return entry.getValue();
            }
        }

        return null;
    }

    private Map<String, Double> resolveRates(ExchangeRateApiResponse response) {
        if (response.getRates() != null) {
            return response.getRates();
        }

        if (response.getQuotes() != null) {
            return response.getQuotes();
        }

        return Collections.emptyMap();
    }

    private String resolveBaseCurrency(ExchangeRateApiResponse response) {
        if (response.getBase() != null && !response.getBase().isBlank()) {
            return response.getBase();
        }

        return response.getSource();
    }

    private String resolveOutputBaseCurrency(ExchangeRateApiResponse response, String countryCurrencyCode) {
        if (countryCurrencyCode != null && !countryCurrencyCode.isBlank()) {
            return countryCurrencyCode;
        }

        return resolveBaseCurrency(response);
    }

    private String normalizeCurrencyCode(String currencyCode) {
        if (currencyCode == null || currencyCode.isBlank()) {
            return null;
        }

        return currencyCode.toUpperCase();
    }

    /**
     * Extract target currency from quote key (e.g., "USDBRL" -> "BRL").
     *
     * @param quoteKey the quote key from API
     * @param baseCurrency the source currency
     * @return the target currency code
     */
    private String extractCurrencyCode(String quoteKey, String baseCurrency, ExchangeRateApiResponse response) {
        if (response.getRates() != null) {
            return quoteKey;
        }

        if (baseCurrency == null || quoteKey == null || quoteKey.length() <= baseCurrency.length()) {
            return quoteKey;
        }

        return quoteKey.substring(baseCurrency.length());
    }

    /**
     * Check if currency is in the supported list.
     *
     * @param currency the currency code
     * @return true if supported, false otherwise
     */
    private boolean isSupported(String currency) {
        return supportedCurrencies.contains(currency);
    }

    /**
     * Handle API error response.
     *
     * @param response the API response
     * @param exchangeRate the domain model to set error on
     */
    private void handleApiError(ExchangeRateApiResponse response, ExchangeRateInfo exchangeRate) {
        if (response.getSuccess() == null || response.getSuccess()) {
            return;
        }

        exchangeRate.setError("Exchange rate API returned error. Check API key and request parameters.");
    }
}
