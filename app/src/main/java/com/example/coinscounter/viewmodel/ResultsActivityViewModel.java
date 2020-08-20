package com.example.coinscounter.viewmodel;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.coinscounter.model.CoinCardItem;
import com.example.coinscounter.repository.Repository;

import java.util.List;

public class ResultsActivityViewModel extends ViewModel {
    private Repository repository;
    private LiveData<List<CoinCardItem>> coinCardItems;
    private LiveData<Float> sum;

    @ViewModelInject
    public ResultsActivityViewModel(Repository repository) {
        this.repository = repository;
        coinCardItems = repository.getCoinCardItems();
        sum = repository.getValueOfCoins();
    }

    public void recognizeCoins() {
        repository.recognizeCoins();
    }

    public LiveData<List<CoinCardItem>> getCoinCardItems() {
        return coinCardItems;
    }

    // TODO transformation to return string?
    public LiveData<Float> getValueOfCoins() {
        return sum;
    }

    public void deleteCoin(int coinCardItemPosition) {
        repository.deleteCoinCardItem(coinCardItemPosition);
    }

    public void updateCoinValue(CoinCardItem coinCardItem, int coinCardItemPosition) {
        repository.updateCoinCard(coinCardItem, coinCardItemPosition);
    }
}
