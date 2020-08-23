package com.example.coinscounter.model;

import java.util.List;

/**
 * A class for encapsulating the return from coin recognition.
 */
public class CoinResults {

    /**
     * A list of recognized coins.
     */
    private List<CoinCardItem> coinCardItems;
    /**
     * The total value of the recognized coins.
     */
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
