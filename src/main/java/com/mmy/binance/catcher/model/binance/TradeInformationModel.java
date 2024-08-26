package com.mmy.binance.catcher.model.binance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeInformationModel {
    //{"e":"trade","E":1724608571882,"s":"AVAXUSDT","t":289223855,"p":"27.03000000","q":"3.79000000","T":1724608571882,"m":false,"M":true}
    @JsonProperty("s")
    private String symbol;

    @JsonProperty("q")
    private BigDecimal amount;

    @JsonProperty("p")
    private BigDecimal price;

    @JsonProperty("T")
    private Long time;


}
