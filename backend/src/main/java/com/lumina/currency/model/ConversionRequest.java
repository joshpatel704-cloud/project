package com.lumina.currency.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversionRequest {
    private String from;
    private String to;
    private Double amount;
}
