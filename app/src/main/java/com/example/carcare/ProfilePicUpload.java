package com.example.carcare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProfilePicUpload extends AppCompatActivity {

    private ImageView imageViewUploadPic;
    private FirebaseAuth fAuth;

    private StorageReference fStorage;
    Button buttonPictureChoose, buttonUploadPicture;
    private static  final int PICK_IMAGE_REQUEST = 1;

    private  Uri uriImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_pic_upload);

        fAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = fAuth.getCurrentUser();

        buttonPictureChoose = findViewById(R.id.choose_profile_pic_btn);
        buttonUploadPicture =findViewById(R.id.upload_pic_button);
        imageViewUploadPic = findViewById(R.id.imageView_profile_dp);

        fStorage = FirebaseStorage.getInstance().getReference("DisplayPics");
        Uri uri = currentUser.getPhotoUrl();

        //Set Users current DP in ImageView (if uploaded already).
        //Regular URIs.
        Picasso.get().load(uri).into(imageViewUploadPic);


        buttonPictureChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        buttonUploadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadPic();
            }
        });

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


    }


    private void UploadPic() {
        if (uriImage != null) {
            try {
                // Reading and rotating the image if necessary
                Bitmap bitmap = handleImageRotation(uriImage);

                // Converting bitmap to byte array
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                // Creating a reference to the file to upload
                StorageReference fileReference = fStorage.child(fAuth.getCurrentUser().getUid() + "." + getFileExtension(uriImage));

                // Uploading the byte array
                fileReference.putBytes(data).addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    Uri downloadUri = uri;
                    FirebaseUser currentUser = fAuth.getCurrentUser();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(downloadUri).build();
                    currentUser.updateProfile(profileUpdates);

                    Toast.makeText(ProfilePicUpload.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = fAuth.getCurrentUser();
                    if (user.getUid().equals("1ekcwcOSV8WttQgaFwCyLpH2Iuj2")) {
                        startActivity(new Intent(ProfilePicUpload.this, ProfilePageAdmin.class));
                    } else {
                        startActivity(new Intent(ProfilePicUpload.this, ProfileActivity.class));
                    }
                    finish();
                })).addOnFailureListener(e -> Toast.makeText(ProfilePicUpload.this, e.getMessage(), Toast.LENGTH_SHORT).show());

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(ProfilePicUpload.this, "Failed to process the image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(ProfilePicUpload.this, "No File Selected!", Toast.LENGTH_SHORT).show();
        }
    }

    //to obtain  File Extension of the image
    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return  mime.getExtensionFromMimeType(cR.getType(uri));
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
            uriImage = data.getData();
            imageViewUploadPic.setImageURI(uriImage);
        }

    }


    private Bitmap handleImageRotation(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        inputStream.close();

        InputStream inputStreamForExif = getContentResolver().openInputStream(uri);
        ExifInterface exif = new ExifInterface(inputStreamForExif);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        inputStreamForExif.close();

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateBitmap(bitmap, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateBitmap(bitmap, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateBitmap(bitmap, 270);
            default:
                return bitmap;
        }
    }


    private Bitmap rotateBitmap(Bitmap bitmap, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}