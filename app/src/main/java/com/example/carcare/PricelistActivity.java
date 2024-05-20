package com.example.carcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class PricelistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_list_page_admin);

        RecyclerView recyclerView = findViewById(R.id.priceListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String priceList = getIntent().getStringExtra("priceList");

        if (priceList != null) {
            List<String> priceItems = Arrays.asList(priceList.split("\\\\n"));
            PriceListAdapter adapter = new PriceListAdapter(priceItems);
            recyclerView.setAdapter(adapter);
        } else {
            // Handle the case where priceList is null, maybe display a message or take some other action
            Toast.makeText(this, "Price list is empty", Toast.LENGTH_SHORT).show();
        }
    }
}