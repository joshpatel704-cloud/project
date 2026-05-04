package com.lumina.android.model;

public class ConversionRequest {
    private String from;
    private String to;
    private double amount;

    public ConversionRequest(String from, String to, double amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }
}
