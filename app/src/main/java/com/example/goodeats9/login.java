package com.example.goodeats9;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);  // Make sure to use the correct layout file

        //Get the text of sign up by its ID
        TextView textViewSignUp = findViewById(R.id.textViewSignUp);
        //Make the "sign up" text look like a link
        textViewSignUp.setPaintFlags(textViewSignUp.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        // Handle Sign Up TextView click
        textViewSignUp.setOnClickListener(v -> {
            // Navigate to SignUpActivity when the Sign Up TextView is clicked
            Intent intent = new Intent(login.this, sign_up.class);
            startActivity(intent);
        });

        // Handle Sign Up TextView click
        findViewById(R.id.buttonSignIN).setOnClickListener(v -> {
            // Navigate to SignUpActivity when the Sign Up TextView is clicked
            Intent intent = new Intent(login.this, menu_bar_main.class);
            startActivity(intent);
        });
    }
}
