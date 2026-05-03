package com.leonardocampos.camelcountrysummary.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leonardocampos.camelcountrysummary.model.CountrySummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RestCountriesServiceTest {

    private RestCountriesService service;

    @BeforeEach
    void setUp() {
        service = new RestCountriesService(new ObjectMapper());
    }

    @Test
    void parseResponse_shouldExtractAllFields() throws Exception {
        String json = """
                [{
                    "name": {"common": "Brazil", "official": "Federative Republic of Brazil"},
                    "capital": ["Brasília"],
                    "currencies": {"BRL": {"name": "Brazilian real", "symbol": "R$"}},
                    "latlng": [-10.0, -55.0]
                }]
                """;

        CountrySummary summary = service.parseResponse(json);

        assertEquals("Brazil", summary.getCountry());
        assertEquals("Brasília", summary.getCapital());
        assertEquals("BRL", summary.getCurrencyCode());
        assertEquals("Brazilian real", summary.getCurrency());
        assertEquals(-10.0, summary.getLatitude());
        assertEquals(-55.0, summary.getLongitude());
    }

    @Test
    void parseResponse_shouldHandleMultipleCurrencies() throws Exception {
        String json = """
                [{
                    "name": {"common": "Test"},
                    "capital": ["Capital"],
                    "currencies": {
                        "USD": {"name": "US Dollar", "symbol": "$"},
                        "EUR": {"name": "Euro", "symbol": "€"}
                    },
                    "latlng": [0.0, 0.0]
                }]
                """;

        CountrySummary summary = service.parseResponse(json);

        assertNotNull(summary.getCurrencyCode());
        assertNotNull(summary.getCurrency());
    }

    @Test
    void parseResponse_shouldHandleMissingCapital() throws Exception {
        String json = """
                [{
                    "name": {"common": "Test"},
                    "currencies": {"USD": {"name": "US Dollar", "symbol": "$"}},
                    "latlng": [0.0, 0.0]
                }]
                """;

        CountrySummary summary = service.parseResponse(json);

        assertNull(summary.getCapital());
        assertEquals("Test", summary.getCountry());
    }

    @Test
    void parseResponse_shouldHandleEmptyCapitalList() throws Exception {
        String json = """
                [{
                    "name": {"common": "Test"},
                    "capital": [],
                    "currencies": {"USD": {"name": "US Dollar", "symbol": "$"}},
                    "latlng": [0.0, 0.0]
                }]
                """;

        CountrySummary summary = service.parseResponse(json);

        assertNull(summary.getCapital());
    }

    @Test
    void parseResponse_shouldHandleMissingCurrencies() throws Exception {
        String json = """
                [{
                    "name": {"common": "Test"},
                    "capital": ["Capital"],
                    "latlng": [0.0, 0.0]
                }]
                """;

        CountrySummary summary = service.parseResponse(json);

        assertNull(summary.getCurrencyCode());
        assertNull(summary.getCurrency());
    }

    @Test
    void parseResponse_shouldHandleMissingLatlng() throws Exception {
        String json = """
                [{
                    "name": {"common": "Test"},
                    "capital": ["Capital"],
                    "currencies": {"USD": {"name": "US Dollar", "symbol": "$"}}
                }]
                """;

        CountrySummary summary = service.parseResponse(json);

        assertNull(summary.getLatitude());
        assertNull(summary.getLongitude());
    }

    @Test
    void parseResponse_shouldHandleMissingName() throws Exception {
        String json = """
                [{
                    "capital": ["Capital"],
                    "currencies": {"USD": {"name": "US Dollar", "symbol": "$"}},
                    "latlng": [0.0, 0.0]
                }]
                """;

        CountrySummary summary = service.parseResponse(json);

        assertNull(summary.getCountry());
    }

    @Test
    void parseResponse_shouldUseFirstCountryFromArray() throws Exception {
        String json = """
                [
                    {"name": {"common": "First"}, "capital": ["Cap1"], "currencies": {"USD": {"name": "Dollar", "symbol": "$"}}, "latlng": [1.0, 2.0]},
                    {"name": {"common": "Second"}, "capital": ["Cap2"], "currencies": {"EUR": {"name": "Euro", "symbol": "€"}}, "latlng": [3.0, 4.0]}
                ]
                """;

        CountrySummary summary = service.parseResponse(json);

        assertEquals("First", summary.getCountry());
        assertEquals("Cap1", summary.getCapital());
    }
}
