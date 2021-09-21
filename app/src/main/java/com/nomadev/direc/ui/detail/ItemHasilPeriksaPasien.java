package com.nomadev.direc.ui.detail;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nomadev.direc.R;

public class ItemHasilPeriksaPasien extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayoutManager HorizontalLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_hasil_periksa_pasien);

        recyclerView = (RecyclerView) findViewById(R.id.rv_photo);
        HorizontalLayout = new LinearLayoutManager(ItemHasilPeriksaPasien.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(HorizontalLayout);
    }
}
