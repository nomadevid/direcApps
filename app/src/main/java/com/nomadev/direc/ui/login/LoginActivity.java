package com.nomadev.direc.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.nomadev.direc.R;
import com.nomadev.direc.ui.home.HomeActivity;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText editTextUsername, editTextPassword;
    Button buttonMasuk;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_DirecApps);
        setContentView(R.layout.activity_login);

        editTextUsername=findViewById(R.id.editTextTextUsername);
        editTextPassword=findViewById(R.id.editTextPassword);
        buttonMasuk=findViewById(R.id.buttonMasuk);
        progressBar=findViewById(R.id.progressBar);
        firebaseAuth=FirebaseAuth.getInstance();

        buttonMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username;
                username = "admin@gmail.com";
                String password= Objects.requireNonNull(editTextPassword.getText()).toString().trim();

                if(TextUtils.isEmpty(username)){
                    editTextUsername.setError("Masukkan Username anda");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    editTextPassword.setError("Masukkan Kata sandi anda");
                    return;
                }
                if(password.length()<6){
                    editTextPassword.setError("Kata sandi harus lebih dari 5 karakter");
                    return;
                }
                progressBar.setVisibility(view.VISIBLE);

                //autentikasi firebase
                firebaseAuth.signInWithEmailAndPassword(username,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this,"Berhasil Masuk",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        }
                        else {
                            Toast.makeText(LoginActivity.this,"Error ! "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(view.GONE);
                        }
                    }
                });
            }
        });

    }
}