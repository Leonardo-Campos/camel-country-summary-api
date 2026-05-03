package com.leonardocampos.camelcountrysummary.service.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leonardocampos.camelcountrysummary.domain.model.CountrySummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountrySummaryCacheServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private CountrySummaryCacheService cacheService;

    @BeforeEach
    void setUp() {
        cacheService = new CountrySummaryCacheService(redisTemplate, new ObjectMapper(), 30);
    }

    @Test
    void get_shouldReturnCachedSummaryWhenValueExists() {
        CountrySummary summary = new CountrySummary();
        summary.setCountry("Brazil");

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("country-summary:brazil")).thenReturn("{\"country\":\"Brazil\"}");

        Optional<CountrySummary> cachedSummary = cacheService.get("Brazil");

        assertTrue(cachedSummary.isPresent());
        assertEquals("Brazil", cachedSummary.get().getCountry());
    }

    @Test
    void get_shouldReturnEmptyWhenRedisHasNoValue() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("country-summary:japan")).thenReturn(null);

        Optional<CountrySummary> cachedSummary = cacheService.get("Japan");

        assertTrue(cachedSummary.isEmpty());
    }

    @Test
    void put_shouldSerializeAndStoreSummary() {
        CountrySummary summary = new CountrySummary();
        summary.setCountry("Germany");

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        cacheService.put("Germany", summary);

        verify(valueOperations).set(eq("country-summary:germany"), any(String.class), any());
    }
}
