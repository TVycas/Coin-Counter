package com.example.coinscounter.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coinscounter.R;
import com.example.coinscounter.model.CoinCardItem;

import java.util.ArrayList;
import java.util.List;

public class CoinCardAdapter extends RecyclerView.Adapter<CoinCardAdapter.CoinCardViewHolder> {
    private static final String TAG = CoinCardAdapter.class.getName();
    private List<CoinCardItem> coinCardList = new ArrayList<>();
    private OnItemClickListener listener;

    public CoinCardAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setCoins(List<CoinCardItem> coinCards) {
        if (coinCards != null) {
            this.coinCardList = coinCards;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public CoinCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coin_card_item, parent, false);
        return new CoinCardViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull CoinCardViewHolder holder, int position) {
        CoinCardItem currentItem = coinCardList.get(position);

        holder.imageView.setImageBitmap(currentItem.getImageBitmap());
        holder.textView.setText(currentItem.getName());
    }

    @Override
    public int getItemCount() {
        return coinCardList == null ? 0 : coinCardList.size();
    }

    public interface OnItemClickListener {

        void onItemClick(int coinCardItemPosition);
    }

    public class CoinCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imageView;
        public TextView textView;
        public OnItemClickListener onCoinListener;

        public CoinCardViewHolder(@NonNull View itemView, OnItemClickListener onCountryListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.coin_image);
            textView = itemView.findViewById(R.id.coinTextView);
            this.onCoinListener = onCountryListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                onCoinListener.onItemClick(position);
            }
        }
    }
}
