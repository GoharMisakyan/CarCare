package com.example.carcare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PriceListAdapter extends RecyclerView.Adapter<PriceListAdapter.ViewHolder> implements Filterable {

    private List<String> priceItems;
    private List<String> priceItemsFull;


    public PriceListAdapter(List<String> priceItems) {
        this.priceItems = new ArrayList<>(priceItems);
        this.priceItemsFull = new ArrayList<>(priceItems);
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

    @Override
    public Filter getFilter() {
        return priceFilter;
    }
    private Filter priceFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<String> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(priceItemsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (String item : priceItemsFull) {
                    if (item.toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }


        @Override
         protected void publishResults(CharSequence constraint, FilterResults results) {
        priceItems.clear();
        priceItems.addAll((List) results.values);
        notifyDataSetChanged();

        }
    };



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


