package com.leonardocampos.camelcountrysummary.service.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leonardocampos.camelcountrysummary.domain.model.CountrySummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Locale;
import java.util.Optional;

@Service
public class CountrySummaryCacheService {

    private static final Logger LOG = LoggerFactory.getLogger(CountrySummaryCacheService.class);
    private static final String CACHE_KEY_PREFIX = "country-summary:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final Duration ttl;

    public CountrySummaryCacheService(
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper,
            @Value("${cache.redis.ttl-minutes:30}") long ttlMinutes
    ) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.ttl = Duration.ofMinutes(ttlMinutes);
    }

    public Optional<CountrySummary> get(String countryName) {
        try {
            String cachedValue = redisTemplate.opsForValue().get(buildKey(countryName));
            if (cachedValue == null || cachedValue.isBlank()) {
                return Optional.empty();
            }

            CountrySummary countrySummary = objectMapper.readValue(cachedValue, CountrySummary.class);
            return Optional.of(countrySummary);
        } catch (Exception exception) {
            LOG.warn("Redis cache lookup failed for country {}", countryName, exception);
            return Optional.empty();
        }
    }

    public void put(String countryName, CountrySummary countrySummary) {
        try {
            String serializedValue = objectMapper.writeValueAsString(countrySummary);
            redisTemplate.opsForValue().set(buildKey(countryName), serializedValue, ttl);
        } catch (Exception exception) {
            LOG.warn("Redis cache write failed for country {}", countryName, exception);
        }
    }

    private String buildKey(String countryName) {
        return CACHE_KEY_PREFIX + normalizeCountryName(countryName);
    }

    private String normalizeCountryName(String countryName) {
        return countryName == null ? "" : countryName.trim().toLowerCase(Locale.ROOT);
    }
}
