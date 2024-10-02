package com.example.goodeats9;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UpdateRecipe extends AppCompatActivity {

    private EditText titleEditText, descriptionEditText, servesEditText, cookTimeEditText;
    private ImageView addPicButton;
    private Button updateRecipeButton;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String recipeId;
    private String imageUrl; // To store the image URL for the recipe

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_recipe); // Ensure this matches your layout file

        // Initialize Firebase Database and Storage references
        databaseReference = FirebaseDatabase.getInstance().getReference("recipes");
        storageReference = FirebaseStorage.getInstance().getReference("recipe_images");

        // Get the recipe ID passed from the previous activity
        recipeId = getIntent().getStringExtra("RECIPE_ID");

        // Initialize views
        titleEditText = findViewById(R.id.entername);
        descriptionEditText = findViewById(R.id.enterdiscription);
        servesEditText = findViewById(R.id.enterserves);
        cookTimeEditText = findViewById(R.id.entertime);
        addPicButton = findViewById(R.id.addPic);
        updateRecipeButton = findViewById(R.id.addrecipebtn);

        // Load existing recipe data
        loadRecipeData();

        // Set up update button click listener
        updateRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRecipe();
            }
        });

        // Set up add picture button click listener (for image selection)
        addPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private void loadRecipeData() {
        databaseReference.child(recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Recipe recipe = dataSnapshot.getValue(Recipe.class);
                    if (recipe != null) {
                        titleEditText.setText(recipe.getTitle());
                        descriptionEditText.setText(recipe.getDescription());
                        servesEditText.setText(recipe.getServes());
                        cookTimeEditText.setText(recipe.getCookTime());
                        imageUrl = recipe.getImageUrl(); // Assuming your Recipe class has an imageUrl field
                    }
                } else {
                    Toast.makeText(UpdateRecipe.this, "Recipe not found", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity if recipe not found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateRecipe.this, "Error loading recipe", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRecipe() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String serves = servesEditText.getText().toString().trim();
        String cookTime = cookTimeEditText.getText().toString().trim();

        // Validate input
        if (title.isEmpty() || description.isEmpty() || serves.isEmpty() || cookTime.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a Recipe object to hold updated data
        Recipe updatedRecipe = new Recipe(title, description, serves, cookTime, imageUrl);

        // Update the recipe in Firebase
        databaseReference.child(recipeId).setValue(updatedRecipe).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(UpdateRecipe.this, "Recipe updated successfully", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity and go back
            } else {
                Toast.makeText(UpdateRecipe.this, "Failed to update recipe", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectImage() {
        // Here you can implement the image selection logic (e.g., using an Intent to open the image gallery)
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 101); // Assuming you are using 101 as request code for image selection
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            // Upload the image to Firebase Storage and get the URL
            uploadImageToFirebase(selectedImageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        imageUrl = downloadUri.toString(); // Store the image URL
                        Toast.makeText(UpdateRecipe.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    }))
                    .addOnFailureListener(e -> Toast.makeText(UpdateRecipe.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
        }
    }
}
