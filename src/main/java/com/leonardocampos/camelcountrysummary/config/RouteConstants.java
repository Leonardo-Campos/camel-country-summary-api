package com.leonardocampos.camelcountrysummary.config;

public final class RouteConstants {

    private RouteConstants() {
    }

    // Route endpoints
    public static final String DIRECT_COUNTRY_SUMMARY = "direct:countrySummary";
    public static final String DIRECT_FETCH_COUNTRY = "direct:fetchCountryInfo";
    public static final String DIRECT_FETCH_WEATHER = "direct:fetchWeather";
    public static final String DIRECT_FETCH_EXCHANGE_RATE = "direct:fetchExchangeRate";
    public static final String DIRECT_ENRICH_DATA = "direct:enrichWithExternalData";

    // Header names
    public static final String HEADER_COUNTRY_NAME = "countryName";
    public static final String HEADER_COUNTRY_SUMMARY = "countrySummary";
    public static final String HEADER_CURRENCY_CODE = "currencyCode";
    public static final String HEADER_LATITUDE = "latitude";
    public static final String HEADER_LONGITUDE = "longitude";
    public static final String HEADER_DATA_TYPE = "dataType";

    // Data type identifiers
    public static final String DATA_TYPE_WEATHER = "weather";
    public static final String DATA_TYPE_EXCHANGE_RATE = "exchangeRate";
}
