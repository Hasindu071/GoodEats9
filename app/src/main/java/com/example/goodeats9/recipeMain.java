package com.example.goodeats9;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.MediaController;

import androidx.annotation.NonNull;
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

import java.util.ArrayList;

public class recipeMain extends AppCompatActivity {

    private Button procedureButton;
    private Button ingredientsButton;
    private ProgressBar loadingSpinner;
    private TextView recipeNameText, descriptionText, userNameText, totalRatingTextView,CooktimeText,ServesText;
    private VideoView recipeVideoView;
    private DatabaseReference recipeDatabaseReference;
    private ImageView save;
    private Uri currentVideoUri;
    private String recipeId;
    private String userEmail;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_main);

        // Find the ImageView for the back button
        ImageView backButton = findViewById(R.id.backbtn);
        backButton.setOnClickListener(v -> finish());

        // Initialize Firebase Database reference
        recipeDatabaseReference = FirebaseDatabase.getInstance().getReference("recipes");

        // Initialize the components
        recipeNameText = findViewById(R.id.recipeName);
        descriptionText = findViewById(R.id.Recipedescription);
        userNameText = findViewById(R.id.profileName);
        CooktimeText = findViewById(R.id.cookTimeText);
        ServesText = findViewById(R.id.servesText);
        procedureButton = findViewById(R.id.procedureButton);
        ingredientsButton = findViewById(R.id.ingredientsButton);
        ImageView starImage = findViewById(R.id.star);
        ImageView shareImage = findViewById(R.id.share);
        TextView textViewReviews = findViewById(R.id.reviews);
        loadingSpinner = findViewById(R.id.loadingSpinner);
        save = findViewById(R.id.save);
        recipeVideoView = findViewById(R.id.videoView2);
        totalRatingTextView = findViewById(R.id.totalRating); // Initialize totalRatingTextView

        // Show ProgressBar while the video is loading
        loadingSpinner.setVisibility(View.VISIBLE);

        // Get the intent passed by the previous page
        Intent intentRecipe = getIntent();
        String name = intentRecipe.getStringExtra("name");
        String description = intentRecipe.getStringExtra("description");
        String userName = intentRecipe.getStringExtra("username");
        String email = intentRecipe.getStringExtra("currentUserEmail");
        String videoUri = intentRecipe.getStringExtra("videoUri");
        String cookTime = intentRecipe.getStringExtra("cookTime");
        String serves = intentRecipe.getStringExtra("serves");
        recipeId = intentRecipe.getStringExtra("recipeID");
        userEmail = intentRecipe.getStringExtra("currentUserEmail");

        if (recipeId == null || userEmail == null) {
            Toast.makeText(this, "Error: Missing recipeId or userEmail", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set video URI and initialize VideoView
        currentVideoUri = Uri.parse(videoUri);
        recipeVideoView.setVideoURI(currentVideoUri);

        // Set up media controller
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(recipeVideoView);
        recipeVideoView.setMediaController(mediaController);

        // Set a listener to know when the video is ready
        recipeVideoView.setOnPreparedListener(mp -> {
            loadingSpinner.setVisibility(View.GONE);
            recipeVideoView.start();
        });

        // Handle star ImageView click to go to the rating class
        starImage.setOnClickListener(v -> {
            Intent intent = new Intent(recipeMain.this, rating.class);
            intent.putExtra("recipeId", recipeId);
            intent.putExtra("userEmail", userEmail);
            startActivity(intent);
        });

        // Set received data to views
        recipeNameText.setText(name);
        descriptionText.setText(description);
        userNameText.setText(userName);
        CooktimeText.setText(cookTime);
        ServesText.setText(serves);

        // Make the "reviews" text look like a link
        textViewReviews.setPaintFlags(textViewReviews.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textViewReviews.setOnClickListener(v -> {
            Intent intent = new Intent(recipeMain.this, Reviews.class);
            intent.putExtra("recipeId", recipeId);
            intent.putExtra("userEmail", userEmail);
            startActivity(intent);
        });

        // Set initial fragment (Procedure)
        setButtonActive(procedureButton);
        setButtonInactive(ingredientsButton);
        Bundle args = new Bundle();
        args.putString("email", email);
        args.putString("recipeID", recipeId);
        ProcedureFragment procedureFragment = new ProcedureFragment();
        procedureFragment.setArguments(args);
        loadFragment(procedureFragment);

        // When clicking procedure button
        procedureButton.setOnClickListener(view -> {
            setButtonActive(procedureButton);
            setButtonInactive(ingredientsButton);
            ProcedureFragment newProcedureFragment = new ProcedureFragment();
            newProcedureFragment.setArguments(args);
            loadFragment(newProcedureFragment);
        });

        // When clicking ingredients button
        ingredientsButton.setOnClickListener(view -> {
            setButtonActive(ingredientsButton);
            setButtonInactive(procedureButton);
            IngredientsFragment ingredientsFragment = new IngredientsFragment();
            ingredientsFragment.setArguments(args);
            loadFragment(ingredientsFragment);
        });

        // Set click listener for shareImage
        shareImage.setOnClickListener(v -> fetchAndShareRecipe(recipeId));

        // Handle save button click
        save.setOnClickListener(view -> {
            Datacls recipeToSave = new Datacls(videoUri, name, description, userName, "");
            saveRecipe(recipeToSave); // Save to Firebase
        });

        // Call method to calculate and display rating
        calculateAndDisplayRating();
    }

    private void calculateAndDisplayRating() {
        String formattedUserEmail = userEmail.replace(".", "_");

        DatabaseReference ratingsRef = FirebaseDatabase.getInstance()
                .getReference("recipes")
                .child(formattedUserEmail)
                .child(recipeId)
                .child("ratings");

        ratingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                float totalRating = 0;
                int ratingCount = 0;

                for (DataSnapshot ratingSnapshot : dataSnapshot.getChildren()) {
                    Float rating = ratingSnapshot.getValue(Float.class);
                    if (rating != null) {
                        totalRating += rating;
                        ratingCount++;
                    }
                }

                if (ratingCount > 0) {
                    float averageRating = totalRating / ratingCount;

                    // Update RatingBar
                    RatingBar ratingBar = findViewById(R.id.recipeRatingBar);
                    ratingBar.setRating(averageRating);

                    // Update TextView with formatted rating
                    totalRatingTextView.setText(String.format("%.1f (%d ratings)", averageRating, ratingCount));
                } else {
                    totalRatingTextView.setText("No ratings yet");

                    // Set RatingBar to 0 if no ratings
                    RatingBar ratingBar = findViewById(R.id.recipeRatingBar);
                    ratingBar.setRating(0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(recipeMain.this, "Failed to load ratings", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveRecipe(Datacls recipe) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please log in to save recipes.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        DatabaseReference savedRecipesRef = FirebaseDatabase.getInstance().getReference("saved_recipes").child(userId);

        savedRecipesRef.push().setValue(recipe)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Recipe saved successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save recipe: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void fetchAndShareRecipe(String recipeId) {
        recipeDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean recipeFound = false;

                // Loop through all users
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Check if the user has the recipe with the given recipeId
                    DataSnapshot recipeSnapshot = userSnapshot.child(recipeId);
                    if (recipeSnapshot.exists()) {
                        // Recipe found
                        recipeFound = true;

                        // Fetch recipe details
                        String recipeName = recipeSnapshot.child("name").getValue(String.class);
                        String description = recipeSnapshot.child("description").getValue(String.class);
                        String serves = recipeSnapshot.child("serves").getValue(String.class);
                        String category = recipeSnapshot.child("category").getValue(String.class);
                        String cookTime = recipeSnapshot.child("cookTime").getValue(String.class);

                        // Initialize ArrayLists to store ingredients and methods
                        ArrayList<String> ingredientList = new ArrayList<>();
                        ArrayList<String> methodList = new ArrayList<>();

                        // Retrieve ingredients
                        for (DataSnapshot ingredientSnapshot : recipeSnapshot.child("ingredients").getChildren()) {
                            String ingredient = ingredientSnapshot.getValue(String.class);
                            if (ingredient != null) {
                                ingredientList.add(ingredient);
                            }
                        }

                        // Retrieve cooking methods
                        for (DataSnapshot methodSnapshot : recipeSnapshot.child("methods").getChildren()) {
                            String method = methodSnapshot.getValue(String.class);
                            if (method != null) {
                                methodList.add(method);
                            }
                        }

                        // Convert ingredient list to a string for sharing
                        StringBuilder ingredientsBuilder = new StringBuilder();
                        for (String ingredient : ingredientList) {
                            ingredientsBuilder.append(ingredient).append("\n");
                        }

                        // Convert method list to a string for sharing
                        StringBuilder stepsBuilder = new StringBuilder();
                        for (String method : methodList) {
                            stepsBuilder.append(method).append("\n");
                        }
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        String message = "Check out this recipe on Goodeats app!\n\n"
                                + "Recipe Name: " + recipeName
                                + "\nDescription: " + description
                                + "\nServes: " + serves
                                + "\nCategory: " + category
                                + "\nCook Time: " + cookTime
                                + "\n\nIngredients:\n" + ingredientsBuilder
                                + "\n\nSteps:\n" + stepsBuilder.toString();

                        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
                        startActivity(Intent.createChooser(shareIntent, "Share via"));
                        break;
                    }
                }
                if (!recipeFound) {
                    Toast.makeText(recipeMain.this, "Recipe not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
                Toast.makeText(recipeMain.this, "Error fetching recipe: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }

    private void setButtonActive(Button button) {
        button.setBackgroundTintList(getResources().getColorStateList(R.color.green));
        button.setTextColor(getResources().getColor(R.color.white));
    }

    private void setButtonInactive(Button button) {
        button.setBackgroundTintList(getResources().getColorStateList(R.color.white));
        button.setTextColor(getResources().getColor(R.color.green));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Cleanup MediaController if needed
        if (recipeVideoView != null) {
            recipeVideoView.suspend();
        }
    }
}
