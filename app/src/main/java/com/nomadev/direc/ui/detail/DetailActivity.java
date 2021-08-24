package com.nomadev.direc.ui.detail;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nomadev.direc.R;

public class DetailActivity extends AppCompatActivity {

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        FloatingActionButton fab_tambah_data = findViewById(R.id.fab_tambah_data);

        fab_tambah_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogTambahData();
            }
        });
    }

    private void openDialogTambahData(){
        dialog.setContentView(R.layout.activity_dialog_add_data);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}