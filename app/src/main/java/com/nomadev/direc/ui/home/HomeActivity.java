package com.nomadev.direc.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ActivityHomeBinding;
import com.nomadev.direc.ui.search.SearchActivity;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ibFilter.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(HomeActivity.this, v);
            popup.setOnMenuItemClickListener(this::onOptionsItemSelected);
            popup.inflate(R.menu.filter_menu);
            popup.show();
        });

        binding.ibSearch.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            overridePendingTransition(0,0);
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.date:
                Toast.makeText(this, "date selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.nama:
                Toast.makeText(this, "nama selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.usia:
                Toast.makeText(this, "usia selected", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }
}