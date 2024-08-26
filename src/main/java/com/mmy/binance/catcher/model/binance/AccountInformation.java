package com.mmy.binance.catcher.model.binance;

import lombok.Data;

import java.util.List;

@Data
public class AccountInformation {
    private List<Balance> balances;
}


