package com.lumina.currency.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConversionResponse {
    private String from;
    private String to;
    private Double amount;
    private Double result;
    private Double rate;
    private LocalDateTime timestamp;
}
