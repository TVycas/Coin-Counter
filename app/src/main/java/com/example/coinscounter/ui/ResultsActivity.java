package com.example.coinscounter.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coinscounter.R;
import com.example.coinscounter.adapters.CoinCardAdapter;
import com.example.coinscounter.viewmodel.ResultsActivityViewModel;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ResultsActivity extends AppCompatActivity {

    public static final String TAG = "ResultsActivity";
    private ResultsActivityViewModel viewModel;
    private RecyclerView recyclerView;
    private TextView sumTextView;
    private CoinCardAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        viewModel = ViewModelProviders.of(this).get(ResultsActivityViewModel.class);

        sumTextView = findViewById(R.id.sumView);
        recyclerView = findViewById(R.id.recyclerView);

//        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new CoinCardAdapter();

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        viewModel.getCoinCardItems().observe(this, (cardList) -> adapter.setCoins(cardList));

        viewModel.getValueOfCoins().observe(this, (sum) -> {
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.HALF_UP);
            sumTextView.setText(df.format(sum) + " €");
        });

        adapter.setOnItemClickListener(new CoinCardAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int coinCardItemPosition) {
                Intent intent = new Intent(ResultsActivity.this, UpdateCoinValueActivity.class);
                intent.putExtra("CoinCardItemPosition", coinCardItemPosition);
                startActivity(intent);
            }
        });
    }
}