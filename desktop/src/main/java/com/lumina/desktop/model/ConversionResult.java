package com.lumina.desktop.model;

public class ConversionResult {
    private final double amount;
    private final double rate;
    private final double result;

    public ConversionResult(double amount, double rate, double result) {
        this.amount = amount;
        this.rate = rate;
        this.result = result;
    }

    public double getAmount() { return amount; }
    public double getRate() { return rate; }
    public double getResult() { return result; }
}
