package com.lumina.currency.service;

import com.lumina.currency.model.ConversionRequest;
import com.lumina.currency.model.ConversionResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class CurrencyService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String API_KEY = "YOUR_API_KEY"; // ExchangeRate-API or similar
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    public ConversionResponse convert(ConversionRequest request) {
        // In real app, fetch from external API or cache
        double mockRate = 0.94; 
        double result = request.getAmount() * mockRate;

        return ConversionResponse.builder()
                .from(request.getFrom())
                .to(request.getTo())
                .amount(request.getAmount())
                .result(result)
                .rate(mockRate)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public Map<String, Double> getLatestRates(String base) {
        // Implementation for External API call
        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 0.94);
        rates.put("GBP", 0.81);
        rates.put("JPY", 148.2);
        rates.put("AUD", 1.52);
        return rates;
    }

    public Map<String, Map<String, Double>> getHistoricalRates(String base, String symbol, int days) {
        // Simulate historical data for charts
        Map<String, Map<String, Double>> history = new HashMap<>();
        // Mocking...
        return history;
    }
}
