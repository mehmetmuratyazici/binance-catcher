package com.mmy.binance.catcher.model.binance;

import lombok.Data;

import java.util.List;

@Data
public class AccountInformation {
    private List<Balance> balances;


    public Balance getBalance(String symbol) {
        return this.balances.parallelStream().filter(balance -> symbol.toLowerCase().indexOf(balance.getAsset().toLowerCase()) == 0).findFirst().get();
    }
}


