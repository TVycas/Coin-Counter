package com.example.coinscounter.viewmodel;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.coinscounter.model.Model;
import com.example.coinscounter.utills.ImageProcessing;



public class MainActivityViewModel extends AndroidViewModel {
    private static final String TAG = "MainActivityViewModel";
    private Model model;
    private MutableLiveData<Bitmap> processedBitmap;
    private int threshSeekProgress;
    private int distSeekProgress;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        model = Model.getInstance(application);
        processedBitmap = model.getProcessedBitmap();
    }

    public void saveThreshSeekProgress(int threshSeekProgress){
        this.threshSeekProgress = threshSeekProgress;
    }

    public void savetDistSeekProgress(int distSeekProgress){
        this.distSeekProgress = distSeekProgress;
    }

    public void setImagePath(String currentPhotoPath) {
        model.setCurrentPhotoPath(currentPhotoPath);
    }

    public LiveData<Bitmap> getProcessedBitmap() {
        return processedBitmap;
    }

    public void findCirclesInImage() {
        new ImageProcessing(model,700, threshSeekProgress, distSeekProgress).execute(BitmapFactory.decodeFile(model.getPhotoPath()));
    }

    public boolean calculateSum() {
        return model.calculateSum();
    }
}