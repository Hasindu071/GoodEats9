package com.example.goodeats9;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//-----------------------------------------IM/2021/028 - Manditha ---------------------------------------------------//
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
            submitRatingToFirebase(rating);  // Call this method to store the rating
        });
    }

    // Store rating to Firebase
    private void submitRatingToFirebase(float rating) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            String recipeId = getIntent().getStringExtra("recipeId");  // Pass the recipeId from the intent
            String userEmail = getIntent().getStringExtra("userEmail");  // Get the email associated with the recipe
            DatabaseReference ratingRef = FirebaseDatabase.getInstance().getReference("recipes").child(userEmail).child(recipeId).child("ratings").child(userId);
            ratingRef.setValue(rating)  // Store the rating under the recipe and user ID
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(rating.this, "Rating submitted: " + rating, Toast.LENGTH_SHORT).show();
                        finish();  // Close the activity and return to recipeMain
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(rating.this, "Failed to submit rating: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(rating.this, "Please log in to submit a rating.", Toast.LENGTH_SHORT).show();
        }
    }
}
//-----------------------------------------IM/2021/028 - Manditha ---------------------------------------------------//