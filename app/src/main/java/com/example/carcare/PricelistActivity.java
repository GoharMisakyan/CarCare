package com.example.carcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PricelistActivity extends AppCompatActivity {
    private PriceListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_list_page_admin);

        RecyclerView recyclerView = findViewById(R.id.priceListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String priceList = getIntent().getStringExtra("priceList");

        if (priceList != null) {
            List<String> priceItems = new ArrayList<>(Arrays.asList(priceList.split("\\\\n")));
            adapter = new PriceListAdapter(priceItems);
            recyclerView.setAdapter(adapter);
        } else {

            Toast.makeText(this, "Price list is empty", Toast.LENGTH_SHORT).show();
        }


        SearchView searchView = findViewById(R.id.search_badge);
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    adapter.getFilter().filter(newText);
                    return false;
                }
            });
        } else {
            // Handle the case where searchView is null
            Toast.makeText(this, "SearchView not found", Toast.LENGTH_SHORT).show();
        }
    }
}