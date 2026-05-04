package com.lumina.android.model;

public class ConversionResult {
    private double inputAmount;
    private double convertedAmount;
    private double rate;

    public ConversionResult(double inputAmount, double convertedAmount, double rate) {
        this.inputAmount = inputAmount;
        this.convertedAmount = convertedAmount;
        this.rate = rate;
    }

    public double getInputAmount() { return inputAmount; }
    public double getConvertedAmount() { return convertedAmount; }
    public double getRate() { return rate; }
}
