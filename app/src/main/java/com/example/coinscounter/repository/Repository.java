package com.example.coinscounter.repository;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.coinscounter.model.CoinCardItem;
import com.example.coinscounter.model.CoinRecognitionModel;
import com.example.coinscounter.utills.EuroCoins;
import com.example.coinscounter.utills.ImageProcessor;
import com.example.coinscounter.utills.ImageProcessorCallback;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerLocalModel;
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerOptions;

import java.util.List;
import java.util.concurrent.Executor;


public class Repository {
    private static Repository instance;
    private MutableLiveData<List<CoinCardItem>> coinCardItems;
    private MutableLiveData<Float> valueOfCoins;
    private CoinRecognitionModel coinRecognitionModel;
    private ImageProcessor imageProcessor;
    private MutableLiveData<Bitmap> imageToDisplay;
    private List<Bitmap> croppedCoinsList;

    /**
     * Singleton pattern
     */
    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

    private Repository(int displayWidthPixels, Executor executor) {
        // TODO inject classifier
        coinRecognitionModel = CoinRecognitionModel.getInstance(getClassifier());

        imageProcessor = new ImageProcessor(displayWidthPixels, executor, new ImageProcessorCallback() {
            @Override
            public void onComplete(Bitmap processedImage, List<Bitmap> croppedCoins) {
                croppedCoinsList = croppedCoins;
                imageToDisplay.postValue(processedImage);
            }
        });
    }

    public LiveData<Bitmap> getImageToDisplay() {
        return imageToDisplay;
    }

    private LiveData<List<CoinCardItem>> getCoinCards() {
        return coinCardItems;
    }

    public MutableLiveData<Float> getValueOfCoins() {
        return valueOfCoins;
    }

    private void processCoinImage(Bitmap image, int lowerThreshold, int minDist) {
        imageProcessor.processImage(image, lowerThreshold, minDist);
    }

    public void recognizeCoins() {
        coinCardItems.setValue(coinRecognitionModel.recognizeCoins(croppedCoinsList));
        valueOfCoins.setValue(CoinRecognitionModel.calculateCoinValue(coinCardItems.getValue()));
    }

    public void updateCoinCard(int position, float value) {
        CoinCardItem oldItem = coinCardItems.getValue().get(position);
        CoinCardItem newItem = new CoinCardItem(oldItem.getImageBitmap(), EuroCoins.floatToStringMap.get(value), value);

        List<CoinCardItem> newCoinList = coinCardItems.getValue();

        newCoinList.set(position, newItem);
        coinCardItems.setValue(newCoinList);
    }

    public void deleteCoinCardItem(int position) {
        List<CoinCardItem> newCoinList = coinCardItems.getValue();

        newCoinList.remove(position);
        coinCardItems.setValue(newCoinList);
    }

    public ImageLabeler getClassifier() {
        AutoMLImageLabelerLocalModel localModel =
                new AutoMLImageLabelerLocalModel.Builder()
                        .setAssetFilePath("tflite_models/manifest.json")
                        .build();

        AutoMLImageLabelerOptions autoMLImageLabelerOptions =
                new AutoMLImageLabelerOptions.Builder(localModel)
                        .setConfidenceThreshold(0.35f)
                        .build();

        return ImageLabeling.getClient(autoMLImageLabelerOptions);
    }
}
