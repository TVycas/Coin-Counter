package com.example.coinscounter.viewmodel;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.coinscounter.model.CoinCardItem;
import com.example.coinscounter.model.CoinRecognitionModel;
import com.example.coinscounter.utills.EuroCoins;

import java.util.ArrayList;

public class UpdateCoinValueViewModel extends AndroidViewModel {

    public static final String TAG = "UpdateCoinValueViewModel";
    private CoinRecognitionModel coinRecognitionModel;
    private Float initCoinValue;
    private Float newCoinValue;
    private int coinCardPosition;
    private Bitmap coinBitmap;
    private MutableLiveData<String> coinValueStr = new MutableLiveData<>();
    private MutableLiveData<ArrayList<CoinCardItem>> coinCardList;

    public UpdateCoinValueViewModel(@NonNull Application application) {
        super(application);

        coinCardList = coinRecognitionModel.getCardList();
    }

    public void init(int coinCardPosition) {
        this.coinCardPosition = coinCardPosition;
        CoinCardItem coinCardItem = coinCardList.getValue().get(coinCardPosition);
        this.initCoinValue = coinCardItem.getValue();
        newCoinValue = initCoinValue;
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
        }catch (Exception e){}
        coinValueStr.setValue(EuroCoins.floatToStringMap.get(newCoinValue));
    }

    public void subValue() {
        int currentPosition = EuroCoins.floatList.indexOf(newCoinValue);
        try {
            newCoinValue = EuroCoins.floatList.get(currentPosition - 1);
        }catch (Exception e){}
        coinValueStr.setValue(EuroCoins.floatToStringMap.get(newCoinValue));
    }

    public void deteleItem() {
//        coinRecognitionModel.deleteCoinCardItem(coinCardPosition);
    }
}
