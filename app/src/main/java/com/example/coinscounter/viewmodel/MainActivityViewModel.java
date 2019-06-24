package com.example.coinscounter.viewmodel;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coinscounter.repository.CoinsRecognitionRepository;
import com.example.coinscounter.tflite.Classifier;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivityViewModel extends ViewModel {
    static final String TAG = "MainActivityViewModel";
    private CoinsRecognitionRepository repo;
    private MutableLiveData<Classifier> coinsRecognitionModel;
    private String photoPath;
    private MutableLiveData<Bitmap> processedBitmap = new MutableLiveData<>();
    private Mat circles;
    private Mat processedMat;

    public void init(){
        if(coinsRecognitionModel != null){
            return;
        }
        repo = CoinsRecognitionRepository.getInstance();
        coinsRecognitionModel = repo.getClassifier();
    }

    public void setProcessedMat(Mat mat){
        processedMat = mat;
    }

    public void setImagePath(String currentPhotoPath) {
        this.photoPath = currentPhotoPath;
    }

    public String getPhotoPath(){ return photoPath; }

    public void setProcessedBitmap(Bitmap bitmap) {
        processedBitmap.setValue(bitmap);
    }

    public LiveData<Bitmap> getProcessedBitmap(){
        return processedBitmap;
    }

    public void setCircles(Mat circles) {
        this.circles = circles;
    }

    public void saveCoins() throws IOException {
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/CoinsCounter/SavedCoins";

        Log.d(TAG, file_path);
        File dir = new File(file_path);

        if(!dir.exists())
            dir.mkdirs();

        FileOutputStream fOut = null;

        int savedCoinsCount = 0;

        for (savedCoinsCount = 0; savedCoinsCount < circles.cols(); savedCoinsCount++) {
            Log.d(TAG, "Saving coin numb " + savedCoinsCount);
            double[] vCircle = circles.get(0, savedCoinsCount);

            Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
            int radius = (int) Math.round(vCircle[2]);

            //Add 5% to the radius to make a cropped box around the coin
            radius *= 1.05;

            //Starting point for Rect
            int x = (int) (pt.x - radius);
            int y = (int) (pt.y - radius);

            //Create a new mat to store the cropped coin image
            Rect coinRect = new Rect(x, y, radius*2, radius*2);
            Mat croppedMat = new Mat(processedMat, coinRect);

            //Convert the Mat to Bitmap
            Bitmap croppedCoin = Bitmap.createBitmap(croppedMat.cols(), croppedMat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(croppedMat, croppedCoin);

            //Save the cropped coin bitmap to disk
            File file = new File(dir, "CroppedCoin_" + System.currentTimeMillis() + ".png");
            fOut = new FileOutputStream(file);
            croppedCoin.compress(Bitmap.CompressFormat.PNG, 85, fOut);

        }

        if(fOut != null)
            fOut.flush();

        fOut.close();

        Log.d(TAG, "number of saved coins = " + savedCoinsCount);
    }

}
