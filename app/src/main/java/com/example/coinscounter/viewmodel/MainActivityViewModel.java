package com.example.coinscounter.viewmodel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.coinscounter.repository.Repository;

import java.io.InputStream;

import javax.inject.Singleton;

@Singleton
public class MainActivityViewModel extends ViewModel {
    private static final String TAG = MainActivityViewModel.class.getName();
    private LiveData<Bitmap> imageToDisplay;
    private LiveData<Integer> numOfSelectedCoins;
    private Repository repository;
    private Bitmap imageOfCoins = null;
    private int lowerThreshold = 50;
    private int minDistance = 50;

    @ViewModelInject
    public MainActivityViewModel(Repository repository) {
        this.repository = repository;
        imageToDisplay = repository.getImageToDisplay();
        numOfSelectedCoins = repository.getNumOfSelectedCoins();
    }

    public LiveData<Bitmap> getImageToDisplay() {
        return imageToDisplay;
    }

    public LiveData<Integer> getNumOfSelectedCoins() {
        return numOfSelectedCoins;
    }

    public void setImageOfCoins(InputStream photoInputStream) {
        Log.i(TAG, "processCoinImage: loading image from stream...");
        this.imageOfCoins = BitmapFactory.decodeStream(photoInputStream);
        processCoinImage();
    }

    public void setImageOfCoins(String photoPath) {
        Log.i(TAG, "processCoinImage: loading image from path...");
        this.imageOfCoins = BitmapFactory.decodeFile(photoPath);
        processCoinImage();
    }

    private void processCoinImage() {
        if (imageOfCoins != null) {
            repository.processCoinImage(imageOfCoins, lowerThreshold, minDistance);
        }
    }

    public void incThreshold() {
        lowerThreshold += 5;
        processCoinImage();
    }

    public void decThreshold() {
        lowerThreshold -= 5;
        processCoinImage();
    }

    public void decDistance() {
        minDistance -= 5;
        processCoinImage();
    }

    public void incDistance() {
        minDistance += 5;
        processCoinImage();
    }
}