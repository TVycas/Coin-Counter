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

    private ArrayList<CoinCardItem> coinCardList = new ArrayList<>();
    private onItemClickListener listener;


    public class CoinCardViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView textView;

        public CoinCardViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.coinImage);
            textView = itemView.findViewById(R.id.coinTextView);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public CoinCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.coin_card_item, parent, false);
        CoinCardViewHolder ccvh = new CoinCardViewHolder(v);
        return ccvh;
    }

    @Override
    public void onBindViewHolder(@NonNull CoinCardViewHolder holder, int position) {
        CoinCardItem currentItem = coinCardList.get(position);

        holder.imageView.setImageBitmap(currentItem.getImageBitmap());
        holder.textView.setText(currentItem.getName());
    }

    @Override
    public int getItemCount() {
        return coinCardList.size();
    }

    public void setCoins(List<CoinCardItem> coinCards){
        this.coinCardList = (ArrayList<CoinCardItem>) coinCards;
        notifyDataSetChanged();
    }

    public interface onItemClickListener{
        void onItemClick(int coinCardItemPosition);
    }

    public void setOnItemClickListener(onItemClickListener listener){
        this.listener = listener;
    }
}
