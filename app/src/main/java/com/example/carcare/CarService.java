package com.example.carcare;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class CarService {
    private String id;
    private String imageUrl;
    private String serviceName;
    private String phone;
    private double longitude;
    private double latitude;
    private String priceList;



    public CarService() {
        // Default constructor
    }

    public CarService(String id, String imageUrl, String serviceName, double latitude, double longitude, String priceList) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.serviceName = serviceName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
        this.priceList = priceList;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getPhone(){return phone;}

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPriceList() {
        return priceList;
    }

    public void setPriceList(String priceList) {
        this.priceList = priceList;
    }


}
