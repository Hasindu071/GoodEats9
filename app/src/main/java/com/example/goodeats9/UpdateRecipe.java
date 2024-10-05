package com.example.goodeats9;

import android.content.Intent;
import android.net.Uri; // Import Uri for handling images/videos
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class UpdateRecipe extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST = 2;
    private ImageView recipeImageView;
    private Uri imageUri; // Variable to hold the image URI
    private Uri videoUri; // Variable to hold the video URI

    private String existingImageUri; // Existing image URL
    private String existingVideoUri; // Existing video URL
    private String recipeID; // Recipe ID


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_recipe);

        // Initialize UI elements
        EditText titleEditText = findViewById(R.id.entername);
        EditText cookTimeEditText = findViewById(R.id.entertime);
        EditText servesEditText = findViewById(R.id.enterserves);
        EditText descriptionEditText = findViewById(R.id.enterdiscription);
        recipeImageView = findViewById(R.id.addPic);

        Button addImage = findViewById(R.id.updateImage);
        Button addVideo = findViewById(R.id.updateVideo);
        Button update = findViewById(R.id.updatebtn);

        addImage.setOnClickListener(v -> openImageChooser());
        addVideo.setOnClickListener(v -> openVideoChooser());

        // Retrieve data from Intent
        Intent intent = getIntent();
        recipeID = intent.getStringExtra("recipeID");
        String title = intent.getStringExtra("title");
        String cookTime = intent.getStringExtra("cookTime");
        String serves = intent.getStringExtra("serves");
        String description = intent.getStringExtra("description");
        existingImageUri = intent.getStringExtra("imageUri"); // Get existing image URL
        existingVideoUri = intent.getStringExtra("videoUri"); // Get existing video URL

        // Set retrieved values to EditText fields
        titleEditText.setText(title);
        cookTimeEditText.setText(cookTime);
        servesEditText.setText(serves);
        descriptionEditText.setText(description);

        // Load existing image if available
        if (existingImageUri != null) {
            Glide.with(this).load(existingImageUri).into(recipeImageView);
        }

        // Update button click listener
        update.setOnClickListener(v -> {
            deleteExistingMediaAndUploadNew(
                    titleEditText.getText().toString(),
                    cookTimeEditText.getText().toString(),
                    servesEditText.getText().toString(),
                    descriptionEditText.getText().toString()
            );
        });
    }

    private void deleteExistingMediaAndUploadNew(String title, String cookTime, String serves, String description) {
        // Get a reference to Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        // Delete existing media files if they exist
        if (existingImageUri != null) {
            StorageReference imageRef = storageRef.child("images/" + existingImageUri); // Adjust path accordingly
            imageRef.delete().addOnSuccessListener(aVoid -> {
                // Existing image deleted successfully
                uploadNewMedia(title, cookTime, serves, description);
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to delete existing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            // If no existing image, directly upload new media
            uploadNewMedia(title, cookTime, serves, description);
        }

        // Similar process for video
        if (existingVideoUri != null) {
            StorageReference videoRef = storageRef.child("videos/" + existingVideoUri); // Adjust path accordingly
            videoRef.delete().addOnSuccessListener(aVoid -> {
                // Existing video deleted successfully
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to delete existing video: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void uploadNewMedia(String title, String cookTime, String serves, String description) {
        // Get a reference to Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        HashMap<String, Object> updatedRecipe = new HashMap<>();

        // Upload new image if selected
        if (imageUri != null) {
            StorageReference imageRef = storageRef.child("images/" + recipeID + "_image"); // Adjust path accordingly
            UploadTask uploadTask = imageRef.putFile(imageUri);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                    updatedRecipe.put("imageUri", downloadUrl.toString());
                    checkForVideoUpload(updatedRecipe, title, cookTime, serves, description);
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            // No new image, check for video upload
            checkForVideoUpload(updatedRecipe, title, cookTime, serves, description);
        }

        // Upload new video if selected
        if (videoUri != null) {
            StorageReference videoRef = storageRef.child("videos/" + recipeID + "_video"); // Adjust path accordingly
            UploadTask uploadTask = videoRef.putFile(videoUri);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                videoRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                    updatedRecipe.put("videoUri", downloadUrl.toString());
                    checkForVideoUpload(updatedRecipe, title, cookTime, serves, description);
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Video upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            // No new video, update the recipe in the database
            updateRecipeInDatabase(updatedRecipe, title, cookTime, serves, description);
        }
    }

    private void checkForVideoUpload(HashMap<String, Object> updatedRecipe, String title, String cookTime, String serves, String description) {
        // Check if video was uploaded
        if (!updatedRecipe.containsKey("videoUri")) {
            // No video uploaded, update the recipe in the database
            updateRecipeInDatabase(updatedRecipe, title, cookTime, serves, description);
        }
    }

    private void updateRecipeInDatabase(HashMap<String, Object> updatedRecipe, String title, String cookTime, String serves, String description) {
        // Get a reference to the recipe in the database
        DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("recipes").child(recipeID);

        // Add other fields to the updated recipe map
        updatedRecipe.put("title", title);
        updatedRecipe.put("cookTime", cookTime);
        updatedRecipe.put("serves", serves);
        updatedRecipe.put("description", description);

        // Update the recipe in the database
        recipeRef.updateChildren(updatedRecipe).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(UpdateRecipe.this, "Recipe updated successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity
            } else {
                Toast.makeText(UpdateRecipe.this, "Failed to update recipe. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openVideoChooser() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                imageUri = data.getData(); // Store the image URI
                if (imageUri != null) {
                    // Load selected image into ImageView using Glide
                    Glide.with(this).load(imageUri).into(recipeImageView);
                }
            } else if (requestCode == PICK_VIDEO_REQUEST) {
                videoUri = data.getData(); // Store the video URI
                if (videoUri != null) {
                    // You can also display a video thumbnail if needed
                    // For example, Glide can load video thumbnails:
                    Glide.with(this)
                            .load(videoUri)
                            .thumbnail(0.1f) // Load a thumbnail of the video
                            .into(recipeImageView); // Display thumbnail in ImageView (or use a separate VideoView if you prefer)

                }
            }
        }

    }}
