package com.example.fetchdisplay;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv;
    private ItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ItemAdapter(new ArrayList<>());
        rv.setAdapter(adapter);
        Log.d("MainActivity", "Setting adapter");
        fetchData();
        adapter.notifyDataSetChanged();
    }

    private void fetchData() {
        NetworkService.getApi().fetchItems().enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Item> filtered = response.body();
                    filtered.removeIf(item -> item.name == null || item.name.trim().isEmpty());

                    Collections.sort(filtered, Comparator.comparingInt((Item o) -> o.listId)
                            .thenComparing(o -> o.name));
                    Log.d("MainActivity", "Response size: " + response.body().size());
                    adapter.updateItems(filtered);
                    Log.d("MainActivity", "Filtered size: " + filtered.size());
                } else {
                    Toast.makeText(MainActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("MainActivity", "Error: ", t);
            }
        });
    }
}
