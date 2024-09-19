package com.example.goodeats9;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goodeats9.sign_up;

public class start extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);  // Ensure this is the correct layout file

        // Find the button by ID
        Button startCookingButton = findViewById(R.id.startButton);

        // Set an OnClickListener for the button
        startCookingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to the sign-up activity
                Intent intent = new Intent(start.this, login.class);
                startActivity(intent);  // Start the new activity
            }
        });

    }
}
