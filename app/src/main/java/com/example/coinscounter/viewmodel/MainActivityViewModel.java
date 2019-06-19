package com.example.coinscounter.viewmodel;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coinscounter.repository.CoinsRecognitionRepository;
import com.example.coinscounter.tflite.Classifier;

public class MainActivityViewModel extends ViewModel {
    private CoinsRecognitionRepository repo;
    private MutableLiveData<Classifier> coinsRecognitionModel;

    public void init(){
        if(coinsRecognitionModel != null){
            return;
        }
        repo = CoinsRecognitionRepository.getInstance();
        coinsRecognitionModel = repo.getClassifier();
    }

    public void setNewPicture (Bitmap bitmap){

    }




}
