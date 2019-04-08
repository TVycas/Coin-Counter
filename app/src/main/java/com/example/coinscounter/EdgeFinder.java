package com.example.coinscounter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EdgeFinder {

    private static final String TAG = "EdgeFinder";
    private Bitmap img;
    private MainActivity main;

    public EdgeFinder(Bitmap image,  MainActivity main) {
        this.img = image;
        this.main = main;
//        new BackgroundImageResize(img);
    }


    public void setBitmap(Bitmap bitmap){
        this.img = bitmap;
    }

}
