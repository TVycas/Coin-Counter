package com.example.coinscounter.viewmodel;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.coinscounter.model.CoinCardItem;
import com.example.coinscounter.model.CoinResults;
import com.example.coinscounter.repository.Repository;

public class ResultsActivityViewModel extends ViewModel {
    private Repository repository;
    private LiveData<CoinResults> coinResults;

    @ViewModelInject
    public ResultsActivityViewModel(Repository repository) {
        this.repository = repository;
        coinResults = repository.getCoinResults();
    }

    public void recognizeCoins() {
        repository.recognizeCoins();
    }

    public LiveData<CoinResults> getCoinResults() {
        return coinResults;
    }

    public void deleteCoin(int coinCardItemPosition) {
        repository.deleteCoinCardItem(coinCardItemPosition);
    }

    public void updateCoinValue(CoinCardItem coinCardItem, int coinCardItemPosition) {
        repository.updateCoinCard(coinCardItem, coinCardItemPosition);
    }
}
