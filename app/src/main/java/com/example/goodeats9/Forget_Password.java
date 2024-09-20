package com.example.goodeats9;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Forget_Password extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password);

        // Back button to go to Login page
        ImageButton backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Login Activity
                Intent intent = new Intent(Forget_Password.this, login.class);
                startActivity(intent);
                finish();  // Optional: To close the Forget_Password activity
            }



        });


                // Reset button to go to forget password 2 page
                Button ResetButton = findViewById(R.id.buttonRsetpassword);
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(com.example.goodeats9.Forget_Password.this, forget_password_2.class);
                        startActivity(intent);
                        finish();
                    }
                    });
                }
    }// Optional: To close the Forget_Password activity




