package com.example.carcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


public class ProfileActivity extends AppCompatActivity {

    Button logoutBtn;

    TextView profileName, profileEmail, profilePhoneNumber, titleName;

    ImageView profilePicImg;



    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        profilePhoneNumber = findViewById(R.id.profilePhoneNumber);
        titleName = findViewById(R.id.titleName);
        profilePicImg = findViewById(R.id.profileImg);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();


        logoutBtn = findViewById(R.id.logoutButton);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);


        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.bottom_profile) {
                return true;
            } else if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), NavigationBarActivity.class));
                overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom);
                finish();
                return true;
            } else if (itemId == R.id.bottom_map) {
                startActivity(new Intent(getApplicationContext(), MapActivity.class));
                overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom);
                finish();
                return true;
            }

            throw new IllegalStateException("Unexpected value: " + itemId);
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                finish();
            }
        });

        profilePicImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProfilePicUpload.class);
                startActivity(intent);
            }
        });

        getUserDataFromFirestore();

    }

    private void getUserDataFromFirestore() {
        FirebaseUser currentUser = fAuth.getCurrentUser();

            fStore.collection("Users").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            // Retrieve user data
                            String fullName = document.getString("FullName");
                            String phoneNumber = document.getString("PhoneNumber");
                            String userEmail = document.getString("UserEmail");

                            // Display user data in TextViews
                            profileName.setText(fullName);
                            profilePhoneNumber.setText(phoneNumber);
                            profileEmail.setText(userEmail);
                            titleName.setText(fullName);

                            // Load profile picture
                            Uri photoUri = currentUser.getPhotoUrl();
                            if (photoUri != null) {
                                Picasso.get().load(photoUri).into(profilePicImg, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        // Image loaded successfully
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        // Log error if image loading fails
                                        Log.e("Picasso", "Error loading profile picture: " + e.getMessage());
                                    }
                                });
                            } else {
                                // No profile picture available
                                Log.d("Picasso", "No profile picture available for user");
                            }
                        } else {
                            Toast.makeText(ProfileActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

}