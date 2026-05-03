package com.leonardocampos.camelcountrysummary.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Configuration for Exchange Rate API filtering.
 * Defines which currencies should be included in the response.
 */
public class ExchangeRateConfig {

    private static final Set<String> SUPPORTED_CURRENCIES =
            new HashSet<>(Arrays.asList("USD", "EUR", "GBP", "JPY", "BRL"));

    private ExchangeRateConfig() {
        throw new AssertionError("Configuration class should not be instantiated");
    }

    public static Set<String> supportedCurrencies() {
        return Set.copyOf(SUPPORTED_CURRENCIES);
    }
}

