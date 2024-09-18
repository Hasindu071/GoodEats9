package com.example.goodeats9;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);  // Make sure to use the correct layout file


        // Handle Sign Up TextView click
        findViewById(R.id.textViewSignUp).setOnClickListener(v -> {
            // Navigate to SignUpActivity when the Sign Up TextView is clicked
            Intent intent = new Intent(login.this, sign_up.class);
            startActivity(intent);
        });

        // Handle Sign Up TextView click
        findViewById(R.id.buttonSignIN).setOnClickListener(v -> {
            // Navigate to SignUpActivity when the Sign Up TextView is clicked
            Intent intent = new Intent(login.this, homeFragment.class);
            startActivity(intent);
        });
    }
}
