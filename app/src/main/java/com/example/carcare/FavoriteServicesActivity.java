package com.example.carcare;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoriteServicesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FavoriteServicesAdapter adapter;
    private List<String> favoriteServices = new ArrayList<>();
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_services);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.bottom_home) {
                return true;
            } else if (itemId == R.id.bottom_map) {
                startActivity(new Intent(getApplicationContext(), MapActivity.class));
                overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom);
                finish();
                return true;
            } else if (itemId == R.id.bottom_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom);
                finish();
                return true;
            }

            throw new IllegalStateException("Unexpected value: " + itemId);
        });

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = fAuth.getCurrentUser();
        userId = currentUser != null ? currentUser.getUid() : "";

        fetchFavoriteServices();

        Log.d("FavoriteServicesAdmin", "Adapter set with initial data: " + favoriteServices.size());


    }


    @Override
    protected void onResume() {
        super.onResume();

        fetchFavoriteServices();
    }

    private void fetchFavoriteServices() {
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        fStore.collection("Users").document(userId).collection("favoriteServiceList").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Log.d("FavoriteServicesAdmin", "Retrieved favorite services: " + task.getResult().getDocuments().size());

                favoriteServices.clear();
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                for (DocumentSnapshot document : documents) {
                    // Getting the ID of the favorite service
                    String serviceId = document.getId();

                    // Retrieving the corresponding service document from Firestore
                    fStore.collection("approvedCarServices").document(serviceId).get().addOnSuccessListener(serviceDocument -> {
                        if (serviceDocument.exists()) {
                            // Getting the service name from the service document
                            String serviceName = serviceDocument.getString("serviceName");
                            if (serviceName != null && !favoriteServices.contains(serviceName)) {
                                // Adding the service name to the list of favorite services if it's not already present
                                favoriteServices.add(serviceName);
                                Log.d("FavoriteServicesAdmin", "Service added: " + serviceName);
                            }
                        } else {
                            Log.d("FavoriteServicesAdmin", "Service document does not exist: " + serviceId);
                        }

                        // Checking if all documents have been processed
                        if (favoriteServices.size() == documents.size()) {
                            Log.d("FavoriteServicesAdmin", "All services added. Updating adapter.");
                            initRecyclerView();

                        }
                    }).addOnFailureListener(e -> Log.e("FavoriteServicesAdmin", "Error getting service document: ", e));
                }

                Log.d("FavoriteServicesAdmin", "Favorite services list size: " + favoriteServices.size());
                for (String service : favoriteServices) {
                    Log.d("FavoriteServicesAdmin", "Service: " + service);
                }
            } else {
                Log.e("FavoriteServicesAdmin", "Error getting favorite services: ", task.getException());
            }
        });
    }


    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view_favorite_services);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FavoriteServicesAdapter(this, favoriteServices, userId);
        recyclerView.setAdapter(adapter);


        ItemTouchHelper.Callback callback = new SwipeToDeleteCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}
