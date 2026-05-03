package com.leonardocampos.camelcountrysummary.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CountrySummary {

    private String country;
    private String capital;
    private String currency;
    private String currencyCode;
    private Double latitude;
    private Double longitude;
    private WeatherInfo weather;
    private ExchangeRateInfo exchangeRate;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public WeatherInfo getWeather() {
        return weather;
    }

    public void setWeather(WeatherInfo weather) {
        this.weather = weather;
    }

    public ExchangeRateInfo getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(ExchangeRateInfo exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class WeatherInfo {
        private String description;
        private Double temperature;
        private Double feelsLike;
        private Integer humidity;
        private Double windSpeed;
        private String error;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Double getTemperature() {
            return temperature;
        }

        public void setTemperature(Double temperature) {
            this.temperature = temperature;
        }

        public Double getFeelsLike() {
            return feelsLike;
        }

        public void setFeelsLike(Double feelsLike) {
            this.feelsLike = feelsLike;
        }

        public Integer getHumidity() {
            return humidity;
        }

        public void setHumidity(Integer humidity) {
            this.humidity = humidity;
        }

        public Double getWindSpeed() {
            return windSpeed;
        }

        public void setWindSpeed(Double windSpeed) {
            this.windSpeed = windSpeed;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ExchangeRateInfo {
        private String baseCurrency;
        private Map<String, Double> rates;
        private String error;

        public String getBaseCurrency() {
            return baseCurrency;
        }

        public void setBaseCurrency(String baseCurrency) {
            this.baseCurrency = baseCurrency;
        }

        public Map<String, Double> getRates() {
            return rates;
        }

        public void setRates(Map<String, Double> rates) {
            this.rates = rates;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}
