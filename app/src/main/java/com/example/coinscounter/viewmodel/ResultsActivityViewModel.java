package com.example.coinscounter.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.coinscounter.model.CoinCardItem;
import com.example.coinscounter.model.CoinRecognitionModel;

import java.util.ArrayList;

public class ResultsActivityViewModel extends AndroidViewModel {

    public static final String TAG = "ResultsActivityViewModel";
    private MutableLiveData<ArrayList<CoinCardItem>> coinCardList;
    private MutableLiveData<Float> sum;
    private CoinRecognitionModel coinRecognitionModel;

    public ResultsActivityViewModel(@NonNull Application application) {
        super(application);

        coinCardList = coinRecognitionModel.getCardList();
        sum = coinRecognitionModel.getSum();
    }

    public LiveData<ArrayList<CoinCardItem>> getCardList(){
        return coinCardList;
    }

    public LiveData<Float> getSum(){
        return sum;
    }


}
