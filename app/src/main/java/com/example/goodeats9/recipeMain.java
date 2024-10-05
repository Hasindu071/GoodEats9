package com.example.goodeats9;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class recipeMain extends AppCompatActivity {

    // initialize the UI components in the xml file
    private Button procedureButton;
    private Button ingredientsButton;
    private ProgressBar loadingSpinner;
    private TextView recipeNameText, descriptionText, userNameText;
    private VideoView recipeVideoView;
    private DatabaseReference recipeDatabaseReference;
    private ImageView save;
    private Uri currentVideoUri;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_main);

        // Find the ImageView for the back button
        ImageView backButton = findViewById(R.id.backbtn);

        // Set click listener on the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the activity and return to the previous screen
                finish();
            }
        });

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
        loadingSpinner = findViewById(R.id.loadingSpinner);
        save = findViewById(R.id.save);
        recipeVideoView = findViewById(R.id.videoView2);

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
        String email = intentRecipe.getStringExtra("currentUserEmail");
        String ID = intentRecipe.getStringExtra("recipeID");



        // Get the video URL from the intent
        String videoUri = intentRecipe.getStringExtra("videoUri");
        currentVideoUri = Uri.parse(videoUri); // Store it in the member variable
        recipeVideoView.setVideoURI(currentVideoUri); // Set it to the VideoView
        currentVideoUri = Uri.parse(videoUri); // Store it in the member variable
        recipeVideoView.setVideoURI(currentVideoUri); // Set it to the VideoView

        // Set received data to views
        recipeNameText.setText(name);
        descriptionText.setText(description);
        userNameText.setText(userName);

        // Set video URI
        currentVideoUri = Uri.parse(videoUri);
        recipeVideoView.setVideoURI(currentVideoUri);


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
        Bundle args = new Bundle();
        args.putString("email", email); // Pass email
        args.putString("recipeID", ID); // Pass recipeID
        ProcedureFragment procedureFragment = new ProcedureFragment();
        procedureFragment.setArguments(args);
        loadFragment(procedureFragment);


        // when click the procedure button go that fragment
        procedureButton.setOnClickListener(view -> {
            setButtonActive(procedureButton);
            setButtonInactive(ingredientsButton);

            // Create a new instance of ProcedureFragment with the arguments
            procedureFragment.setArguments(args);
            loadFragment(procedureFragment);
        });

// when click the ingredient button go that fragment
        ingredientsButton.setOnClickListener(view -> {
            setButtonActive(ingredientsButton);
            setButtonInactive(procedureButton);

            // Create a new instance of IngredientsFragment (if needed)
            IngredientsFragment ingredientsFragment = new IngredientsFragment();

            // Pass the necessary arguments (if any)
            ingredientsFragment.setArguments(args);
            loadFragment(ingredientsFragment);
        });

        // Set click listener for shareImage
        shareImage.setOnClickListener(v -> fetchAndShareRecipe());

        // Get the video URL,load and run it
        recipeVideoView.setVideoURI(Uri.parse(videoUri));

        // Back button functionality
        backButton.setOnClickListener(v -> finish());

        // Add media controller to enable play/pause controls
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(recipeVideoView);  // Attach media controller to VideoView
        recipeVideoView.setMediaController(mediaController);

        // Show ProgressBar while the video is loading
        loadingSpinner.setVisibility(View.VISIBLE);


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


        // Handle save button click
        save.setOnClickListener(view -> {
            Datacls recipeToSave = new Datacls(videoUri, name, description, userName, ""); // Create recipe object without imageUri
            saveRecipe(recipeToSave); // Save to Firebase
        });

        // Handle star ImageView click to go to the rating class
        starImage.setOnClickListener(v -> {
            Intent intent = new Intent(recipeMain.this, rating.class);
            startActivity(intent);
        });
    }

    // Save the recipe to Firebase
    private void saveRecipe(Datacls recipe) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please log in to save recipes.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        DatabaseReference savedRecipesRef = FirebaseDatabase.getInstance().getReference("saved_recipes").child(userId);

        // Push the new recipe under the user's saved recipes
        savedRecipesRef.push().setValue(recipe)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Recipe saved successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save recipe: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
