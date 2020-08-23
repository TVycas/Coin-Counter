package com.example.coinscounter.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.coinscounter.R;
import com.example.coinscounter.model.CoinCardItem;


public class UpdateCoinValueDialogFragment extends DialogFragment {
    private final CoinCardItem coinCardItem;
    private final int coinCardItemPosition;
    private UpdateCoinDialogListener listener;

    private TextView coinValueTextView;

    public UpdateCoinValueDialogFragment(CoinCardItem coinCardItem, int coinCardItemPosition) {
        this.coinCardItem = coinCardItem;
        this.coinCardItemPosition = coinCardItemPosition;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_update_coin_value_dialog, null);

        // Set the view and add buttons as well as listeners for the buttons.
        builder.setView(view)
                .setPositiveButton("Update Coin", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onUpdateCoin(coinCardItem, coinCardItemPosition);
                    }
                })
                .setNegativeButton("Delete Coin", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDeleteCoin(coinCardItemPosition);
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /* no-op */
                    }
                });

        coinValueTextView = view.findViewById(R.id.coin_value_textView);
        updateValueTextView();

        ImageView imageView = view.findViewById(R.id.coin_image);
        imageView.setImageBitmap(coinCardItem.getImageBitmap());

        setUpControlButtons(view);

        return builder.create();
    }

    /**
     * Finds buttons responsible for changing value of the coin and sets listeners for their onClick.
     *
     * @param view The view in which the buttons are initiated.
     */
    private void setUpControlButtons(View view) {
        Button subButton = view.findViewById(R.id.sub_value_button);
        Button addButton = view.findViewById(R.id.add_value_button);

        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coinCardItem.decrementValue();
                updateValueTextView();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coinCardItem.incrementValue();
                updateValueTextView();
            }
        });
    }

    private void updateValueTextView() {
        coinValueTextView.setText(coinCardItem.getName());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            // Instantiate the UpdateCoinDialogListener so we can send events to the host activity
            listener = (UpdateCoinDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement UpdateCoinDialogListener");
        }
    }

    /**
     * A simple interface to update the coin values that must be implemented by the calling activity.
     */
    public interface UpdateCoinDialogListener {
        void onUpdateCoin(CoinCardItem coinCardItem, int coinCardItemPosition);

        void onDeleteCoin(int coinCardItemPosition);
    }

}