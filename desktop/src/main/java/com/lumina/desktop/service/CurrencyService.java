package com.lumina.desktop.service;

import com.lumina.desktop.model.ConversionResult;
import java.util.concurrent.CompletableFuture;

public interface CurrencyService {
    CompletableFuture<ConversionResult> convert(String from, String to, double amount);
}
