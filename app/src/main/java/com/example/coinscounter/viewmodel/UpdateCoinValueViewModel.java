package com.example.coinscounter.viewmodel;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coinscounter.model.CoinCardItem;
import com.example.coinscounter.repository.Repository;
import com.example.coinscounter.utills.EuroCoins;

import java.util.List;

public class UpdateCoinValueViewModel extends ViewModel {
    private Repository repository;
    private Float newCoinValue;
    private Bitmap coinBitmap;
    private MutableLiveData<String> coinValueStr = new MutableLiveData<>();
    private LiveData<List<CoinCardItem>> coinCardItems;

    public UpdateCoinValueViewModel(Repository repository) {
        this.repository = repository;
        coinCardItems = repository.getCoinCardItems();
    }

    public void init(int coinCardPosition) {
        CoinCardItem coinCardItem = coinCardItems.getValue().get(coinCardPosition);
        newCoinValue = coinCardItem.getValue();
        this.coinBitmap = coinCardItem.getImageBitmap();
        this.coinValueStr.setValue(coinCardItem.getName());
    }

    public void updateCoinValue() {
//        if (!newCoinValue.equals(initCoinValue))
//            coinRecognitionModel.updateCoinCard(coinCardPosition, newCoinValue);
    }

    public LiveData<String> getCoinValueStr() {
        return coinValueStr;
    }

    public Bitmap getImageBitmap() {
        return coinBitmap;
    }

    public void addValue() {
        int currentPosition = EuroCoins.floatList.indexOf(newCoinValue);
        try {
            newCoinValue = EuroCoins.floatList.get(currentPosition + 1);
        } catch (Exception e) {
        }
        coinValueStr.setValue(EuroCoins.floatToStringMap.get(newCoinValue));
    }

    public void subValue() {
        int currentPosition = EuroCoins.floatList.indexOf(newCoinValue);
        try {
            newCoinValue = EuroCoins.floatList.get(currentPosition - 1);
        } catch (Exception e) {
        }
        coinValueStr.setValue(EuroCoins.floatToStringMap.get(newCoinValue));
    }

    public void deleteItem() {
//        coinRecognitionModel.deleteCoinCardItem(coinCardPosition);
    }
}
