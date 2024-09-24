package com.example.goodeats9;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class AddNew extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new);


        // Initialize back button and set click listener inside onCreate
        ImageView back = findViewById(R.id.backbtn);
        back.setOnClickListener(v -> {
            // Navigate to AddNew activity when back button is clicked
            Intent intent = new Intent(AddNew.this, homeFragment.class);
            this.finish();
            //startActivity(intent); // Uncomment if you want to start a new activity
        });
    }
}
