package com.leonardocampos.camelcountrysummary.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leonardocampos.camelcountrysummary.model.ExchangeRateInfo;
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
                    "base": "USD",
                    "rates": {"EUR": 0.85, "GBP": 0.73, "JPY": 110.0},
                    "success": true
                }
                """;

        ExchangeRateInfo info = service.parseResponse(json);

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

        ExchangeRateInfo info = service.parseResponse(json);

        assertEquals("BRL", info.getBaseCurrency());
        assertTrue(info.getRates().isEmpty());
    }

    @Test
    void parseResponse_shouldHandleMissingRates() throws Exception {
        String json = """
                {
                    "base": "GBP",
                    "success": false
                }
                """;

        ExchangeRateInfo info = service.parseResponse(json);

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

        ExchangeRateInfo info = service.parseResponse(json);

        assertNull(info.getBaseCurrency());
        assertEquals(1, info.getRates().size());
    }

    @Test
    void parseResponse_shouldHandleMinimalResponse() throws Exception {
        String json = "{}";

        ExchangeRateInfo info = service.parseResponse(json);

        assertNull(info.getBaseCurrency());
        assertNull(info.getRates());
    }
}
