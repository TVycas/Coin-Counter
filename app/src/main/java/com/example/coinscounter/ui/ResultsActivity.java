package com.example.coinscounter.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
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
import com.example.coinscounter.model.CoinResults;
import com.example.coinscounter.viewmodel.ResultsActivityViewModel;

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
    private ProgressBar progressBar;
    private View divider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        sumTextView = findViewById(R.id.sumView);
        progressBar = findViewById(R.id.progress_circular);
        divider = findViewById(R.id.divider);

        viewModel = new ViewModelProvider(this).get(ResultsActivityViewModel.class);
        viewModel.recognizeCoins();

        viewModel.getCoinResults().observe(this, new Observer<CoinResults>() {
            @Override
            public void onChanged(CoinResults coinResults) {
                coinCardItems = coinResults.getCoinCardItems();
                adapter.setCoins(coinCardItems);
                sumTextView.setText(getString(R.string.total_value, coinResults.getValueSum()));

                makeResultsVisible();
            }
        });

        setUpRecyclerView();
    }

    private void makeResultsVisible() {
        progressBar.setVisibility(View.GONE);
        sumTextView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        sumTextView.setVisibility(View.VISIBLE);
        divider.setVisibility(View.VISIBLE);
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
