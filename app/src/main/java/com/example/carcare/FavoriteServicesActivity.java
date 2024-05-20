package com.example.carcare;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

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

        // Initialize RecyclerView and layout manager
        recyclerView = findViewById(R.id.recycler_view_favorite_services);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter
        adapter = new FavoriteServicesAdapter(this, favoriteServices, userId);
        recyclerView.setAdapter(adapter);

        // Retrieve list of favorite services and update RecyclerView
        updateFavoriteServices();

        ItemTouchHelper.Callback callback = new SwipeToDeleteCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateFavoriteServices();
    }

    private void updateFavoriteServices() {
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("Users").document(userId).collection("favoriteServiceList").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                favoriteServices.clear();
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    favoriteServices.add(document.getId());
                }
                adapter.updateData(favoriteServices); // Update adapter with new data
            }
        });
    }
}
