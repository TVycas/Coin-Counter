package com.example.coinscounter.model;

import java.util.List;

public class CoinResults {

    private List<CoinCardItem> coinCardItems;
    private String valueSum;

    public CoinResults(List<CoinCardItem> coinCardItems, String valueSum) {
        this.coinCardItems = coinCardItems;
        this.valueSum = valueSum;
    }

    public List<CoinCardItem> getCoinCardItems() {
        return coinCardItems;
    }

    public String getValueSum() {
        return valueSum;
    }
}
