package com.example.coinscounter.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.coinscounter.tflite.Classifier;
import com.example.coinscounter.tflite.Classifier.Device;

import java.io.IOException;
import java.nio.MappedByteBuffer;


public class Repository {
    private static final String TAG = "Repository";
    private static Repository instance;
    private Classifier classifier;

    /**
     * Singleton pattern
     */
    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

    public String getModelPath() {
        return "thirdModel.tflite";
    }

    public MutableLiveData<Classifier> getClassifier(MappedByteBuffer modelFile) {
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
        try {
            Log.d(TAG, "Creating classifier");
            classifier = Classifier.create(modelFile, Device.valueOf("CPU"), 4);
        } catch (IOException e) {
            Log.e(String.valueOf(e), "Failed to create classifier.");
        }
    }
}
