package com.example.goodeats9;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class recipeMain extends AppCompatActivity {

    private Button procedureButton;
    private Button ingredientsButton;
    TextView recipeNameText,DescriptionText,ServesText,CookTimeText,UserNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_main);

        //Get the intent passed by the previous page
        Intent intentRecipe = getIntent();
        String imageUri = intentRecipe.getStringExtra("imageUri");
        String name = intentRecipe.getStringExtra("name");
        String description = intentRecipe.getStringExtra("description");
        String serves = intentRecipe.getStringExtra("serves");
        String cookTime = intentRecipe.getStringExtra("cookTime");
        String userName = intentRecipe.getStringExtra("username");

        recipeNameText = findViewById(R.id.recipeName);
        recipeNameText.setText(name);

        DescriptionText = findViewById(R.id.Recipedescription);
        DescriptionText.setText(description);

        UserNameText = findViewById(R.id.profileName);
        UserNameText.setText(userName);

        //Get the text of reviews by its ID
        TextView textViewReviews = findViewById(R.id.reviews);
        //Make the "reviews" text look like a link
        textViewReviews.setPaintFlags(textViewReviews.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        // Handle Reviews TextView click
        textViewReviews.setOnClickListener(v -> {
            Intent intent = new Intent(recipeMain.this, Reviews.class);
            startActivity(intent);

        });

        // Find buttons by their ID
        procedureButton = findViewById(R.id.procedureButton);
        ingredientsButton = findViewById(R.id.ingredientsButton);

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