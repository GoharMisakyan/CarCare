package com.example.carcare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class PriceListPageAdmin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_list_page_admin);

        String priceList = getIntent().getStringExtra("priceList");

        // Display the price list
        TextView priceListTextView = findViewById(R.id.priceItem1);
        priceListTextView.setText(priceList);
    }
}