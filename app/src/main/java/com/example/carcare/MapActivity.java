package com.example.carcare;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private GoogleMap myMap;
    private final int FINE_PERMISSION_CODE = 1;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private Map<String, String> priceListMap = new HashMap<>();
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

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = fAuth.getCurrentUser();

        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("approvedCarServices").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.contains("latitude") && document.contains("longitude")) {
                            double latitude = document.getDouble("latitude");
                            double longitude = document.getDouble("longitude");

                            // Verify latitude and longitude values
                            Log.d("FirestoreData", "Latitude: " + latitude + ", Longitude: " + longitude);

                            LatLng serviceLocation = new LatLng(latitude, longitude);
                            myMap.addMarker(new MarkerOptions().position(serviceLocation).title(document.getId()));
                        } else {
                            Log.e("FirestoreError", "Latitude or longitude field not found in document: " + document.getId());
                        }
                    }
                } else {
                    Log.e("FirestoreError", "Error getting documents: ", task.getException());
                }
            }
        });


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

        TextView serviceNameTxt = dialog.findViewById(R.id.service_name_txt);
        Log.d("Debug", "serviceNameTxt: " + serviceNameTxt);


       String documentId = marker.getTitle();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("approvedCarServices").document(documentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Get service name from the document
                        String priceList = document.getString("priceList");
                        priceListMap.put(document.getId(), priceList);

                        String serviceName = document.getString("serviceName");
                        if (serviceName != null) {
                            serviceNameTxt.setText(serviceName);
                        } else {
                            // If service name is null, use the document ID as fallback
                            serviceNameTxt.setText("No service name found");
                        }

                        // Find the heart icon and set OnClickListener
                        ImageView addToFavorites = dialog.findViewById(R.id.add_to_fav);

                        // Get favorites from SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("MyFavorites", Context.MODE_PRIVATE);
                        Set<String> favorites = sharedPreferences.getStringSet("favorites", new HashSet<String>());

                        // Update the heart icon state accordingly
                        if (favorites.contains(serviceName)) {
                            addToFavorites.setImageResource(R.drawable.baseline_favorite_24); // Filled heart
                        } else {
                            addToFavorites.setImageResource(R.drawable.baseline_favorite_border_24); // Unfilled heart
                        }

                        addToFavorites.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Get favorites from SharedPreferences
                                Set<String> favorites = sharedPreferences.getStringSet("favorites", new HashSet<String>());
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                if (favorites.contains(serviceName)) {
                                    // Remove service from favorites
                                    favorites.remove(serviceName);
                                    // Update SharedPreferences with the new set of favorites
                                    editor.putStringSet("favorites", favorites).apply();
                                    // Update heart icon to unfilled
                                    addToFavorites.setImageResource(R.drawable.baseline_favorite_border_24);
                                    // Show a toast or any feedback to the user
                                    Toast.makeText(MapActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Add service to favorites
                                    favorites.add(serviceName);
                                    // Update SharedPreferences with the new set of favorites
                                    editor.putStringSet("favorites", favorites).apply();
                                    // Update heart icon to filled
                                    addToFavorites.setImageResource(R.drawable.baseline_favorite_24);
                                    // Show a toast or any feedback to the user
                                    Toast.makeText(MapActivity.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
                dialog.show(); // Show the dialog after retrieving service name
            }
        });

        TextView seeThePriceList =  dialog.findViewById(R.id.pricelist_see);
        seeThePriceList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serviceId = marker.getTitle();
                String priceList = priceListMap.get(serviceId);
                Intent intent = new Intent(MapActivity.this, PriceListPageAdmin.class);
                intent.putExtra("priceList", priceList);
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