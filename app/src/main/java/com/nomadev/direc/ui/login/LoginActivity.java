package com.nomadev.direc.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nomadev.direc.R;
import com.nomadev.direc.ui.home.HomeActivity;

public class LoginActivity extends AppCompatActivity {
    TextInputLayout editTextUsername, editTextPassword;
    Button buttonMasuk;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_DirecApps);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.editTextTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonMasuk = findViewById(R.id.buttonMasuk);
        progressBar = findViewById(R.id.progressBar);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

        buttonMasuk.setOnClickListener(view -> {
            String username = "";
            String password = "";

            if (editTextUsername.getEditText() != null) {
                username = editTextUsername.getEditText().getText().toString().trim();
            }

            if (editTextPassword.getEditText() != null) {
                password = editTextPassword.getEditText().getText().toString().trim();
            }

            if (TextUtils.isEmpty(username)) {
                editTextUsername.setError("Masukkan Username anda");
                return;
            } else {
                editTextUsername.setError(null);
            }

            if (TextUtils.isEmpty(password)) {
                editTextPassword.setError("Masukkan Kata sandi anda");
                return;
            } else {
                editTextPassword.setError(null);
            }

            if (password.length() < 6) {
                editTextPassword.setError("Kata sandi harus lebih dari 5 karakter");
                return;
            } else {
                editTextPassword.setError(null);
            }

            String email = username + "@gmail.com";

            progressBar.setVisibility(View.VISIBLE);

            //autentikasi firebase
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Berhasil Masuk", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            if (task.getException() != null) {
                                Toast.makeText(LoginActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        });

    }
}