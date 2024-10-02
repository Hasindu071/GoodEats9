package com.example.goodeats9;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class rating extends AppCompatActivity {

    private RatingBar ratingBar;
    private Button submitRatingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        // Initialize views
        ratingBar = findViewById(R.id.ratingBar);
        submitRatingButton = findViewById(R.id.submitRatingButton);

        // Handle the Submit Rating button click
        submitRatingButton.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            Toast.makeText(rating.this, "Rating submitted: " + rating, Toast.LENGTH_SHORT).show();
            // After submitting the rating, you can either save it or navigate back
            finish();  // Close the activity and go back to the recipe screen
        });
    }
}
