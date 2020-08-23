package com.example.coinscounter.coinprocessing;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.coinscounter.model.CoinCardItem;
import com.example.coinscounter.model.CoinMapper;
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
    private CoinMapper coinMapper;

    @Inject
    public CoinRecognitionModel(ImageLabeler imageLabeler, CoinMapper coinMapper) {
        this.coinMapper = coinMapper;
        classifier = imageLabeler;
    }

    public void recognizeCoins(List<Bitmap> croppedCoinsList, CoinRecognitionCallback callback) {
        for (Bitmap coinBitmap : croppedCoinsList) {
            Bitmap scaledCoinBitmap = Bitmap.createScaledBitmap(coinBitmap, 150, 150, false);
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

                                Log.v(TAG, "onSuccess: text: " + predictedClass + "; confidence: " + confidence + "; index: " + index);
                                CoinCardItem coinCardItem = new CoinCardItem(coinBitmap, predictedClass, coinMapper);

                                callback.onPrediction(coinCardItem);
                            } else {
                                callback.onPrediction(null);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Failed to classify coin");
                            callback.onPrediction(null);
                        }
                    });
        }
    }

    public interface CoinRecognitionCallback {
        void onPrediction(CoinCardItem coinCardItem);
    }

}
