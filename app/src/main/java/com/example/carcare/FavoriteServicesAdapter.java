package com.example.carcare;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
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



    public void deleteItem(int position) {
        favoriteServices.remove(position);
        notifyItemRemoved(position);

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyFavorites", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("favorites", new HashSet<>(favoriteServices));
        editor.apply();
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



            // Implement swipe gesture detection
            itemView.setOnTouchListener(new View.OnTouchListener() {
                private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        final int SWIPE_THRESHOLD = 100;
                        final int SWIPE_VELOCITY_THRESHOLD = 100;

                        try {
                            float diffY = e2.getY() - e1.getY();
                            float diffX = e2.getX() - e1.getX();
                            if (Math.abs(diffX) > Math.abs(diffY) && Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                                if (diffX > 0) {
                                    // Right swipe
                                } else {
                                    // Left swipe
                                    int position = getAdapterPosition();
                                    deleteItem(position);
                                }
                                return true;
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        return false;
                    }
                });

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }
            });
        }
    }




        // Constructor and other methods

        public void updateData(List<String> newData) {
            favoriteServices.clear();
            favoriteServices.addAll(newData);
        }

}
