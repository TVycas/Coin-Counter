package com.example.coinscounter.model;

import android.graphics.Bitmap;

/**
 * A class for storing information about a single coin.
 */
public class CoinCardItem {
    private Bitmap imageResource;
    private String name;
    private float value;
    private CoinMapper coinMapper;

    public CoinCardItem(Bitmap imageResource, String predictedClass, CoinMapper coinMapper) {
        this.value = coinMapper.mapPredictedClassToFloatValue(predictedClass);
        this.name = coinMapper.mapFloatValueToString(value);
        this.imageResource = imageResource;
        this.coinMapper = coinMapper;
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

    public void decrementValue() {
        value = coinMapper.decrementValue(value);
        name = coinMapper.mapFloatValueToString(value);
    }

    public void incrementValue() {
        value = coinMapper.incrementValue(value);
        name = coinMapper.mapFloatValueToString(value);
    }
}
