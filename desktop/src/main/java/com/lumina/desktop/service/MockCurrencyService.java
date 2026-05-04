package com.lumina.desktop.service;

import com.lumina.desktop.model.ConversionResult;
import java.util.concurrent.CompletableFuture;

public class MockCurrencyService implements CurrencyService {
    @Override
    public CompletableFuture<ConversionResult> convert(String from, String to, double amount) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(500); // Simulate network
            } catch (InterruptedException e) e.printStackTrace();
            
            double rate = 0.9423; // Mock rate
            return new ConversionResult(amount, rate, amount * rate);
        });
    }
}
