package com.example.carcare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavoriteServicesAdapter extends RecyclerView.Adapter<FavoriteServicesAdapter.ViewHolder> {
    private List<String> favoriteServices;
    private Context context;

    public FavoriteServicesAdapter(Context context, List<String> favoriteServices) {
        this.context = context;
        this.favoriteServices = favoriteServices;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite_service, parent, false);
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_favorite_service, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String service = favoriteServices.get(position);
        holder.textServiceName.setText(service);
    }


    @Override
    public int getItemCount() {
        return favoriteServices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textServiceName;
        ImageView rateStar1, rateStar2, rateStar3;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textServiceName = itemView.findViewById(R.id.service_name);
            rateStar1 = itemView.findViewById(R.id.imageView);
            rateStar2 = itemView.findViewById(R.id.imageView2);
            rateStar3 = itemView.findViewById(R.id.imageView3);
        }
    }




        // Constructor and other methods

        public void updateData(List<String> newData) {
            favoriteServices.clear();
            favoriteServices.addAll(newData);
        }

}
