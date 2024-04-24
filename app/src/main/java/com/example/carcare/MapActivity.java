package com.example.carcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashSet;
import java.util.Set;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private GoogleMap myMap;
    private final int FINE_PERMISSION_CODE = 1;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private Marker samauto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();





        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_map);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.bottom_map) {
                return true;
            } else if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), NavigationBarActivity.class));
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
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;

                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    mapFragment.getMapAsync(MapActivity.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;



        LatLng Samauto = new LatLng(40.159373712757656, 44.44923769322041);
        myMap.addMarker(new MarkerOptions().position(Samauto).title("SamAuto"));
        myMap.moveCamera(CameraUpdateFactory.newLatLng(Samauto));



        LatLng MyLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        myMap.setMyLocationEnabled(true);
        myMap.moveCamera(CameraUpdateFactory.newLatLng(MyLocation));

        myMap.getUiSettings().setZoomControlsEnabled(true);
        myMap.getUiSettings().setCompassEnabled(true);

        myMap.setOnMarkerClickListener(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == FINE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                getLastLocation();
            }else {
                Toast.makeText(this,"Location permission is denied, please allow the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);
        dialog.show();

        // Find the heart icon and set OnClickListener
        ImageView addToFavorites = dialog.findViewById(R.id.add_to_fav);

        // Check if the service is already in favorites
        final SharedPreferences[] sharedPreferences = {getSharedPreferences("MyFavorites", Context.MODE_PRIVATE)};
        Set<String> favorites = sharedPreferences[0].getStringSet("favorites", new HashSet<String>());
        final boolean[] isFavorite = {favorites.contains("Service Name")};

        // Update the heart icon state accordingly
        if (isFavorite[0]) {
            addToFavorites.setImageResource(R.drawable.baseline_favorite_24); // Filled heart
        } else {
            addToFavorites.setImageResource(R.drawable.baseline_favorite_border_24); // Unfilled heart
        }

        addToFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isFavorite[0]) {
                    // Remove service from favorites
                    favorites.remove("Service Name");
                    // Update SharedPreferences with the new set of favorites
                    sharedPreferences[0].edit().putStringSet("favorites", favorites).apply();
                    // Update heart icon to unfilled
                    addToFavorites.setImageResource(R.drawable.baseline_favorite_border_24);
                    // Update isFavorite flag
                    isFavorite[0] = false;
                    // Show a toast or any feedback to the user
                    Toast.makeText(MapActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                } else {
                    // Add service to favorites
                    favorites.add("Service Name");
                    // Update SharedPreferences with the new set of favorites
                    sharedPreferences[0].edit().putStringSet("favorites", favorites).apply();
                    // Update heart icon to filled
                    addToFavorites.setImageResource(R.drawable.baseline_favorite_24);
                    // Update isFavorite flag
                    isFavorite[0] = true;
                    // Show a toast or any feedback to the user
                    Toast.makeText(MapActivity.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView seeThePriceList =  dialog.findViewById(R.id.pricelist_see);
        seeThePriceList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, PricelistActivity.class);
                startActivity(intent);
            }
        });



        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);


        return true;
    }
}