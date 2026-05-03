package com.leonardocampos.camelcountrysummary.strategy;

import com.leonardocampos.camelcountrysummary.model.CountrySummary;
import com.leonardocampos.camelcountrysummary.model.ExchangeRateInfo;
import com.leonardocampos.camelcountrysummary.model.WeatherInfo;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.leonardocampos.camelcountrysummary.config.RouteConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountrySummaryAggregationStrategyTest {

    private CountrySummaryAggregationStrategy strategy;

    @Mock
    private Exchange oldExchange;

    @Mock
    private Exchange newExchange;

    @Mock
    private Message oldMessage;

    @Mock
    private Message newMessage;

    @BeforeEach
    void setUp() {
        strategy = new CountrySummaryAggregationStrategy();
    }

    @Test
    void aggregate_firstExchange_shouldCreateNewSummaryWithWeather() {
        WeatherInfo weather = new WeatherInfo();
        weather.setDescription("clear sky");
        CountrySummary summary = new CountrySummary();
        summary.setCountry("Brazil");

        when(newExchange.getIn()).thenReturn(newMessage);
        when(newMessage.getHeader(HEADER_COUNTRY_SUMMARY, CountrySummary.class)).thenReturn(summary);
        when(newMessage.getHeader(HEADER_DATA_TYPE, String.class)).thenReturn(DATA_TYPE_WEATHER);
        when(newMessage.getBody()).thenReturn(weather);

        Exchange result = strategy.aggregate(null, newExchange);

        assertSame(newExchange, result);
        verify(newMessage).setBody(summary);
        assertEquals("clear sky", summary.getWeather().getDescription());
    }

    @Test
    void aggregate_firstExchange_shouldHandleNullSummaryHeader() {
        WeatherInfo weather = new WeatherInfo();

        when(newExchange.getIn()).thenReturn(newMessage);
        when(newMessage.getHeader(HEADER_COUNTRY_SUMMARY, CountrySummary.class)).thenReturn(null);
        when(newMessage.getHeader(HEADER_DATA_TYPE, String.class)).thenReturn(DATA_TYPE_WEATHER);
        when(newMessage.getBody()).thenReturn(weather);

        Exchange result = strategy.aggregate(null, newExchange);

        assertSame(newExchange, result);
    }

    @Test
    void aggregate_secondExchange_shouldMergeExchangeRate() {
        CountrySummary summary = new CountrySummary();
        summary.setCountry("Brazil");
        ExchangeRateInfo exchangeRate = new ExchangeRateInfo();
        exchangeRate.setBaseCurrency("BRL");

        when(oldExchange.getIn()).thenReturn(oldMessage);
        when(oldMessage.getHeader(HEADER_COUNTRY_SUMMARY, CountrySummary.class)).thenReturn(summary);
        when(newExchange.getIn()).thenReturn(newMessage);
        when(newMessage.getHeader(HEADER_DATA_TYPE, String.class)).thenReturn(DATA_TYPE_EXCHANGE_RATE);
        when(newMessage.getBody()).thenReturn(exchangeRate);

        Exchange result = strategy.aggregate(oldExchange, newExchange);

        assertSame(oldExchange, result);
        verify(oldMessage).setBody(summary);
        assertEquals("BRL", summary.getExchangeRate().getBaseCurrency());
    }

    @Test
    void aggregate_shouldHandleUnknownDataType() {
        CountrySummary summary = new CountrySummary();

        when(oldExchange.getIn()).thenReturn(oldMessage);
        when(oldMessage.getHeader(HEADER_COUNTRY_SUMMARY, CountrySummary.class)).thenReturn(summary);
        when(newExchange.getIn()).thenReturn(newMessage);
        when(newMessage.getHeader(HEADER_DATA_TYPE, String.class)).thenReturn("unknown");
        when(newMessage.getBody()).thenReturn("some data");

        Exchange result = strategy.aggregate(oldExchange, newExchange);

        assertSame(oldExchange, result);
        assertNull(summary.getWeather());
        assertNull(summary.getExchangeRate());
    }

    @Test
    void aggregate_secondExchange_shouldHandleNullSummaryHeader() {
        ExchangeRateInfo exchangeRate = new ExchangeRateInfo();

        when(oldExchange.getIn()).thenReturn(oldMessage);
        when(oldMessage.getHeader(HEADER_COUNTRY_SUMMARY, CountrySummary.class)).thenReturn(null);
        when(newExchange.getIn()).thenReturn(newMessage);
        when(newMessage.getHeader(HEADER_DATA_TYPE, String.class)).thenReturn(DATA_TYPE_EXCHANGE_RATE);
        when(newMessage.getBody()).thenReturn(exchangeRate);

        Exchange result = strategy.aggregate(oldExchange, newExchange);

        assertSame(oldExchange, result);
    }
}
