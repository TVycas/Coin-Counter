package com.example.coinscounter.viewmodel;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.coinscounter.model.CoinCardItem;
import com.example.coinscounter.repository.Repository;

import java.util.List;

public class ResultsActivityViewModel extends ViewModel {
    private LiveData<List<CoinCardItem>> coinCardItems;
    private LiveData<Float> sum;

    @ViewModelInject
    public ResultsActivityViewModel(Repository repository) {
        coinCardItems = repository.getCoinCardItems();
        sum = repository.getValueOfCoins();
    }

    public LiveData<List<CoinCardItem>> getCoinCardItems() {
        return coinCardItems;
    }

    // TODO transformation?
    public LiveData<Float> getValueOfCoins() {
        return sum;
    }
}
