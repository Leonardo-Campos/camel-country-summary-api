package com.leonardocampos.camelcountrysummary.service.exchangerate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leonardocampos.camelcountrysummary.domain.model.ExchangeRateInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeRateServiceTest {

    private ExchangeRateService service;

    @BeforeEach
    void setUp() {
        service = new ExchangeRateService(new ObjectMapper());
    }

    @Test
    void parseResponse_shouldExtractBaseAndRates() throws Exception {
        String json = """
                {
                    "source": "USD",
                    "quotes": {"USDEUR": 0.85, "USDGBP": 0.73, "USDJPY": 110.0},
                    "success": true
                }
                """;

        ExchangeRateInfo info = service.parseResponse(json, "USD");

        assertEquals("USD", info.getBaseCurrency());
        assertEquals(3, info.getRates().size());
        assertEquals(0.85, info.getRates().get("EUR"));
        assertEquals(0.73, info.getRates().get("GBP"));
        assertEquals(110.0, info.getRates().get("JPY"));
    }

    @Test
    void parseResponse_shouldHandleEmptyRates() throws Exception {
        String json = """
                {
                    "base": "BRL",
                    "rates": {},
                    "success": true
                }
                """;

        ExchangeRateInfo info = service.parseResponse(json, "BRL");

        assertEquals("BRL", info.getBaseCurrency());
        assertTrue(info.getRates().isEmpty());
    }

    @Test
    void parseResponse_shouldHandleMissingRates() throws Exception {
        String json = """
                {
                    "source": "GBP",
                    "success": false
                }
                """;

        ExchangeRateInfo info = service.parseResponse(json, "GBP");

        assertEquals("GBP", info.getBaseCurrency());
        assertNull(info.getRates());
    }

    @Test
    void parseResponse_shouldHandleMissingBase() throws Exception {
        String json = """
                {
                    "rates": {"USD": 1.0},
                    "success": true
                }
                """;

        ExchangeRateInfo info = service.parseResponse(json, null);

        assertNull(info.getBaseCurrency());
        assertEquals(1, info.getRates().size());
    }

    @Test
    void parseResponse_shouldHandleMinimalResponse() throws Exception {
        String json = "{}";

        ExchangeRateInfo info = service.parseResponse(json, null);

        assertNull(info.getBaseCurrency());
        assertNull(info.getRates());
    }

    @Test
    void parseResponse_shouldConvertUsdRatesToCountryCurrencyBase() throws Exception {
        String json = """
                {
                    "source": "USD",
                    "quotes": {
                        "USDJPY": 156.905994,
                        "USDEUR": 0.85256,
                        "USDGBP": 0.736035,
                        "USDBRL": 4.953899
                    },
                    "success": true
                }
                """;

        ExchangeRateInfo info = service.parseResponse(json, "JPY");

        assertEquals("JPY", info.getBaseCurrency());
        assertEquals(3, info.getRates().size());
        assertEquals(0.0054335113, info.getRates().get("EUR"), 0.000001);
        assertEquals(0.0046909298, info.getRates().get("GBP"), 0.000001);
        assertEquals(0.0315724012, info.getRates().get("BRL"), 0.000001);
    }
}
