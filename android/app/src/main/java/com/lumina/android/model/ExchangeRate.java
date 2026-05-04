package com.lumina.android.model;

import java.util.Date;

public class ExchangeRate {
    private String baseCurrency;
    private String targetCurrency;
    private double rate;
    private Date timestamp;

    public ExchangeRate(String baseCurrency, String targetCurrency, double rate) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
        this.timestamp = new Date();
    }

    public String getBaseCurrency() { return baseCurrency; }
    public String getTargetCurrency() { return targetCurrency; }
    public double getRate() { return rate; }
    public Date getTimestamp() { return timestamp; }
}
