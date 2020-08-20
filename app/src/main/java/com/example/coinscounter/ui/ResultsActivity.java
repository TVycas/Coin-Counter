package com.example.coinscounter.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coinscounter.R;
import com.example.coinscounter.adapters.CoinCardAdapter;
import com.example.coinscounter.model.CoinCardItem;
import com.example.coinscounter.viewmodel.ResultsActivityViewModel;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ResultsActivity extends AppCompatActivity implements UpdateCoinValueDialogFragment.UpdateCoinDialogListener {

    private static final String TAG = ResultsActivity.class.getName();
    private ResultsActivityViewModel viewModel;
    private RecyclerView recyclerView;
    private TextView sumTextView;
    private CoinCardAdapter adapter;
    private List<CoinCardItem> coinCardItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        sumTextView = findViewById(R.id.sumView);

        viewModel = new ViewModelProvider(this).get(ResultsActivityViewModel.class);

        viewModel.getCoinCardItems().observe(this, new Observer<List<CoinCardItem>>() {
            @Override
            public void onChanged(List<CoinCardItem> cardList) {
                coinCardItems = cardList;
                adapter.setCoins(cardList);
            }
        });

        viewModel.getValueOfCoins().observe(this, (sum) -> {
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.HALF_UP);
            sumTextView.setText(df.format(sum) + " €");
        });

        viewModel.recognizeCoins();

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        adapter = new CoinCardAdapter(new CoinCardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int coinCardItemPosition) {
                DialogFragment newFragment = new UpdateCoinValueDialogFragment(coinCardItems.get(coinCardItemPosition), coinCardItemPosition);
                newFragment.show(getSupportFragmentManager(), "update_coin_value");
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onUpdateCoin(CoinCardItem coinCardItem, int coinCardItemPosition) {
        viewModel.updateCoinValue(coinCardItem, coinCardItemPosition);
    }

    @Override
    public void onDeleteCoin(int coinCardItemPosition) {
        viewModel.deleteCoin(coinCardItemPosition);
    }
}
