package com.example.goodeats9;

import android.content.Intent;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.MediaController;

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

    // initialize the UI components in the xml file
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

        // Initialize the components
        recipeNameText = findViewById(R.id.recipeName);
        descriptionText = findViewById(R.id.Recipedescription);
        userNameText = findViewById(R.id.profileName);
        procedureButton = findViewById(R.id.procedureButton);
        ingredientsButton = findViewById(R.id.ingredientsButton);
        ImageView starImage = findViewById(R.id.star);
        ImageView shareImage = findViewById(R.id.share);
        TextView textViewReviews = findViewById(R.id.reviews);
        ProgressBar loadingSpinner = findViewById(R.id.loadingSpinner);

        // Show ProgressBar while the video is loading
        loadingSpinner.setVisibility(View.VISIBLE);


        // Initialize VideoView
        recipeVideoView = findViewById(R.id.videoView2);

        // Handle star ImageView click to go to the rating class
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

        // when click the procedure button go that fragment
        procedureButton.setOnClickListener(view -> {
            setButtonActive(procedureButton);
            setButtonInactive(ingredientsButton);
            loadFragment(new ProcedureFragment());
        });

        // when click the ingredient button go that fragment
        ingredientsButton.setOnClickListener(view -> {
            setButtonActive(ingredientsButton);
            setButtonInactive(procedureButton);
            loadFragment(new IngredientsFragment());
        });

        // Set click listener for shareImage
        shareImage.setOnClickListener(v -> fetchAndShareRecipe());

        // Get the video URL,load and run it
        String videoUri = intentRecipe.getStringExtra("videoUri");
        recipeVideoView.setVideoURI(Uri.parse(videoUri));

        // Add media controller to enable play/pause controls
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(recipeVideoView);  // Attach media controller to VideoView
        recipeVideoView.setMediaController(mediaController);

        // Set a listener to know when the video is ready. until it is ready there will be a progress bar
        recipeVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // Hide ProgressBar once the video is ready
                loadingSpinner.setVisibility(View.GONE);

                // Start the video
                recipeVideoView.start();
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
