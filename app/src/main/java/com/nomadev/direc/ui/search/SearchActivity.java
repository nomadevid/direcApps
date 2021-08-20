package com.nomadev.direc.ui.search;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.nomadev.direc.R;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0,0);
    }
}