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

        // Apply edge-to-edge window insets
        View mainView = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        // Handle Sign Up TextView click
        findViewById(R.id.textViewSignUp).setOnClickListener(v -> {
            // Navigate to SignUpActivity when the Sign Up TextView is clicked
            Intent intent = new Intent(login.this, sign_up.class);
            startActivity(intent);
        });
    }
}
