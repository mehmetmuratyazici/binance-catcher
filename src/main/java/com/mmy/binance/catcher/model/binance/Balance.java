package com.mmy.binance.catcher.model.binance;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Balance {
    private String asset;
    private BigDecimal free;
    private BigDecimal locked;
}
