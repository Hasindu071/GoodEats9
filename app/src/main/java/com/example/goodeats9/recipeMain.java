package com.example.goodeats9;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

        // Handle star ImageView click
        starImage.setOnClickListener(v -> {
            // Navigate to the RatingActivity when the star is clicked
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
        // Handle Reviews TextView click
        textViewReviews.setOnClickListener(v -> {
            Intent intent = new Intent(recipeMain.this, Reviews.class);
            startActivity(intent);
        });

        // Initially set the Procedure button as the selected one
        setButtonActive(procedureButton);
        setButtonInactive(ingredientsButton);

        // Load the Procedure fragment by default
        loadFragment(new ProcedureFragment());

        // Procedure Button Click Listener
        procedureButton.setOnClickListener(view -> {
            setButtonActive(procedureButton);
            setButtonInactive(ingredientsButton);
            loadFragment(new ProcedureFragment());
        });

        // Ingredients Button Click Listener
        ingredientsButton.setOnClickListener(view -> {
            setButtonActive(ingredientsButton);
            setButtonInactive(procedureButton);
            loadFragment(new IngredientsFragment());
        });

        // Set click listener for shareImage
        shareImage.setOnClickListener(v -> fetchAndShareRecipe());
    }

    // Fetch recipe details from Firebase and share them
    private void fetchAndShareRecipe() {
        String userId = "tharushikahirushani@gmail_com";  // User's ID
        String recipeId = "-O81PlObeCLLFiYCY8oH"; // Recipe ID

        // Fetch data from Firebase
        recipeDatabaseReference.child(userId).child(recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve the recipe details from the snapshot
                    String cookTime = dataSnapshot.child("cookTime").getValue(String.class);
                    String recipeDescription = dataSnapshot.child("description").getValue(String.class);
                    String ingredients = dataSnapshot.child("ingredients/0").getValue(String.class); // Assuming there's a list
                    String methodName = dataSnapshot.child("methods/name").getValue(String.class);
                    String serves = dataSnapshot.child("methods/serves").getValue(String.class);

                    // Create the shareable text
                    String recipeDetails = "Check out this amazing recipe!\n\n" +
                            "Recipe: " + recipeDescription + "\n" +
                            "Cook Time: " + cookTime + "\n" +
                            "Serves: " + serves + "\n" +
                            "Ingredients:\n" + ingredients + "\n" +
                            "Instructions: " + methodName;

                    // Share the recipe using an Intent
                    shareRecipe(recipeDetails);
                } else {
                    Toast.makeText(recipeMain.this, "Recipe not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
                Toast.makeText(recipeMain.this, "Failed to load recipe", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Share recipe details via an Intent
    private void shareRecipe(String recipeDetails) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, recipeDetails);  // Use the fetched recipe details
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "Share recipe via");
        startActivity(shareIntent);
    }

    private void setButtonActive(Button button) {
        button.setBackgroundTintList(getResources().getColorStateList(R.color.green));
        button.setTextColor(Color.WHITE);
    }

    @SuppressLint("ResourceAsColor")
    private void setButtonInactive(Button button) {
        button.setBackgroundTintList(getResources().getColorStateList(R.color.white));
        button.setTextColor(getResources().getColor(R.color.green));
    }

    // Method to load the selected fragment
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }
}
