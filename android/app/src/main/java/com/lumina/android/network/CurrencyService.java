package com.lumina.android.network;

import com.lumina.android.model.ConversionRequest;
import com.lumina.android.model.ConversionResponse;
import com.lumina.android.model.HistoricalRatesResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CurrencyService {
    @POST("api/v1/currency/convert")
    Call<ConversionResponse> convert(@Body ConversionRequest request);

    @GET("api/v1/currency/historical-rates")
    Call<HistoricalRatesResponse> getHistoricalRates(@Query("base") String base, @Query("symbol") String symbol, @Query("days") int days);
}
