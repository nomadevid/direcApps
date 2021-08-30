package com.nomadev.direc.ui;

import android.app.Application;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nomadev.direc.ui.home.HomeActivity;

public class CheckCurrentUser extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null){
            startActivity(new Intent(CheckCurrentUser.this, HomeActivity.class));
        }
    }
}
