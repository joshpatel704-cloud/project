package com.lumina.android.service;

import com.lumina.android.model.ConversionRequest;
import com.lumina.android.model.ConversionResponse;
import com.lumina.android.model.ConversionResult;
import com.lumina.android.network.ApiClient;
import com.lumina.android.model.HistoricalRatesResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrencyServiceImpl implements CurrencyService {

    @Override
    public void convertCurrency(String from, String to, double amount, Callback<ConversionResult> callback) {
        ApiClient.getInstance().getService().convert(new ConversionRequest(from, to, amount)).enqueue(new retrofit2.Callback<ConversionResponse>() {
            @Override
            public void onResponse(Call<ConversionResponse> call, Response<ConversionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    double result = response.body().getResult();
                    double rate = result / amount;
                    callback.onSuccess(new ConversionResult(amount, result, rate));
                } else {
                    callback.onError("Conversion failed");
                }
            }

            @Override
            public void onFailure(Call<ConversionResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    @Override
    public void getLatestRates(String base, Callback<Map<String, Double>> callback) {
        // Implementation for latest rates if needed
    }

    @Override
    public void getHistoricalRates(String from, String to, int days, Callback<Map<String, Map<String, Double>>> callback) {
        ApiClient.getInstance().getService().getHistoricalRates(from, to, days).enqueue(new retrofit2.Callback<HistoricalRatesResponse>() {
            @Override
            public void onResponse(Call<HistoricalRatesResponse> call, Response<HistoricalRatesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch historical rates");
                }
            }

            @Override
            public void onFailure(Call<HistoricalRatesResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
