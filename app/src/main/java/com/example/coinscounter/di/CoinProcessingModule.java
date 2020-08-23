package com.example.coinscounter.di;

import android.app.Application;
import android.util.DisplayMetrics;

import com.example.coinscounter.coinprocessing.ImageProcessor;
import com.example.coinscounter.model.CoinMapper;
import com.example.coinscounter.model.EuroCoinMapper;
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
public class CoinProcessingModule {

    @Provides
    @Singleton
    public static ImageProcessor provideImageProcessor(Application application) {
        DisplayMetrics dm = application.getResources().getDisplayMetrics();

        return new ImageProcessor(dm.widthPixels, Executors.newCachedThreadPool());
    }

    @Provides
    @Singleton
    public static ImageLabeler provideImageLabeler() {
        AutoMLImageLabelerLocalModel localModel =
                new AutoMLImageLabelerLocalModel.Builder()
                        .setAssetFilePath("tflite_models/manifest.json")
                        .build();

        AutoMLImageLabelerOptions autoMLImageLabelerOptions =
                new AutoMLImageLabelerOptions.Builder(localModel)
                        .setConfidenceThreshold(0.35f)
                        .build();

        return ImageLabeling.getClient(autoMLImageLabelerOptions);
    }

    @Provides
    @Singleton
    public static CoinMapper provideEuroCoinMapper(Application application) {
        return new EuroCoinMapper(application.getApplicationContext());
    }
}
