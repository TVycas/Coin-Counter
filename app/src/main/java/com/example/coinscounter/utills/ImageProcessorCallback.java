package com.example.coinscounter.utills;

import android.graphics.Bitmap;

import java.util.List;

public interface ImageProcessorCallback {
    void onComplete(Bitmap imageToDisplay, List<Bitmap> croppedCoinsList);
}
