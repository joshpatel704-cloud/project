package com.lumina.android.controller;

import com.lumina.android.model.ConversionResult;
import com.lumina.android.service.CurrencyService;
import com.lumina.android.service.CurrencyServiceImpl;

public class CurrencyController {
    private final CurrencyService currencyService;
    private final View view;

    public interface View {
        void showConversionResult(ConversionResult result);
        void showLoading(boolean loading);
        void showError(String message);
    }

    public CurrencyController(View view) {
        this.view = view;
        this.currencyService = new CurrencyServiceImpl(); // In real app, use Dependency Injection
    }

    public void convert(String from, String to, double amount) {
        view.showLoading(true);
        currencyService.convertCurrency(from, to, amount, new CurrencyService.Callback<ConversionResult>() {
            @Override
            public void onSuccess(ConversionResult result) {
                view.showLoading(false);
                view.showConversionResult(result);
            }

            @Override
            public void onError(String error) {
                view.showLoading(false);
                view.showError(error);
            }
        });
    }
}
