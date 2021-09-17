package com.nomadev.direc.ui.home.laporerror;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.nomadev.direc.databinding.ActivityLaporErrorBinding;

public class LaporErrorActivity extends AppCompatActivity {

    private ActivityLaporErrorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLaporErrorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ibBack.setOnClickListener(v -> onBackPressed());

        binding.btnKirim.setOnClickListener(v -> sendMail());
    }

    private void sendMail() {
        String mail = "nomadev.id@gmail.com";
        String subject = binding.mSubject.getText().toString().trim();
        String message = binding.mMessage.getText().toString();

        MailAPI mailAPI = new MailAPI(this, mail, subject, message);
        mailAPI.execute();
    }
}