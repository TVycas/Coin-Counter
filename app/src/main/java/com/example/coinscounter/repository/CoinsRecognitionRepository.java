package com.example.coinscounter.repository;

import android.content.res.AssetManager;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.coinscounter.tflite.Classifier;
import com.example.coinscounter.tflite.Classifier.Device;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton pattern
 */
public class CoinsRecognitionRepository {
    private static final String TAG = "CoinsRecognitionRepository";
    private static CoinsRecognitionRepository instance;
    private Classifier classifier;

    public static CoinsRecognitionRepository getInstance(){
        if(instance == null){
            instance = new CoinsRecognitionRepository();
        }
        return instance;
    }

    public String getModelPath() {
        return "firstModel.tflite";
    }

    public MutableLiveData<Classifier> getClassifier(MappedByteBuffer modelFile){
        configureClassifier(modelFile);
        MutableLiveData<Classifier> data = new MutableLiveData<>();
        data.setValue(classifier);
        return data;
    }
    //todo?
//    @Override
//    protected void onInferenceConfigurationChanged() {
//        if (croppedBitmap == null) {
//            // Defer creation until we're getting camera frames.
//            return;
//        }
//        final Classifier.Device device = getDevice();
//        final Classifier.Model model = getModel();
//        final int numThreads = getNumThreads();
//        runInBackground(() -> recreateClassifier(model, device, numThreads));

//    }

    private void configureClassifier(MappedByteBuffer modelFile) {
        if (classifier != null) {
            Log.d(TAG, "Closing classifier.");
            classifier.close();
            classifier = null;
        }
        //TODO
//        if (device == Device.GPU && model == Model.QUANTIZED) {
//            Log.d("Not creating classifier: GPU doesn't support quantized models.");
//            runOnUiThread(
//                    () -> {
//                        Toast.makeText(this, "GPU does not yet supported quantized models.", Toast.LENGTH_LONG)
//                                .show();
//                    });
//            return;
//        }

        try {
            Log.d(TAG, "Creating classifier");
            classifier = Classifier.create(modelFile, Device.valueOf("CPU"), 4);
        } catch (IOException e) {
            Log.e(String.valueOf(e), "Failed to create classifier.");
        }
    }
}
