package com.example.coinscounter.repository;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.coinscounter.model.CoinCardItem;
import com.example.coinscounter.model.CoinRecognitionModel;
import com.example.coinscounter.model.CoinResults;
import com.example.coinscounter.utills.CoinMapper;
import com.example.coinscounter.utills.ImageProcessor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Repository {
    private final CoinMapper coinMapper;
    private final CoinRecognitionModel coinRecognitionModel;
    private final ImageProcessor imageProcessor;
    private List<Bitmap> croppedCoinsList;
    private List<CoinCardItem> coinCardItems;

    private MutableLiveData<Integer> numOfSelectedCoins = new MutableLiveData<>();
    private MutableLiveData<CoinResults> coinResults = new MutableLiveData<>();
    private MutableLiveData<Bitmap> imageToDisplay = new MutableLiveData<>();

    private static final String TAG = Repository.class.getName();

    @Inject
    public Repository(ImageProcessor imageProcessor, CoinRecognitionModel model, CoinMapper coinMapper) {
        this.coinRecognitionModel = model;
        this.imageProcessor = imageProcessor;
        this.coinMapper = coinMapper;
    }

    public LiveData<Bitmap> getImageToDisplay() {
        return imageToDisplay;
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

    public static float sumCoinValues(List<CoinCardItem> coins) {
        float sum = 0;

        if (coins != null) {
            for (CoinCardItem coin : coins) {
                sum += coin.getValue();
            }
        }

        return sum;
    }

    public LiveData<CoinResults> getCoinResults() {
        return coinResults;
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
                    coinCardItems = returnedCoinCardItems;
                    setNewCoinResults();
                }
            }
        });
    }

    private void setNewCoinResults() {
        float floatSum = sumCoinValues(coinCardItems);
        String formattedSum = coinMapper.formatFloatValueSumToString(floatSum);
        coinResults.setValue(new CoinResults(coinCardItems, formattedSum));
    }

    public void updateCoinCard(CoinCardItem coinCardItem, int position) {
        coinCardItems.set(position, coinCardItem);
        setNewCoinResults();
    }

    public void deleteCoinCardItem(int position) {
        coinCardItems.remove(position);
        setNewCoinResults();
    }
}
