package com.leonardocampos.camelcountrysummary.model;

import com.fasterxml.jackson.annotation.JsonInclude;

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
}
