package com.leonardocampos.camelcountrysummary.service.exchangerate;

import com.leonardocampos.camelcountrysummary.domain.model.ExchangeRateInfo;

public interface IExchangeRateService {

    ExchangeRateInfo parseResponse(String json, String countryCurrencyCode) throws Exception;
}
