package com.example.goodeats9;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goodeats9.sign_up;

//------------------------------IM/2021/028 - Manditha -------------------------------------------------//

public class start extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);  // Show the start layout first

        // Check if the user is logged in
        SharedPreferences sharedPreferences = getSharedPreferences("loginDetails", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        Button startCookingButton = findViewById(R.id.startButton);

        // If logged in, skip login and go to the homepage
        if (isLoggedIn) {
            Intent intent = new Intent(start.this, menu_bar_main.class);
            startActivity(intent);  // Start the homepage activity
            finish(); // Close the start activity
            return; // Stop further execution
        }

        // If not logged in, set the click listener for the start button
        startCookingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(start.this, login.class);
                startActivity(intent);  // Start the login activity
            }
        });
    }
}
//----------------------------------IM/2021/028 - Manditha ---------------------------------//