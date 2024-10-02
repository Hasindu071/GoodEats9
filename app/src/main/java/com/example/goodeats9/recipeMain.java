package com.example.goodeats9;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class recipeMain extends AppCompatActivity {

    private Button procedureButton;
    private Button ingredientsButton;
    private TextView recipeNameText, descriptionText, userNameText;
    private VideoView recipeVideoView;
    private DatabaseReference recipeDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_main);

        // Initialize Firebase Database reference
        recipeDatabaseReference = FirebaseDatabase.getInstance().getReference("recipes");

        // Initialize views
        recipeNameText = findViewById(R.id.recipeName);
        descriptionText = findViewById(R.id.Recipedescription);
        userNameText = findViewById(R.id.profileName);
        procedureButton = findViewById(R.id.procedureButton);
        ingredientsButton = findViewById(R.id.ingredientsButton);
        ImageView starImage = findViewById(R.id.star);
        ImageView shareImage = findViewById(R.id.share);
        TextView textViewReviews = findViewById(R.id.reviews);

        // Initialize VideoView
        recipeVideoView = findViewById(R.id.videoView2);

        // Handle star ImageView click
        starImage.setOnClickListener(v -> {
            Intent intent = new Intent(recipeMain.this, rating.class);
            startActivity(intent);
        });

        // Get the intent passed by the previous page
        Intent intentRecipe = getIntent();
        String name = intentRecipe.getStringExtra("name");
        String description = intentRecipe.getStringExtra("description");
        String userName = intentRecipe.getStringExtra("username");

        // Set received data to views
        recipeNameText.setText(name);
        descriptionText.setText(description);
        userNameText.setText(userName);

        // Make the "reviews" text look like a link
        textViewReviews.setPaintFlags(textViewReviews.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textViewReviews.setOnClickListener(v -> {
            Intent intent = new Intent(recipeMain.this, Reviews.class);
            startActivity(intent);
        });

        // Initially set the Procedure button as the selected one
        setButtonActive(procedureButton);
        setButtonInactive(ingredientsButton);

        // Load the Procedure fragment by default
        loadFragment(new ProcedureFragment());

        procedureButton.setOnClickListener(view -> {
            setButtonActive(procedureButton);
            setButtonInactive(ingredientsButton);
            loadFragment(new ProcedureFragment());
        });

        ingredientsButton.setOnClickListener(view -> {
            setButtonActive(ingredientsButton);
            setButtonInactive(procedureButton);
            loadFragment(new IngredientsFragment());
        });

        // Set click listener for shareImage
        shareImage.setOnClickListener(v -> fetchAndShareRecipe());

        // Load video from Firebase
        loadRecipeVideo();
    }

    // Fetch and load the video from Firebase Storage
    private void loadRecipeVideo() {
        String userId = "jagath@gmail_com";  // User's ID
        String recipeId = "-O8C8BCQipBiXFRF6MFf"; // Recipe ID

        recipeDatabaseReference.child(userId).child(recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Fetch the video URL from Firebase
                    String videoUrl = dataSnapshot.child("videoUri").getValue(String.class);
                    if (videoUrl != null) {
                        // Load and play the video
                        Uri videoUri = Uri.parse(videoUrl);
                        recipeVideoView.setVideoURI(videoUri);
                        recipeVideoView.start();
                    } else {
                        Toast.makeText(recipeMain.this, "No video available for this recipe.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(recipeMain.this, "Recipe not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(recipeMain.this, "Failed to load video", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Share recipe details via an Intent
    private void fetchAndShareRecipe() {
        String userId = "jagath@gmail_com";  // User's ID
        String recipeId = "-O8C8BCQipBiXFRF6MFf"; // Recipe ID

        recipeDatabaseReference.child(userId).child(recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Fetch recipe details
                    String recipeName = dataSnapshot.child("name").getValue(String.class);
                    String description = dataSnapshot.child("description").getValue(String.class);

                    // Create sharing intent
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this recipe!");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Recipe Name: " + recipeName + "\nDescription: " + description);
                    startActivity(Intent.createChooser(shareIntent, "Share via"));
                } else {
                    Toast.makeText(recipeMain.this, "Recipe not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(recipeMain.this, "Failed to fetch recipe", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper methods to set button states
    private void setButtonActive(Button button) {
        button.setBackgroundTintList(getResources().getColorStateList(R.color.green));
        button.setTextColor(getResources().getColor(R.color.white));
    }

    private void setButtonInactive(Button button) {
        button.setBackgroundTintList(getResources().getColorStateList(R.color.white));
        button.setTextColor(getResources().getColor(R.color.green));
    }

    // Load fragment into container
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }
}
