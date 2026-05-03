package com.leonardocampos.camelcountrysummary.model.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RestCountryResponse {

    private CountryName name;
    private List<String> capital;
    private Map<String, CurrencyInfo> currencies;
    private List<Double> latlng;

    public CountryName getName() {
        return name;
    }

    public void setName(CountryName name) {
        this.name = name;
    }

    public List<String> getCapital() {
        return capital;
    }

    public void setCapital(List<String> capital) {
        this.capital = capital;
    }

    public Map<String, CurrencyInfo> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(Map<String, CurrencyInfo> currencies) {
        this.currencies = currencies;
    }

    public List<Double> getLatlng() {
        return latlng;
    }

    public void setLatlng(List<Double> latlng) {
        this.latlng = latlng;
    }
}
