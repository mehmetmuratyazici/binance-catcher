package com.mmy.binance.catcher.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketInitialRequest {
    private String firstPart;
    private String secondPart;

    public String getSymbol() {
        return this.firstPart + this.secondPart;
    }
}
