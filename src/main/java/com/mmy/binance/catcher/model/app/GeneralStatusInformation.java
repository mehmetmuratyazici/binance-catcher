package com.mmy.binance.catcher.model.app;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class GeneralStatusInformation {

    private boolean isBuy;
    private boolean isSell;

    private BigDecimal purchasePrice;

}
