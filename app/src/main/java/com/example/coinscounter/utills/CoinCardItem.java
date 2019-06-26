package com.example.coinscounter.utills;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class CoinCardItem {
    private Bitmap imageResource;
    private String name;
    private float value;

    public CoinCardItem(Bitmap imageResource, String name, float value){
        this.imageResource = imageResource;
        this.name = name;
        this.value = value;
    }

    public Bitmap getImageBitmap(){
        return imageResource;
    }

    public String getName(){
        return name;
    }

    public float getValue(){ return value; }
}
