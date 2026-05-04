package com.lumina.android.service;

import com.lumina.android.model.ConversionResult;
import com.lumina.android.model.ExchangeRate;
import java.util.Map;

public interface CurrencyService {
    void convertCurrency(String from, String to, double amount, Callback<ConversionResult> callback);
    void getLatestRates(String base, Callback<Map<String, Double>> callback);
    void getHistoricalRates(String from, String to, int days, Callback<Map<String, Map<String, Double>>> callback);

    interface Callback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}
