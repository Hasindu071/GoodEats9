package com.example.goodeats9;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

//-----------------------------IM/2021/062 - Hasindu -----------------------------------------------//
public class splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Ensure the splash screen layout is set to fullscreen by hiding system bars
        View splashScreenView = findViewById(R.id.main);  // Check if this ID exists in activity_splash.xml
        ViewCompat.setOnApplyWindowInsetsListener(splashScreenView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Delay for 3 seconds before transitioning to MainActivity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Navigate to activity_start
            Intent intent = new Intent(splash.this, start.class);
            startActivity(intent);
            // Finish splash activity to prevent the user from returning to it
            finish();
        }, 2000);  // Delay for 2 seconds

    }
}
//----------------------------------IM/2021/062 - Hasindu -----------------------------------------//