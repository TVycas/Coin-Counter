package com.example.coinscounter.repository;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.coinscounter.model.CoinCardItem;
import com.example.coinscounter.model.CoinRecognitionModel;
import com.example.coinscounter.utills.EuroCoins;
import com.example.coinscounter.utills.ImageProcessor;
import com.example.coinscounter.utills.ImageProcessorCallback;

import java.util.List;

import javax.inject.Inject;


public class Repository {
    private CoinRecognitionModel coinRecognitionModel;
    private ImageProcessor imageProcessor;
    private List<Bitmap> croppedCoinsList;
    private MutableLiveData<List<CoinCardItem>> coinCardItems = new MutableLiveData<>();
    private MutableLiveData<Float> valueOfCoins = new MutableLiveData<>();
    private MutableLiveData<Bitmap> imageToDisplay = new MutableLiveData<>();

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

    public void processCoinImage(Bitmap image, int lowerThreshold, int minDist) {
        imageProcessor.processImage(image, lowerThreshold, minDist, new ImageProcessorCallback() {
            @Override
            public void onComplete(Bitmap processedImage, List<Bitmap> croppedCoins) {
                croppedCoinsList = croppedCoins;
                imageToDisplay.postValue(processedImage);
            }
        });
    }

    public void recognizeCoins() {
        coinCardItems.setValue(coinRecognitionModel.recognizeCoins(croppedCoinsList));
        valueOfCoins.setValue(CoinRecognitionModel.calculateCoinValue(coinCardItems.getValue()));
    }

    public void updateCoinCard(int position, float value) {
        CoinCardItem oldItem = coinCardItems.getValue().get(position);
        // TODO just change the old item instead of creating a new one?
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
}
