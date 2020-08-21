package com.example.coinscounter.viewmodel;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.coinscounter.model.CoinCardItem;
import com.example.coinscounter.repository.Repository;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class ResultsActivityViewModel extends ViewModel {
    private Repository repository;
    private LiveData<List<CoinCardItem>> coinCardItems;
    private LiveData<String> formattedSum;

    @ViewModelInject
    public ResultsActivityViewModel(Repository repository) {
        this.repository = repository;
        coinCardItems = repository.getCoinCardItems();
        LiveData<Float> sumFloat = repository.getValueOfCoins();

        formattedSum = Transformations.map(sumFloat,
                sum -> {
                    DecimalFormat df = new DecimalFormat("#.##");
                    df.setRoundingMode(RoundingMode.HALF_UP);
                    return df.format(sum) + " €";
                });
    }

    public void recognizeCoins() {
        repository.recognizeCoins();
    }

    public LiveData<List<CoinCardItem>> getCoinCardItems() {
        return coinCardItems;
    }

    public LiveData<String> getValueOfCoins() {
        return formattedSum;
    }

    public void deleteCoin(int coinCardItemPosition) {
        repository.deleteCoinCardItem(coinCardItemPosition);
    }

    public void updateCoinValue(CoinCardItem coinCardItem, int coinCardItemPosition) {
        repository.updateCoinCard(coinCardItem, coinCardItemPosition);
    }
}
