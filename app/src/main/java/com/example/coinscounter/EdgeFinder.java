package com.example.coinscounter;

import android.graphics.Bitmap;

public class EdgeFinder {

    private static final String TAG = "EdgeFinder";
    private Bitmap img;
    private MainActivity main;

    public EdgeFinder(Bitmap image, MainActivity main) {
        this.img = image;
        this.main = main;
//        new BackgroundImageResize(img);
    }


    public void setBitmap(Bitmap bitmap) {
        this.img = bitmap;
    }

}
