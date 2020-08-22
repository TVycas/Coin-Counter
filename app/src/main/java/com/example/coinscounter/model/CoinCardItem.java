package com.example.coinscounter.model;

import android.graphics.Bitmap;

import com.example.coinscounter.utills.EuroCoins;

public class CoinCardItem {
    private Bitmap imageResource;
    private String name;
    private float value;

    public CoinCardItem(Bitmap imageResource, String name, float value) {
        this.imageResource = imageResource;
        this.name = name;
        this.value = value;
    }

    public Bitmap getImageBitmap() {
        return imageResource;
    }

    public String getName() {
        return name;
    }

    public float getValue() {
        return value;
    }

    public void subtractOne() {
        int currentPosition = EuroCoins.floatList.indexOf(value);
        if (currentPosition > 0) {
            value = EuroCoins.floatList.get(currentPosition - 1);
            name = EuroCoins.mapFloatValueToEuroString(value);
        }
    }

    public void addOne() {
        int currentPosition = EuroCoins.floatList.indexOf(value);
        if (currentPosition < EuroCoins.floatList.size() - 1) {
            value = EuroCoins.floatList.get(currentPosition + 1);
            name = EuroCoins.mapFloatValueToEuroString(value);
        }
    }
}
