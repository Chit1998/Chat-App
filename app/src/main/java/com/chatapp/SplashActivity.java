package com.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.chatapp.activities.CreateProfileActivity;
import com.chatapp.activities.MainActivity;
import com.chatapp.activities.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private static final int REQUEST_TIME = 5000;

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_splash);

        user = FirebaseAuth.getInstance().getCurrentUser();

        new Handler()
                .postDelayed(() -> {
                    if (user != null){
                        startActivity(new Intent(getApplicationContext(), CreateProfileActivity.class));
                        finish();
                    }else {
                        startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                        finish();
                    }
                },REQUEST_TIME);
    }
}