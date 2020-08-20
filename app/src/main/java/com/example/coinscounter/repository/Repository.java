package com.example.coinscounter.repository;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.coinscounter.model.CoinCardItem;
import com.example.coinscounter.model.CoinRecognitionModel;
import com.example.coinscounter.utills.ImageProcessor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Repository {
    private CoinRecognitionModel coinRecognitionModel;
    private ImageProcessor imageProcessor;
    private List<Bitmap> croppedCoinsList;

    private MutableLiveData<Integer> numOfSelectedCoins = new MutableLiveData<>();
    private MutableLiveData<List<CoinCardItem>> coinCardItems = new MutableLiveData<>();
    private MutableLiveData<Float> valueOfCoins = new MutableLiveData<>();
    private MutableLiveData<Bitmap> imageToDisplay = new MutableLiveData<>();

    private static final String TAG = Repository.class.getName();

    @Inject
    public Repository(ImageProcessor imageProcessor, CoinRecognitionModel model) {
        this.coinRecognitionModel = model;
        this.imageProcessor = imageProcessor;
    }

    public LiveData<Bitmap> getImageToDisplay() {
        return imageToDisplay;
    }

    public LiveData<List<CoinCardItem>> getCoinCardItems() {
        return coinCardItems;
    }

    public LiveData<Float> getValueOfCoins() {
        return valueOfCoins;
    }

    public LiveData<Integer> getNumOfSelectedCoins() {
        return numOfSelectedCoins;
    }

    public void processCoinImage(Bitmap image, int lowerThreshold, int minDist) {
        imageProcessor.processImage(image, lowerThreshold, minDist, new ImageProcessor.ImageProcessorCallback() {
            @Override
            public void onComplete(Bitmap processedImage, List<Bitmap> croppedCoins) {
                croppedCoinsList = croppedCoins;
                numOfSelectedCoins.postValue(croppedCoinsList.size());
                imageToDisplay.postValue(processedImage);
            }
        });
    }

    public void recognizeCoins() {
        coinRecognitionModel.recognizeCoins(croppedCoinsList, new CoinRecognitionModel.CoinRecognitionCallback() {
            private List<CoinCardItem> returnedCoinCardItems = new ArrayList<>();
            private int noPredictionCounter = 0;

            @Override
            public void onPrediction(CoinCardItem coinCardItem) {
                if (coinCardItem != null) {
                    returnedCoinCardItems.add(coinCardItem);
                } else {
                    noPredictionCounter++;
                }

                if (returnedCoinCardItems.size() + noPredictionCounter == croppedCoinsList.size()) {
                    Log.i(TAG, "onPrediction: Setting " + returnedCoinCardItems.size() + " predicted values of coins out of " + croppedCoinsList.size());
                    coinCardItems.setValue(returnedCoinCardItems);
                }
            }
        });

        if (coinCardItems.getValue() != null) {
            valueOfCoins.setValue(CoinRecognitionModel.calculateCoinValue(coinCardItems.getValue()));
        } else {
            valueOfCoins.setValue(0f);
        }
    }

    public void updateCoinCard(CoinCardItem coinCardItem, int position) {
        List<CoinCardItem> newCoinList = coinCardItems.getValue();

        newCoinList.set(position, coinCardItem);
        coinCardItems.setValue(newCoinList);
    }

    public void deleteCoinCardItem(int position) {
        List<CoinCardItem> newCoinList = coinCardItems.getValue();

        newCoinList.remove(position);
        coinCardItems.setValue(newCoinList);
    }
}
