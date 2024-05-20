package com.example.carcare;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class FavoriteServicesAdapter extends RecyclerView.Adapter<FavoriteServicesAdapter.ViewHolder> {
    private List<String> favoriteServices;
    private Context context;
    private String userId;

    public FavoriteServicesAdapter(Context context, List<String> favoriteServices, String userId) {
        this.context = context;
        this.favoriteServices = favoriteServices;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String service = favoriteServices.get(position);
        holder.textServiceName.setText(service);
        holder.ratingBar.setRating(5);
    }

    @Override
    public int getItemCount() {
        return favoriteServices.size();
    }

    public void deleteItem(int position) {
        // Get the service name to be deleted
        String serviceName = favoriteServices.get(position);

        // Search for the service document in approvedCarServices to get its ID
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("approvedCarServices")
                .whereEqualTo("serviceName", serviceName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Get the ID of the service document
                        String serviceId = documentSnapshot.getId();

                        // Delete the corresponding document from favoriteServiceList
                        deleteFromFavoriteServiceList(serviceId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error searching for service document: ", e);
                });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textServiceName;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textServiceName = itemView.findViewById(R.id.service_name);
            ratingBar = itemView.findViewById(R.id.rate_bar);

            // Swipe gesture detection
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

                                } else {
                                   //deleting an item by left swipe
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

    public void updateData(List<String> newData) {

        favoriteServices.clear();
        favoriteServices.addAll(newData);
        notifyDataSetChanged();
    }

    private void deleteFromFavoriteServiceList(String serviceId) {
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("Users").document(userId)
                .collection("favoriteServiceList").document(serviceId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully deleted");
                    // Show a Toast message indicating successful deletion
                    Toast.makeText(context, "Service deleted from favorites", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error deleting document", e);
                    // Show a Toast message indicating deletion failure
                    Toast.makeText(context, "Failed to delete the service", Toast.LENGTH_SHORT).show();
                });
    }

}
