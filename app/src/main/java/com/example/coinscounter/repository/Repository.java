package com.example.coinscounter.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.coinscounter.tflite.Classifier;
import com.example.coinscounter.tflite.Classifier.Device;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerLocalModel;
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerOptions;

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
        return "tflite_models/thirdModel.tflite";
    }

    public MutableLiveData<ImageLabeler> getClassifier() {
//        configureClassifier(modelFile);

        AutoMLImageLabelerLocalModel localModel =
                new AutoMLImageLabelerLocalModel.Builder()
                        .setAssetFilePath("tflite_models/manifest.json")
                        // or .setAbsoluteFilePath(absolute file path to manifest file)
                        .build();

        AutoMLImageLabelerOptions autoMLImageLabelerOptions =
                new AutoMLImageLabelerOptions.Builder(localModel)
                        .setConfidenceThreshold(0.35f)  // Evaluate your model in the Firebase console
                        // to determine an appropriate value.
                        .build();
        ImageLabeler labeler = ImageLabeling.getClient(autoMLImageLabelerOptions);

        MutableLiveData<ImageLabeler> data = new MutableLiveData<>();
        data.setValue(labeler);

        return data;
    }


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
