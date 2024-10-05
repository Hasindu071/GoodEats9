package com.example.goodeats9;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class UpdateRecipe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Assuming you have this method set up
        setContentView(R.layout.activity_update_recipe);

        // Initialize the UI elements
        EditText titleEditText = findViewById(R.id.entername);
        EditText cookTimeEditText = findViewById(R.id.entertime);
        EditText servesEditText = findViewById(R.id.enterserves);
        EditText descriptionEditText = findViewById(R.id.enterdiscription);
        ImageView recipeImageView = findViewById(R.id.addPic);

        // Retrieve the data from the Intent
        Intent intent = getIntent();
        String recipeID = intent.getStringExtra("recipeID");
        String title = intent.getStringExtra("title");
        String imageUri = intent.getStringExtra("Image");
        String cookTime = intent.getStringExtra("cookTime");
        String serves = intent.getStringExtra("serves");
        String videoUri = intent.getStringExtra("video");
        String description = intent.getStringExtra("description");

        // Set the retrieved values to the EditText fields and ImageView
        titleEditText.setText(title);
        cookTimeEditText.setText(cookTime);
        servesEditText.setText(serves);
        descriptionEditText.setText(description);

        // Retrieve the lists
        ArrayList<String> stepList = intent.getStringArrayListExtra("stepList");
        ArrayList<String> ingredientList = intent.getStringArrayListExtra("ingredientList");

        // Use Glide or any image loading library to set the image in the ImageView
        Glide.with(this).load(imageUri).into(recipeImageView);

        // Ensure lists are not null
        if (stepList == null) {
            stepList = new ArrayList<>(); // Initialize an empty list if null
        }
        if (ingredientList == null) {
            ingredientList = new ArrayList<>(); // Initialize an empty list if null
        }

        // Example: Log the steps and ingredients (optional debugging)
        for (String step : stepList) {
            System.out.println("Step: " + step);
        }

        for (String ingredient : ingredientList) {
            System.out.println("Ingredient: " + ingredient);
        }

        // Initialize ListViews
        ListView stepListView = findViewById(R.id.stepListView);
        ListView ingredientListView = findViewById(R.id.ingredientListView);

        // Set up adapter for the steps and ingredients
        ArrayAdapter<String> stepAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stepList);
        ArrayAdapter<String> ingredientAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ingredientList);

        // Set the adapter to the ListViews
        stepListView.setAdapter(stepAdapter);
        ingredientListView.setAdapter(ingredientAdapter);
    }
}