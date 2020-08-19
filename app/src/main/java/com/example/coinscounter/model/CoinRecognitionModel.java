package com.example.coinscounter.model;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class CoinRecognitionModel {
    private static final String TAG = "CoinRecognitionModel";
    private static CoinRecognitionModel instance;
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

    public ArrayList<CoinCardItem> recognizeCoins(List<Bitmap> croppedCoinsList) {
        ArrayList<CoinCardItem> coinCardItems = new ArrayList<>();
        for (Bitmap coin : croppedCoinsList) {
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(coin, 150, 150, false);

            int rotationDegree = 0;
            InputImage image = InputImage.fromBitmap(scaledBitmap, rotationDegree);

            classifier.process(image)
                    .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                        @Override
                        public void onSuccess(List<ImageLabel> labels) {
                            for (ImageLabel label : labels) {
                                String text = label.getText();
                                float confidence = label.getConfidence();
                                int index = label.getIndex();

                                //TODO figure out how to construct the Coin objects from the results
                                Log.d(TAG, "onSuccess: text: " + text + "; confidence: " + confidence + "; index: " + index);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Failed to classify image");
                        }
                    });
        }
        return coinCardItems;
    }
}
