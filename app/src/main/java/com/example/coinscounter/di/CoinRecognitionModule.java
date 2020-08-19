package com.example.coinscounter.di;

import android.app.Application;
import android.util.DisplayMetrics;

import com.example.coinscounter.model.CoinRecognitionModel;
import com.example.coinscounter.utills.ImageProcessor;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerLocalModel;
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerOptions;

import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;

@Module
@InstallIn(ApplicationComponent.class)
public class CoinRecognitionModule {

    @Provides
    @Singleton
    public static ImageProcessor provideImageProcessor(Application application) {
        DisplayMetrics dm = application.getResources().getDisplayMetrics();

        return new ImageProcessor(dm.widthPixels, Executors.newCachedThreadPool());
    }

    @Provides
    @Singleton
    public static CoinRecognitionModel provideCoinRecognitionModel() {
        AutoMLImageLabelerLocalModel localModel =
                new AutoMLImageLabelerLocalModel.Builder()
                        .setAssetFilePath("tflite_models/manifest.json")
                        .build();

        AutoMLImageLabelerOptions autoMLImageLabelerOptions =
                new AutoMLImageLabelerOptions.Builder(localModel)
                        .setConfidenceThreshold(0.35f)
                        .build();

        ImageLabeler imageLabeler = ImageLabeling.getClient(autoMLImageLabelerOptions);

        return new CoinRecognitionModel(imageLabeler);
    }
}
