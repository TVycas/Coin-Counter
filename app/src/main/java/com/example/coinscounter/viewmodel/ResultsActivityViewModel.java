package com.example.coinscounter.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.coinscounter.model.Model;
import com.example.coinscounter.utills.CoinCardItem;

import java.util.ArrayList;

public class ResultsActivityViewModel extends AndroidViewModel {

    public static final String TAG = "ResultsActivityViewModel";
    private MutableLiveData<ArrayList<CoinCardItem>> coinCardList;
    private MutableLiveData<Float> sum;
    private Model model;

    public ResultsActivityViewModel(@NonNull Application application) {
        super(application);

        model = Model.getInstance(application);

        coinCardList = model.getCardList();
        sum = model.getSum();
    }

    public LiveData<ArrayList<CoinCardItem>> getCardList(){
        return coinCardList;
    }

    public LiveData<Float> getSum(){
        return sum;
    }


}
