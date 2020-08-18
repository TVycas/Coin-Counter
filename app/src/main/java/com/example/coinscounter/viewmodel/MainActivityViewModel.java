package com.example.coinscounter.viewmodel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.coinscounter.repository.Repository;

import java.io.InputStream;


public class MainActivityViewModel extends ViewModel {
    private static final String TAG = MainActivityViewModel.class.getName();
    private LiveData<Bitmap> imageToDisplay;
    private Repository repository;

    public MainActivityViewModel(Repository repository) {
        this.repository = repository;
        imageToDisplay = repository.getImageToDisplay();
    }

    public LiveData<Bitmap> getImageToDisplay() {
        return imageToDisplay;
    }

    public void processCoinImage(InputStream photoInputStream, int lowerThreshold, int minDist) {
        Log.i(TAG, "processCoinImage: processing image from stream...");
        Bitmap imageOfCoins = BitmapFactory.decodeStream(photoInputStream);
        repository.processCoinImage(imageOfCoins, lowerThreshold, minDist);
    }

    public void processCoinImage(String photoPath, int lowerThreshold, int minDist) {
        Log.i(TAG, "processCoinImage: processing image from path...");
        Bitmap imageOfCoins = BitmapFactory.decodeFile(photoPath);
        repository.processCoinImage(imageOfCoins, lowerThreshold, minDist);
    }

}