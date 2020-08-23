package com.example.coinscounter.model;

import android.graphics.Bitmap;

import com.example.coinscounter.utills.CoinMapper;

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

    public void subtractOne() {
        value = coinMapper.decrementValue(value);
        name = coinMapper.mapFloatValueToString(value);
    }

    public void addOne() {
        value = coinMapper.incrementValue(value);
        name = coinMapper.mapFloatValueToString(value);
    }
}
