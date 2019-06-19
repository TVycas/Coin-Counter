package com.example.coinscounter.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.coinscounter.tflite.Classifier;
import com.example.coinscounter.tflite.Classifier.Device;

import java.io.IOException;

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

    public MutableLiveData<Classifier> getClassifier(){
        configureClassifier();
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

    private void configureClassifier() {
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
            classifier = Classifier.create(Device.valueOf("GPU"), 3);
        } catch (IOException e) {
            Log.e(String.valueOf(e), "Failed to create classifier.");
        }
    }


}
