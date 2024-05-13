package com.example.carcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoriteServicesAdmin extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavoriteServicesAdapter adapter;
    private List<String> favoriteServices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_services_admin);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.bottom_home) {
                return true;
            } else if (itemId == R.id.bottom_map) {
                startActivity(new Intent(getApplicationContext(),MapPageAdmin.class));
                overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom);
                finish();
                return true;
            } else if (itemId == R.id.bottom_profile) {
                startActivity(new Intent(getApplicationContext(), ProfilePageAdmin.class));
                overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom);
                finish();
                return true;
            }

            throw new IllegalStateException("Unexpected value: " + itemId);
        });

        // Initialize RecyclerView and layout manager
        recyclerView = findViewById(R.id.recycler_view_favorite_services);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve list of favorite services and update RecyclerView
        updateFavoriteServices();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh favorite services when the activity is resumed
        updateFavoriteServices();
    }

    private void updateFavoriteServices() {
        // Retrieve list of favorite services from SharedPreferences or data source
        SharedPreferences sharedPreferences = getSharedPreferences("MyFavorites", Context.MODE_PRIVATE);
        Set<String> favoritesSet = sharedPreferences.getStringSet("favorites", new HashSet<>());
        favoriteServices = new ArrayList<>(favoritesSet);

        // Initialize or update adapter with the new list of favorite services
        if (adapter == null) {
            adapter = new FavoriteServicesAdapter(this, favoriteServices);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(favoriteServices);
            adapter.notifyDataSetChanged();
        }
    }
}