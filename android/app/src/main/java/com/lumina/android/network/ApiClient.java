package com.lumina.android.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static ApiClient instance;
    private final CurrencyService service;

    private ApiClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/") // Standard Android emulator localhost
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(CurrencyService.class);
    }

    public static synchronized ApiClient getInstance() {
        if (instance == null) instance = new ApiClient();
        return instance;
    }

    public CurrencyService getService() {
        return service;
    }
}
