package com.example.carcare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PriceListAdapter extends RecyclerView.Adapter<PriceListAdapter.ViewHolder> {

    private List<String> priceItems;

    public PriceListAdapter(List<String> priceItems) {
        this.priceItems = priceItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_price_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String priceItem = priceItems.get(position);
        holder.bind(priceItem);
    }

    @Override
    public int getItemCount() {

        return priceItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView priceItemTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            priceItemTextView = itemView.findViewById(R.id.item_txt);
        }

        public void bind(String priceItem) {
            priceItemTextView.setText(priceItem);
        }
    }
}


