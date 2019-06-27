package com.example.coinscounter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.coinscounter.utills.CoinCardItem;
import com.example.coinscounter.viewmodel.ResultsActivityViewModel;
import com.example.coinscounter.viewmodel.UpdateCoinValueViewModel;

public class UpdateCoinValueActivity extends AppCompatActivity {

    private static final String TAG = "UpdateCoinValueActivity";
    private UpdateCoinValueViewModel viewModel;
    private int coinCardPosition;
    private ImageView coinImageView;
    private Button addValueButton;
    private Button subValueButton;
    private TextView valueTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_coin_value);

        viewModel = ViewModelProviders.of(this).get(UpdateCoinValueViewModel.class);

        coinCardPosition = getIntent().getExtras().getInt("CoinCardItemPosition");
        viewModel.init(coinCardPosition);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = (int) (dm.widthPixels*0.9);
        int height = (int) (dm.heightPixels*.6);

        getWindow().setLayout(width, height);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);

        coinImageView = findViewById(R.id.coinImage);
        addValueButton = findViewById(R.id.addValuebutton);
        subValueButton = findViewById(R.id.subValuebutton);
        valueTextView = findViewById(R.id.coinValuetextView);

        coinImageView.setImageBitmap(viewModel.getImageBitmap());
        viewModel.getCoinValueStr().observe(this, (coinValueStr) -> valueTextView.setText(coinValueStr));
    }


    public void addValue(View view) {
        viewModel.addValue();
    }

    public void subValue(View view) {
        viewModel.subValue();
    }

    @Override
    protected void onDestroy() {
        viewModel.updateCoinValue();
        Log.d(TAG, "OnDestroy");
        super.onDestroy();
    }
}
