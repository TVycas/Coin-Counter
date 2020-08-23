package com.example.coinscounter.repository;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.coinscounter.coinprocessing.CoinRecognitionModel;
import com.example.coinscounter.coinprocessing.ImageProcessor;
import com.example.coinscounter.model.CoinCardItem;
import com.example.coinscounter.model.CoinMapper;
import com.example.coinscounter.model.CoinResults;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Repository {
    private static final String TAG = Repository.class.getName();

    private final CoinMapper COIN_MAPPER;
    private final CoinRecognitionModel COIN_RECOGNITION_MODEL;
    private final ImageProcessor IMAGE_PROCESSOR;

    private List<Bitmap> croppedCoinsList;
    private List<CoinCardItem> coinCardItems;

    private MutableLiveData<Integer> numOfSelectedCoins = new MutableLiveData<>();
    private MutableLiveData<CoinResults> coinResults = new MutableLiveData<>();
    private MutableLiveData<Bitmap> imageToDisplay = new MutableLiveData<>();

    @Inject
    public Repository(ImageProcessor imageProcessor, CoinRecognitionModel model, CoinMapper coinMapper) {
        this.COIN_RECOGNITION_MODEL = model;
        this.IMAGE_PROCESSOR = imageProcessor;
        this.COIN_MAPPER = coinMapper;
    }

    public LiveData<Bitmap> getImageToDisplay() {
        return imageToDisplay;
    }

    public LiveData<Integer> getNumOfSelectedCoins() {
        return numOfSelectedCoins;
    }

    /**
     * Starts the async processing of the image to find coins. Stops any previous executions if they're still running.
     *
     * @param image          The image bitmap to process.
     * @param lowerThreshold The lower threshold for HoughCircles calculation.
     * @param minDist        The minimum distance between circles in HoughCircles calculation.
     */
    public void processCoinImage(Bitmap image, int lowerThreshold, int minDist) {
        IMAGE_PROCESSOR.cancelExecution();
        IMAGE_PROCESSOR.processImage(image, lowerThreshold, minDist, new ImageProcessor.ImageProcessorCallback() {
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

    /**
     * Uses the coin recognition model to recognize the values of the coins and sets coinCardItems to those values.
     */
    public void recognizeCoins() {
        COIN_RECOGNITION_MODEL.recognizeCoins(croppedCoinsList, new CoinRecognitionModel.CoinRecognitionCallback() {
            private List<CoinCardItem> returnedCoinCardItems = new ArrayList<>();
            /**
             * A counter for the coins that had no prediction from the model.
             */
            private int noPredictionCounter = 0;

            @Override
            public void onPrediction(CoinCardItem coinCardItem) {
                if (coinCardItem != null) {
                    returnedCoinCardItems.add(coinCardItem);
                } else {
                    noPredictionCounter++;
                }

                /*
                 *  Because the ML recognition is asynchronous, we need to wait until all of the coins that we've sent to recognize are processed.
                 */
                if (returnedCoinCardItems.size() + noPredictionCounter == croppedCoinsList.size()) {
                    Log.i(TAG, "onPrediction: Setting " + returnedCoinCardItems.size() + " predicted values of coins out of " + croppedCoinsList.size());
                    coinCardItems = returnedCoinCardItems;
                    setNewCoinResults();
                }
            }
        });
    }

    /**
     * Recalculates the sum of the coins and sets new value for coinResults MutableLiveData object
     */
    private void setNewCoinResults() {
        float floatSum = sumCoinValues(coinCardItems);
        String formattedSum = COIN_MAPPER.formatFloatValueSumToString(floatSum);
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
