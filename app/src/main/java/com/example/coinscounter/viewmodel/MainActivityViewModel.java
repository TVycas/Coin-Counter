package com.example.coinscounter.viewmodel;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.coinscounter.model.Model;
import com.example.coinscounter.utills.ImageProcessing;

import java.io.InputStream;


public class MainActivityViewModel extends AndroidViewModel {
    private static final String TAG = "MainActivityViewModel";
    private Model model;
    private MutableLiveData<Bitmap> processedBitmap;
    private int threshSeekProgress;
    private int distSeekProgress;
    private String photoPath;
    private Bitmap photoBitmap;
    private ImageProcessing currentProcessing = null;


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

    public void setImagePath(String photoPath) {
        this.photoPath = photoPath;
    }

    public void setPhotoFromStream(InputStream photoInputStream) {
        this.photoBitmap = BitmapFactory.decodeStream(photoInputStream);
    }

    public LiveData<Bitmap> getProcessedBitmap() {
        return processedBitmap;
    }

    public void findCirclesInImage(boolean fromPath, int screenWidth) {
        if(currentProcessing != null){
            currentProcessing.cancel(true);
        }

        if(fromPath) {
            currentProcessing = (ImageProcessing) new ImageProcessing(model, screenWidth, threshSeekProgress, distSeekProgress).execute(BitmapFactory.decodeFile(photoPath));
        }else{
            currentProcessing = (ImageProcessing) new ImageProcessing(model, screenWidth, threshSeekProgress, distSeekProgress).execute(photoBitmap);
        }
    }

    public boolean calculateSum() {
        return model.calculateSum();
    }
}