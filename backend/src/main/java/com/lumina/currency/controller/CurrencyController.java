package com.lumina.currency.controller;

import com.lumina.currency.model.ConversionRequest;
import com.lumina.currency.model.ConversionResponse;
import com.lumina.currency.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/currency")
@CrossOrigin(origins = "*")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    @PostMapping("/convert")
    public ResponseEntity<ConversionResponse> convert(@RequestBody ConversionRequest request) {
        return ResponseEntity.ok(currencyService.convert(request));
    }

    @GetMapping("/latest-rates")
    public ResponseEntity<Map<String, Double>> getLatestRates(@RequestParam(defaultValue = "USD") String base) {
        return ResponseEntity.ok(currencyService.getLatestRates(base));
    }

    @GetMapping("/historical-rates")
    public ResponseEntity<Map<String, Map<String, Double>>> getHistoricalRates(
            @RequestParam String base,
            @RequestParam String symbol,
            @RequestParam int days) {
        return ResponseEntity.ok(currencyService.getHistoricalRates(base, symbol, days));
    }
}
