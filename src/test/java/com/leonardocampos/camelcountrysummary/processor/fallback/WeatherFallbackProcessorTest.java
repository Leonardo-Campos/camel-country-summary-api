package com.leonardocampos.camelcountrysummary.processor.fallback;

import com.leonardocampos.camelcountrysummary.domain.model.WeatherInfo;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.leonardocampos.camelcountrysummary.config.RouteConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherFallbackProcessorTest {

    @Mock
    private Exchange exchange;

    @Mock
    private Message message;

    @InjectMocks
    private WeatherFallbackProcessor processor;

    @Test
    void process_shouldSetWeatherInfoWithError() {
        when(exchange.getIn()).thenReturn(message);

        processor.process(exchange);

        verify(message).setHeader(HEADER_DATA_TYPE, DATA_TYPE_WEATHER);

        ArgumentCaptor<WeatherInfo> captor = ArgumentCaptor.forClass(WeatherInfo.class);
        verify(message).setBody(captor.capture());

        WeatherInfo weather = captor.getValue();
        assertNotNull(weather.getError());
        assertTrue(weather.getError().contains("unavailable"));
        assertNull(weather.getTemperature());
        assertNull(weather.getDescription());
    }
}
