package com.example.coinscounter.model;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.coinscounter.utills.EuroCoins;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CoinRecognitionModel {
    private static final String TAG = CoinRecognitionModel.class.getName();
    private ImageLabeler classifier;

    @Inject
    public CoinRecognitionModel(ImageLabeler imageLabeler) {
        classifier = imageLabeler;
    }

    public static float calculateCoinValue(List<CoinCardItem> coins) {
        float sum = 0;

        for (CoinCardItem coin : coins) {
            sum += coin.getValue();
        }
        return sum;
    }

    public void recognizeCoins(List<Bitmap> croppedCoinsList, CoinRecognitionCallback callback) {
        for (Bitmap coin : croppedCoinsList) {
            Bitmap scaledCoinBitmap = Bitmap.createScaledBitmap(coin, 150, 150, false);
            int rotationDegree = 0;
            InputImage image = InputImage.fromBitmap(scaledCoinBitmap, rotationDegree);

            classifier.process(image)
                    .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                        @Override
                        public void onSuccess(List<ImageLabel> labels) {
                            if (labels.size() > 0) {
                                String predictedClass = labels.get(0).getText();
                                float confidence = labels.get(0).getConfidence();
                                int index = labels.get(0).getIndex();

                                Log.d(TAG, "onSuccess: text: " + predictedClass + "; confidence: " + confidence + "; index: " + index);
                                CoinCardItem coinCardItem = new CoinCardItem(coin, predictedClass, EuroCoins.stringToFloatMap.get(predictedClass));
                                callback.onPrediction(coinCardItem);
                            } else {
                                callback.onPrediction(null);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Failed to classify image");
                            callback.onPrediction(null);
                        }
                    });
        }
    }

    public interface CoinRecognitionCallback {
        void onPrediction(CoinCardItem coinCardItem);
    }

}
