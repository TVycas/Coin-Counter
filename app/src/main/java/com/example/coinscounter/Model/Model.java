package com.example.coinscounter.Model;

import android.app.Application;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.coinscounter.utills.EuroCoins;
import com.example.coinscounter.repository.Repository;
import com.example.coinscounter.tflite.Classifier;
import com.example.coinscounter.utills.CoinCardItem;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class Model {
    private static final String TAG = "Model";
    private static Model instance;
    private Repository repo;
    private MutableLiveData<Classifier> classifier;
    private MutableLiveData<ArrayList<CoinCardItem>> results = new MutableLiveData<>();
    private MutableLiveData<Bitmap> processedBitmap = new MutableLiveData<>();
    private MutableLiveData<Float> sum = new MutableLiveData<>();
    private List<Bitmap> croppedPhotosList = new ArrayList<>();
    private String photoPath;
    private Mat circles;
    private Mat processedMat;

    public static Model getInstance(Application application) {
        if (instance == null) {
            instance = new Model(application);
        }
        return instance;
    }

    private Model(Application application) {
        repo = Repository.getInstance();
        try {
            classifier = repo.getClassifier(loadModelFile(application));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MutableLiveData<ArrayList<CoinCardItem>> getCardList() {
        return results;
    }

    public MutableLiveData<Float> getSum() {
        return sum;
    }

    public MutableLiveData<Bitmap> getProcessedBitmap() {
        return processedBitmap;
    }

    public void setProcessedMat(Mat mat) {
        processedMat = mat;
    }

    public void setProcessedBitmap(Bitmap bitmap) {
        processedBitmap.setValue(bitmap);
    }


    public void setCircles(Mat circles) {
        this.circles = circles;
    }


    public void setCurrentPhotoPath(String currentPhotoPath) {
        this.photoPath = currentPhotoPath;
    }

    public String getPhotoPath() {
        return photoPath;
    }


    private MappedByteBuffer loadModelFile(Application application) throws IOException {
        AssetFileDescriptor fileDescriptor = application.getAssets().openFd(repo.getModelPath());
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public void saveCoins() throws IOException {
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/CoinsCounter/SavedCoins";

        Log.d(TAG, file_path);
        File dir = new File(file_path);

        if (!dir.exists())
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
            Rect coinRect = new Rect(x, y, radius * 2, radius * 2);
            Mat croppedMat = new Mat(processedMat, coinRect);

            //Convert the Mat to Bitmap
            Bitmap croppedCoin = Bitmap.createBitmap(croppedMat.cols(), croppedMat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(croppedMat, croppedCoin);

            //Save the bitmap to the list for the ML model
            croppedPhotosList.add(croppedCoin);

            //Save the cropped coin bitmap to disk
            File file = new File(dir, "CroppedCoin_" + System.currentTimeMillis() + ".png");
            fOut = new FileOutputStream(file);
            croppedCoin.compress(Bitmap.CompressFormat.PNG, 85, fOut);

        }

        if (fOut != null)
            fOut.flush();

        fOut.close();

        Log.d(TAG, "number of saved coins = " + savedCoinsCount);
    }


    public boolean calculateSum() {
        //reset data
        if (results.getValue() != null) {
            croppedPhotosList.clear();
            sum.setValue(0f);
        }
        if (!circles.empty()) {
            //Save the coins that we're processing for the future
            try {
                saveCoins();
            } catch (IOException e) {
                e.printStackTrace();
            }

            results.setValue(new ArrayList<>());
            sum.setValue(0f);

            for (Bitmap coin : croppedPhotosList) {
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(coin, 75, 75, false);
                List<Classifier.Recognition> recognitions = classifier.getValue().recognizeImage(scaledBitmap);
                Classifier.Recognition rec = recognitions.get(0);
                Log.d(TAG, rec.getTitle() + " -- " + rec.getConfidence() + " -- " + rec.getId() + "  --- " + EuroCoins.stringToFloatMap.get(rec.getTitle()));
                results.getValue().add(new CoinCardItem(scaledBitmap, rec.getTitle(), EuroCoins.stringToFloatMap.get(rec.getTitle())));
            }

            Log.i(TAG, "Recognition values: ");

            addResultsItems();

            Log.i(TAG, "Sum = " + sum.getValue());

            return true;
        } else {
            return false;
        }

    }

    private void addResultsItems() {
        for (CoinCardItem rec : results.getValue()) {
            Log.i(TAG, rec.getName());
            sum.setValue(sum.getValue() + rec.getValue());
            Log.i(TAG, sum.getValue().toString());
        }
        sum.setValue(sum.getValue());
    }

    public void updateCoinCard(int position, float value) {
        CoinCardItem oldItem = results.getValue().get(position);
        CoinCardItem newItem = new CoinCardItem(oldItem.getImageBitmap(), EuroCoins.floatToStringMap.get(value), value);
        results.getValue().set(position, newItem);
    }
}
