package com.leonardocampos.camelcountrysummary.processor.response;

import com.leonardocampos.camelcountrysummary.domain.model.WeatherInfo;
import com.leonardocampos.camelcountrysummary.service.weather.WeatherService;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.leonardocampos.camelcountrysummary.config.RouteConstants.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherResponseProcessorTest {

    @Mock
    private WeatherService weatherService;

    @Mock
    private Exchange exchange;

    @Mock
    private Message message;

    @InjectMocks
    private WeatherResponseProcessor processor;

    @Test
    void process_shouldSetDataTypeHeaderAndBody() throws Exception {
        String body = "{\"weather\":[{\"description\":\"rain\"}]}";
        WeatherInfo weather = new WeatherInfo();
        weather.setDescription("rain");

        when(exchange.getIn()).thenReturn(message);
        when(message.getBody(String.class)).thenReturn(body);
        when(weatherService.parseResponse(body)).thenReturn(weather);

        processor.process(exchange);

        verify(message).setHeader(HEADER_DATA_TYPE, DATA_TYPE_WEATHER);
        verify(message).setBody(weather);
    }
}
