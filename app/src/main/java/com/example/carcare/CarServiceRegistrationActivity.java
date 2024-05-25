package com.example.carcare;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class CarServiceRegistrationActivity extends AppCompatActivity {

    private EditText editTextServiceName, editTextLatitude, editTextLongitude, editTextPriceList, editTextPhone ;
    private ImageView imageViewUploadPhoto;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private FirebaseStorage fStorage;
    private StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_service_registration);

        editTextServiceName = findViewById(R.id.editTextServiceName);
        editTextLatitude = findViewById(R.id.editTextLatitude);
        editTextLongitude = findViewById(R.id.editTextLongitude);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPriceList = findViewById(R.id.editTextPriceList);
        imageViewUploadPhoto = findViewById(R.id.imageViewUploadPhoto);
        Button buttonUploadPhoto = findViewById(R.id.buttonUploadPhoto);
        Button buttonSubmit = findViewById(R.id.buttonSubmit);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        fStorage = FirebaseStorage.getInstance();
        storageRef = fStorage.getReference();


        buttonUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRegistration();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            imageViewUploadPhoto.setImageURI(imageUri);
        }

    }

    private void submitRegistration() {

        String serviceName = editTextServiceName.getText().toString();
        String latitude = editTextLatitude.getText().toString();
        String longitude = editTextLongitude.getText().toString();
        String priceList = editTextPriceList.getText().toString();
        String phone = editTextPhone.getText().toString();

        if (serviceName.trim().isEmpty() || latitude.trim().isEmpty() || longitude.trim().isEmpty() || phone.trim().isEmpty() || priceList.trim().isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill in all fields and upload an image", Toast.LENGTH_SHORT).show();
            return;
        }

        //storeRegistrationData(serviceName, latitude, longitude, priceList);
        FirebaseUser user = fAuth.getCurrentUser();
        if (user != null) {
            storeRegistrationData(user, serviceName, latitude, longitude, phone, priceList);
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void storeRegistrationData(FirebaseUser user, String serviceName, String latitude, String longitude, String phone, String priceList) {

       // FirebaseUser user = fAuth.getCurrentUser();


        Map<String, Object> registrationData = new HashMap<>();
        registrationData.put("id", user.getUid());
        registrationData.put("serviceName", serviceName);
        // Convert latitude string to double before storing
        try {
            double parsedLatitude = Double.parseDouble(latitude);
            registrationData.put("latitude", parsedLatitude);
        } catch (NumberFormatException e) {
            Log.w("CarServiceApproval", "Error parsing latitude: " + e.getMessage());
            return;
        }

        // Convert longitude string to double before storing
        try {
            double parsedLongitude = Double.parseDouble(longitude);
            registrationData.put("longitude", parsedLongitude);
        } catch (NumberFormatException e) {
            Log.w("CarServiceApproval", "Error parsing longitude: " + e.getMessage());
            return;
        }

        registrationData.put("phone", phone);

        registrationData.put("priceList", priceList);




        StorageReference imageRef = storageRef.child("images/" + user.getUid() + "_service_image");
        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Image upload successful, get download URL
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Add image URL to registration data
                    registrationData.put("imageUrl", uri.toString());

                    // Add registration data to Firestore
                    fStore.collection("registrationRequests")
                            .document(user.getUid())
                            .set(registrationData)
                            .addOnSuccessListener(aVoid -> {

                                notifyOwnerAboutRegistrationRequest(user.getUid());

                                Toast.makeText(this, "Registration submitted for approval", Toast.LENGTH_SHORT).show();

                                // Clear input fields and image view
                                editTextServiceName.setText("");
                                editTextLatitude.setText("");
                                editTextLongitude.setText("");
                                editTextPhone.setText("");
                                editTextPriceList.setText("");
                                imageViewUploadPhoto.setImageResource(R.drawable.baseline_browse_gallery_24);
                                imageUri = null;

                                fAuth.signOut();
                                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                            })
                            .addOnFailureListener(e -> {

                                Toast.makeText(this, "Failed to submit registration. Please try again.", Toast.LENGTH_SHORT).show();
                            });
                });
            } else {

                Toast.makeText(this, "Failed to upload image. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void notifyOwnerAboutRegistrationRequest(String userId) {
        String ownerId = "1ekcwcOSV8WttQgaFwCyLpH2Iuj2";

        fStore.collection("approvalRequests")
                .document(ownerId)
                .collection("pending")
                .document(userId)
                .set(new HashMap<>()) // Empty document, or you can add additional data
                .addOnSuccessListener(aVoid -> {
                    // Notification sent successfully
                })
                .addOnFailureListener(e -> {
                    // Error occurred while sending notification
                });
    }
}